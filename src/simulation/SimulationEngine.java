package simulation;

import model.*;
import service.*;
import java.util.*;

/**
 * The main simulation engine that manages the progression of village life over time.
 * Handles population dynamics, marriages, births, deaths, and player succession.
 */
public class SimulationEngine {
    private final Village managedVillage;
    private final NameGenerator nameGenerationService;
    private final DemographicsService demographicsCalculator;
    private final Random randomNumberGenerator;
    
    private Person currentPlayerCharacter;
    private int currentSimulationYear;
    private final List<List<SimulationEvent>> yearlyEventHistory;
    
    // Configuration
    private final SimulationConfig simulationConfiguration;
    
    // Statistics tracking
    private int totalBirthsCount = 0;
    private int totalDeathsCount = 0;
    private int totalMarriagesCount = 0;
    private int totalOutsiderArrivalsCount = 0;
    
    // Constants for outsider generation
    private static final int INITIAL_COUPLES_ADDED_AS_OUTSIDERS = 2; // Per couple
    
    public SimulationEngine(Village village, SimulationConfig config) {
        this.managedVillage = Objects.requireNonNull(village, "Village cannot be null");
        this.simulationConfiguration = Objects.requireNonNull(config, "Configuration cannot be null");
        this.randomNumberGenerator = new Random();
        this.nameGenerationService = new NameGenerator(randomNumberGenerator);
        this.demographicsCalculator = new DemographicsService(randomNumberGenerator, config);
        this.currentSimulationYear = 0;
        this.yearlyEventHistory = new ArrayList<>();
    }
    
    /**
     * Sets the initial player character and adds them to the village.
     * 
     * @param playerCharacter The starting player character
     */
    public void setInitialPlayer(Person playerCharacter) {
        this.currentPlayerCharacter = Objects.requireNonNull(playerCharacter, "Player character cannot be null");
        managedVillage.addVillager(playerCharacter);
        yearlyEventHistory.add(new ArrayList<>()); // Initialize year 0 events list
    }
    
    /**
     * Initializes the starting population with the specified number of married couples.
     * These couples represent the founding families of the settlement.
     * 
     * @param numberOfCouples Number of married couples to create
     */
    public void initializeStartingPopulation(int numberOfCouples) {
        if (numberOfCouples <= 0) return;
        
        // Validate against configured limits
        if (numberOfCouples < simulationConfiguration.getMinimumStartingCouples() || 
            numberOfCouples > simulationConfiguration.getMaximumStartingCouples()) {
            throw new IllegalArgumentException(
                String.format("Number of couples must be between %d and %d",
                    simulationConfiguration.getMinimumStartingCouples(),
                    simulationConfiguration.getMaximumStartingCouples()));
        }
        
        List<SimulationEvent> initializationEvents = new ArrayList<>();
        List<Person> foundingMales = new ArrayList<>();
        List<Person> foundingFemales = new ArrayList<>();
        
        // Generate founding couples with varied characteristics
        for (int coupleIndex = 0; coupleIndex < numberOfCouples; coupleIndex++) {
            // Create male founding member
            String maleName = nameGenerationService.generateName(Person.Sex.MALE);
            String maleOccupation = nameGenerationService.generateOccupation(Person.Sex.MALE);
            int maleAge = simulationConfiguration.getInitialPopulationMinAge() + 
                         randomNumberGenerator.nextInt(simulationConfiguration.getInitialPopulationAgeRange());
            Person foundingMale = new Person(maleName, maleAge, Person.Sex.MALE, true, maleOccupation);
            foundingMales.add(foundingMale);
            managedVillage.addVillager(foundingMale);
            
            // Create female founding member
            String femaleName = nameGenerationService.generateName(Person.Sex.FEMALE);
            String femaleOccupation = nameGenerationService.generateOccupation(Person.Sex.FEMALE);
            int femaleAge = simulationConfiguration.getInitialPopulationMinAge() + 
                           randomNumberGenerator.nextInt(simulationConfiguration.getInitialPopulationAgeRange());
            Person foundingFemale = new Person(femaleName, femaleAge, Person.Sex.FEMALE, true, femaleOccupation);
            foundingFemales.add(foundingFemale);
            managedVillage.addVillager(foundingFemale);
            
            // Record arrival events
            initializationEvents.add(SimulationEvent.outsiderArrival(
                foundingMale, 0, "Founding settler - initial population"));
            initializationEvents.add(SimulationEvent.outsiderArrival(
                foundingFemale, 0, "Founding settler - initial population"));
                
            totalOutsiderArrivalsCount += INITIAL_COUPLES_ADDED_AS_OUTSIDERS;
        }
        
        // Randomly pair founding members for marriage
        Collections.shuffle(foundingMales, randomNumberGenerator);
        Collections.shuffle(foundingFemales, randomNumberGenerator);
        
        for (int coupleIndex = 0; coupleIndex < numberOfCouples; coupleIndex++) {
            Person male = foundingMales.get(coupleIndex);
            Person female = foundingFemales.get(coupleIndex);
            try {
                male.marry(female);
                initializationEvents.add(SimulationEvent.marriage(male, female, 0));
                totalMarriagesCount++;
            } catch (IllegalStateException | IllegalArgumentException e) {
                // Log marriage failure but continue initialization
                System.err.println("Warning: Failed to marry founding couple - " + e.getMessage());
            }
        }
        
        // Record all initialization events
        if (!yearlyEventHistory.isEmpty()) {
            yearlyEventHistory.get(0).addAll(initializationEvents);
        }
    }
    
    /**
     * Processes one complete year of simulation, handling all demographic events.
     * 
     * @return SimulationResult indicating whether simulation should continue
     */
    public SimulationResult processSimulationYear() {
        // Check termination conditions before processing
        if (currentSimulationYear >= simulationConfiguration.getMaximumSimulationYears()) {
            return SimulationResult.ended("Maximum simulation years reached");
        }
        
        if (currentPlayerCharacter == null || !currentPlayerCharacter.isAlive()) {
            return SimulationResult.ended("No living player character or heir");
        }
        
        // Advance to next year
        currentSimulationYear++;
        
        List<SimulationEvent> currentYearEvents = new ArrayList<>();
        
        // Process demographic events in logical order
        processAnnualAging(currentYearEvents);
        processMortalityEvents(currentYearEvents);
        processMarriageFormation(currentYearEvents);
        processBirthEvents(currentYearEvents);
        
        // Clean up deceased villagers from village registry
        managedVillage.removeDeceased();
        
        // Store this year's events in history
        yearlyEventHistory.add(currentYearEvents);
        
        // Check if simulation should continue
        if (currentPlayerCharacter == null || !currentPlayerCharacter.isAlive()) {
            return SimulationResult.ended("Player lineage ended - no living heir");
        }
        
        return SimulationResult.continued(currentYearEvents);
    }
    
    /**
     * Processes aging for all villagers (happens at start of year).
     */
    private void processAnnualAging(List<SimulationEvent> yearEvents) {
        List<Person> allVillagers = new ArrayList<>(managedVillage.getAllVillagers());
        for (Person villager : allVillagers) {
            if (villager.isAlive()) {
                villager.ageOneYear();
            }
        }
    }
    
    /**
     * Processes mortality events based on age and demographics.
     */
    private void processMortalityEvents(List<SimulationEvent> yearEvents) {
        List<Person> livingVillagers = new ArrayList<>(managedVillage.getLivingVillagers());
        
        for (Person villager : livingVillagers) {
            if (demographicsCalculator.shouldPersonDie(villager)) {
                boolean wasPlayerCharacter = (villager == currentPlayerCharacter);
                villager.die();
                totalDeathsCount++;
                
                yearEvents.add(SimulationEvent.death(villager, currentSimulationYear, wasPlayerCharacter));
                
                if (wasPlayerCharacter) {
                    handlePlayerSuccession(yearEvents);
                }
            }
        }
    }
    
    /**
     * Handles succession when the player character dies.
     */
    private void handlePlayerSuccession(List<SimulationEvent> yearEvents) {
        Person successor = null;
        
        // Primary succession: First living child
        if (currentPlayerCharacter.hasChildren()) {
            for (Person child : currentPlayerCharacter.getChildren()) {
                if (child.isAlive()) {
                    successor = child;
                    break;
                }
            }
        }
        
        // Secondary succession: Could be extended to siblings, spouse, etc.
        // Currently, simulation ends if no direct heir
        
        if (successor != null) {
            currentPlayerCharacter = successor;
            yearEvents.add(SimulationEvent.playerChange(successor, currentSimulationYear));
        } else {
            currentPlayerCharacter = null;
            yearEvents.add(SimulationEvent.simulationEnd(
                "No eligible heir found for succession", currentSimulationYear));
        }
    }
    
    /**
     * Processes marriage formation for eligible villagers.
     */
    private void processMarriageFormation(List<SimulationEvent> yearEvents) {
        List<Person> marriageCandidates = managedVillage.getLivingVillagers().stream()
            .filter(Person::isEligibleForMarriage)
            .toList();
        
        // Process marriages with oldest candidates first (more realistic)
        List<Person> sortedCandidates = new ArrayList<>(marriageCandidates);
        sortedCandidates.sort(Comparator.comparingInt(Person::getAge).reversed());
        
        for (Person candidate : sortedCandidates) {
            if (!candidate.isEligibleForMarriage()) continue; // May have married earlier in loop
            
            Person potentialSpouse = findOrGenerateSpouse(candidate, yearEvents);
            if (potentialSpouse != null) {
                try {
                    candidate.marry(potentialSpouse);
                    yearEvents.add(SimulationEvent.marriage(candidate, potentialSpouse, currentSimulationYear));
                    totalMarriagesCount++;
                } catch (IllegalStateException | IllegalArgumentException e) {
                    // Marriage failed - log but continue
                    System.err.println("Warning: Marriage failed - " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Finds an eligible spouse from the village or generates an outsider if needed.
     * Includes detailed reasoning for outsider generation.
     */
    private Person findOrGenerateSpouse(Person seekingSpouse, List<SimulationEvent> yearEvents) {
        List<Person> eligiblePartners = managedVillage.findEligibleSpouses(seekingSpouse);
        
        if (!eligiblePartners.isEmpty()) {
            // Select partner from existing villagers
            return eligiblePartners.get(randomNumberGenerator.nextInt(eligiblePartners.size()));
        } else if (seekingSpouse.getAge() >= simulationConfiguration.getOutsiderMarriageMinAge() && 
                   Math.random() < simulationConfiguration.getOutsiderMarriageThreshold()) {
            // Generate outsider spouse if person is older and no local options
            Person.Sex requiredSex = seekingSpouse.getSex().getOpposite();
            String spouseName = nameGenerationService.generateName(requiredSex);
            String spouseOccupation = nameGenerationService.generateOccupation(requiredSex);
            int spouseAge = demographicsCalculator.generateSpouseAge();
            
            Person outsiderSpouse = new Person(spouseName, spouseAge, requiredSex, true, spouseOccupation);
            managedVillage.addVillager(outsiderSpouse);
            
            // Analyze and document why outsider was needed
            String outsiderReason = analyzeOutsiderNecessity(seekingSpouse);
            yearEvents.add(SimulationEvent.outsiderArrival(outsiderSpouse, currentSimulationYear, outsiderReason));
            totalOutsiderArrivalsCount++;
            
            return outsiderSpouse;
        }
        
        return null; // No marriage this year
    }
    
    /**
     * Analyzes and explains why an outsider spouse was necessary.
     */
    private String analyzeOutsiderNecessity(Person seekingSpouse) {
        Person.Sex requiredSex = seekingSpouse.getSex().getOpposite();
        List<Person> allPotentialPartners = managedVillage.getLivingVillagers().stream()
            .filter(p -> p.getSex() == requiredSex)
            .filter(p -> p.getAge() >= simulationConfiguration.getMinimumMarriageAge() && 
                        p.getAge() <= simulationConfiguration.getMaximumMarriageAge())
            .toList();
        
        if (allPotentialPartners.isEmpty()) {
            return String.format("No %s of marriageable age in village", 
                requiredSex.toString().toLowerCase() + "s");
        }
        
        // Categorize reasons for unavailability
        int alreadyMarriedCount = (int) allPotentialPartners.stream()
            .filter(p -> p.getMarriedTo() != null).count();
        int closeRelativesCount = (int) allPotentialPartners.stream()
            .filter(p -> managedVillage.areCloseRelatives(seekingSpouse, p)).count();
        int hasChildrenCount = (int) allPotentialPartners.stream()
            .filter(Person::hasChildren).count();
        
        List<String> reasons = new ArrayList<>();
        if (alreadyMarriedCount > 0) {
            reasons.add(String.format("%d already married", alreadyMarriedCount));
        }
        if (closeRelativesCount > 0) {
            reasons.add(String.format("%d are close relatives (marriage prohibited)", closeRelativesCount));
        }
        if (hasChildrenCount > 0) {
            reasons.add(String.format("%d have children (ineligible)", hasChildrenCount));
        }
        
        if (reasons.isEmpty()) {
            return "All potential partners unavailable";
        }
        
        return String.join("; ", reasons);
    }
    
    /**
     * Processes birth events for married couples.
     */
    private void processBirthEvents(List<SimulationEvent> yearEvents) {
        List<Person> potentialFathers = managedVillage.getLivingVillagers().stream()
            .filter(p -> p.getSex() == Person.Sex.MALE)
            .filter(p -> p.getMarriedTo() != null)
            .filter(p -> p.canHaveMoreChildren())
            .toList();
        
        for (Person father : potentialFathers) {
            Person mother = father.getMarriedTo();
            if (mother == null || !mother.isAlive() || !mother.canHaveMoreChildren()) continue;
            
            // Special rule: Player lineage may have different child limit for gameplay balance
            boolean isPlayerLineage = (father == currentPlayerCharacter || 
                                     father.getChildren().contains(currentPlayerCharacter));
            int lineageChildLimit = isPlayerLineage ? 
                simulationConfiguration.getPlayerLineageChildLimit() : 
                simulationConfiguration.getMaximumChildrenPerFamily();
            
            if (father.getChildren().size() < lineageChildLimit && 
                demographicsCalculator.shouldChildBeBorn(father, mother)) {
                
                Person newChild = generateChild(father, mother);
                yearEvents.add(SimulationEvent.birth(newChild, father, mother, currentSimulationYear));
                totalBirthsCount++;
            }
        }
    }
    
    /**
     * Generates a new child for the given parents.
     */
    private Person generateChild(Person father, Person mother) {
        Person.Sex childSex = demographicsCalculator.generateChildSex();
        String childName = nameGenerationService.generateName(childSex);
        
        Person child = new Person(childName, 0, childSex, false, "None");
        child.setParents(mother, father);
        father.addChild(child);
        managedVillage.addVillager(child);
        
        return child;
    }
    
    // Public accessors for simulation state
    public Person getCurrentPlayer() { 
        return currentPlayerCharacter; 
    }
    
    public int getCurrentYear() { 
        return currentSimulationYear; 
    }
    
    public Village getVillage() { 
        return managedVillage; 
    }
    
    public SimulationConfig getConfiguration() {
        return simulationConfiguration;
    }
    
    public List<SimulationEvent> getEventsForYear(int year) {
        if (year >= 0 && year < yearlyEventHistory.size()) {
            return new ArrayList<>(yearlyEventHistory.get(year));
        }
        return new ArrayList<>();
    }
    
    // Statistics accessors
    public int getTotalBirths() { return totalBirthsCount; }
    public int getTotalDeaths() { return totalDeathsCount; }
    public int getTotalMarriages() { return totalMarriagesCount; }
    public int getTotalOutsiderArrivals() { return totalOutsiderArrivalsCount; }
}
