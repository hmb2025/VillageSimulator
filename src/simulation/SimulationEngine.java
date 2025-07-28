package simulation;

import model.*;
import service.*;
import java.util.*;

/**
 * The main simulation engine that handles the yearly progression of the village.
 */
public class SimulationEngine {
    private final Village village;
    private final NameGenerator nameGenerator;
    private final DemographicsService demographicsService;
    private final Random random;
    
    private Person currentPlayer;
    private int currentYear;
    private final List<List<SimulationEvent>> yearlyEvents;
    
    // Configuration
    private final SimulationConfig config;
    
    public SimulationEngine(Village village, SimulationConfig config) {
        this.village = Objects.requireNonNull(village);
        this.config = Objects.requireNonNull(config);
        this.random = new Random();
        this.nameGenerator = new NameGenerator(random);
        this.demographicsService = new DemographicsService(random);
        this.currentYear = 0;
        this.yearlyEvents = new ArrayList<>();
    }
    
    /**
     * Sets the initial player character.
     */
    public void setInitialPlayer(Person player) {
        this.currentPlayer = Objects.requireNonNull(player);
        village.addVillager(player);
        yearlyEvents.add(new ArrayList<>());
    }
    
    /**
     * Simulates one year in the village.
     */
    public SimulationResult simulateYear() {
        if (currentYear >= config.getMaxYears() || currentPlayer == null || !currentPlayer.isAlive()) {
            return SimulationResult.ended("Simulation ended");
        }
        
        List<SimulationEvent> events = new ArrayList<>();
        
        // Process deaths and aging
        processAgingAndDeaths(events);
        
        // Process marriages
        processMarriages(events);
        
        // Process births
        processBirths(events);
        
        // Clean up deceased villagers
        village.removeDeceased();
        
        // Store events and increment year
        yearlyEvents.add(events);
        currentYear++;
        
        return SimulationResult.continued(events);
    }
    
    /**
     * Processes aging and potential deaths for all villagers.
     */
    private void processAgingAndDeaths(List<SimulationEvent> events) {
        List<Person> villagersCopy = new ArrayList<>(village.getAllVillagers());
        
        for (Person person : villagersCopy) {
            if (!person.isAlive()) continue;
            
            person.ageOneYear();
            
            if (demographicsService.shouldPersonDie(person)) {
                boolean wasPlayer = (person == currentPlayer);
                person.die();
                
                events.add(SimulationEvent.death(person, currentYear, wasPlayer));
                
                if (wasPlayer) {
                    handlePlayerDeath(events);
                }
            }
        }
    }
    
    /**
     * Handles the death of the current player character.
     */
    private void handlePlayerDeath(List<SimulationEvent> events) {
        // Try to find an heir (first child)
        Person heir = null;
        if (currentPlayer.hasChildren()) {
            for (Person child : currentPlayer.getChildren()) {
                if (child.isAlive()) {
                    heir = child;
                    break;
                }
            }
        }
        
        if (heir != null) {
            currentPlayer = heir;
            events.add(SimulationEvent.playerChange(heir, currentYear));
        } else {
            currentPlayer = null;
            events.add(SimulationEvent.simulationEnd("No heir found. Simulation ends.", currentYear));
        }
    }
    
    /**
     * Processes potential marriages for eligible villagers.
     */
    private void processMarriages(List<SimulationEvent> events) {
        List<Person> eligibleVillagers = village.getLivingVillagers().stream()
            .filter(Person::isEligibleForMarriage)
            .filter(p -> demographicsService.shouldMarriageOccur(config.getMarriageChance()))
            .toList();
        
        for (Person person : eligibleVillagers) {
            if (!person.isEligibleForMarriage()) continue; // Could have married already this year
            
            Person spouse = findOrCreateSpouse(person, events);
            if (spouse != null) {
                try {
                    person.marry(spouse);
                    events.add(SimulationEvent.marriage(person, spouse, currentYear));
                } catch (IllegalStateException | IllegalArgumentException e) {
                    // Marriage failed, continue
                }
            }
        }
    }
    
    /**
     * Finds an eligible spouse from the village or creates a new one from outside.
     * Now tracks why outsiders are created.
     */
    private Person findOrCreateSpouse(Person person, List<SimulationEvent> events) {
        List<Person> candidates = village.findEligibleSpouses(person);
        
        if (!candidates.isEmpty()) {
            // Marry someone from the village
            return candidates.get(random.nextInt(candidates.size()));
        } else {
            // Create a new spouse from outside the village
            Person.Sex spouseSex = person.getSex().getOpposite();
            String spouseName = nameGenerator.generateName(spouseSex);
            String occupation = nameGenerator.generateOccupation(spouseSex);
            int spouseAge = demographicsService.generateSpouseAge();
            
            Person spouse = new Person(spouseName, spouseAge, spouseSex, true, occupation);
            village.addVillager(spouse);
            
            // Log why an outsider was needed
            String reason = analyzeWhyOutsiderNeeded(person);
            events.add(SimulationEvent.outsiderArrival(spouse, currentYear, reason));
            
            return spouse;
        }
    }
    
    /**
     * Analyzes why an outsider spouse was needed.
     */
    private String analyzeWhyOutsiderNeeded(Person person) {
        List<Person> allEligible = village.getLivingVillagers().stream()
            .filter(p -> p.getSex() == person.getSex().getOpposite())
            .filter(p -> p.getAge() >= Person.MINIMUM_MARRIAGE_AGE && p.getAge() <= Person.MAXIMUM_MARRIAGE_AGE)
            .toList();
        
        if (allEligible.isEmpty()) {
            return "No eligible " + person.getSex().getOpposite().toString().toLowerCase() + "s in village";
        }
        
        int married = (int) allEligible.stream().filter(p -> p.getMarriedTo() != null).count();
        int relatives = (int) allEligible.stream().filter(p -> village.areCloseRelatives(person, p)).count();
        int hasChildren = (int) allEligible.stream().filter(Person::hasChildren).count();
        
        List<String> reasons = new ArrayList<>();
        if (married > 0) reasons.add(married + " already married");
        if (relatives > 0) reasons.add(relatives + " are close relatives");
        if (hasChildren > 0) reasons.add(hasChildren + " already have children");
        
        return String.join(", ", reasons);
    }
    
    /**
     * Processes potential births for married couples.
     */
    private void processBirths(List<SimulationEvent> events) {
        List<Person> marriedMales = village.getLivingVillagers().stream()
            .filter(p -> p.getSex() == Person.Sex.MALE)
            .filter(p -> p.getMarriedTo() != null)
            .filter(p -> p.canHaveMoreChildren())
            .toList();
        
        for (Person father : marriedMales) {
            Person mother = father.getMarriedTo();
            if (mother == null || !mother.isAlive()) continue;
            
            // Special rule: Player lineage can only have 1 child
            boolean isPlayerLineage = (father == currentPlayer || father.getChildren().contains(currentPlayer));
            int maxChildren = isPlayerLineage ? 1 : Person.MAXIMUM_CHILDREN;
            
            if (father.getChildren().size() < maxChildren && 
                demographicsService.shouldChildBeBorn(father, mother)) {
                
                Person child = createChild(father, mother);
                events.add(SimulationEvent.birth(child, father, mother, currentYear));
            }
        }
    }
    
    /**
     * Creates a new child for the given parents.
     */
    private Person createChild(Person father, Person mother) {
        Person.Sex childSex = demographicsService.generateChildSex();
        String childName = nameGenerator.generateName(childSex);
        
        Person child = new Person(childName, 0, childSex, false, "None");
        child.setParents(mother, father);
        father.addChild(child);
        village.addVillager(child);
        
        return child;
    }
    
    // Getters
    public Person getCurrentPlayer() { return currentPlayer; }
    public int getCurrentYear() { return currentYear; }
    public Village getVillage() { return village; }
    public List<SimulationEvent> getEventsForYear(int year) {
        if (year >= 0 && year < yearlyEvents.size()) {
            return new ArrayList<>(yearlyEvents.get(year));
        }
        return new ArrayList<>();
    }
}
