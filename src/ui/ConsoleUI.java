package ui;

import model.*;
import simulation.SimulationEngine;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Console-based user interface for the village simulation.
 * Now outputs to a file instead of console for better tracking.
 */
public class ConsoleUI {
    private final SimulationEngine engine;
    private final Scanner scanner;
    private final PrintWriter fileWriter;
    private final PrintStream console;
    
    // Display constants
    private static final String YEAR_SEPARATOR = "========== Year %d ==========";
    private static final String SECTION_SEPARATOR = "--------------------------------";
    private static final String SUB_SECTION_SEPARATOR = "........................";
    
    public ConsoleUI(SimulationEngine engine, Scanner scanner) throws IOException {
        this.engine = engine;
        this.scanner = scanner;
        this.console = System.out;
        
        // Create output file with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("village_simulation_%s.txt", timestamp);
        this.fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        
        console.println("Output will be written to: " + filename);
        writeLine("Village Simulation Log");
        writeLine("Started: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        writeLine(SECTION_SEPARATOR);
    }
    
    /**
     * Gets the initial player name from the user.
     */
    public String getPlayerName() {
        console.print("Enter starting player name (male): ");
        String name = scanner.nextLine();
        writeLine("Player name: " + name);
        return name;
    }
    
    /**
     * Gets the number of additional couples to start with.
     */
    public int getNumberOfAdditionalCouples() {
        int couples = 0;
        boolean validInput = false;
        
        while (!validInput) {
            console.print("Enter number of additional starting couples (0-10): ");
            try {
                couples = Integer.parseInt(scanner.nextLine());
                if (couples >= 0 && couples <= 10) {
                    validInput = true;
                } else {
                    console.println("Please enter a number between 0 and 10.");
                }
            } catch (NumberFormatException e) {
                console.println("Please enter a valid number.");
            }
        }
        
        writeLine("Additional starting couples: " + couples);
        return couples;
    }
    
    /**
     * Displays the yearly summary to the file and console summary.
     * Fixed to display the current year's events, not the previous year's.
     */
    public void displayYearSummary() {
        int year = engine.getCurrentYear(); // Display current year
        Village village = engine.getVillage();
        List<SimulationEvent> events = engine.getEventsForYear(year);
        
        writeLine("");
        writeLine(String.format(YEAR_SEPARATOR, year));
        
        // Display events first (as they happened during this year)
        displayEvents(events);
        
        // Display population statistics (after events)
        displayPopulationStatistics(village);
        
        // Display all villagers with detailed info
        displayAllVillagers(village);
        
        // Display families with improved formatting
        displayFamilies(village);
        
        // Display relationship analysis
        displayRelationshipAnalysis(village);
        
        writeLine(SECTION_SEPARATOR);
        
        // Console summary
        console.printf("Year %d completed. Population: %d%n", year, village.getPopulation());
    }
    
    /**
     * Displays detailed population statistics.
     */
    private void displayPopulationStatistics(Village village) {
        writeLine("");
        writeLine("Population Statistics:");
        
        List<Person> living = village.getLivingVillagers();
        writeLine(String.format("  Total Population: %d", living.size()));
        
        // Count by origin
        long natives = living.stream().filter(p -> !p.isBornOutsideVillage()).count();
        long outsiders = living.stream().filter(Person::isBornOutsideVillage).count();
        writeLine(String.format("  Natives: %d", natives));
        writeLine(String.format("  Outsiders: %d", outsiders));
        
        // Count by sex
        long males = living.stream().filter(p -> p.getSex() == Person.Sex.MALE).count();
        long females = living.stream().filter(p -> p.getSex() == Person.Sex.FEMALE).count();
        writeLine(String.format("  Males: %d, Females: %d", males, females));
        
        // Age statistics
        if (!living.isEmpty()) {
            IntSummaryStatistics ageStats = living.stream()
                .mapToInt(Person::getAge)
                .summaryStatistics();
            writeLine(String.format("  Age Range: %d - %d (Average: %.1f)", 
                ageStats.getMin(), ageStats.getMax(), ageStats.getAverage()));
        }
        
        // Marriage statistics
        long married = living.stream().filter(p -> p.getMarriedTo() != null).count();
        writeLine(String.format("  Married: %d (%.1f%%)", married, 
            living.isEmpty() ? 0 : (married * 100.0) / living.size()));
    }
    
    /**
     * Displays the events that occurred during the year.
     */
    private void displayEvents(List<SimulationEvent> events) {
        writeLine("");
        writeLine("Events This Year:");
        if (events.isEmpty()) {
            writeLine("  None");
        } else {
            // Group events by type
            Map<String, List<SimulationEvent>> eventsByType = events.stream()
                .collect(Collectors.groupingBy(e -> e.getDescription().split(":")[0]));
            
            for (Map.Entry<String, List<SimulationEvent>> entry : eventsByType.entrySet()) {
                writeLine(String.format("  %s (%d):", entry.getKey(), entry.getValue().size()));
                for (SimulationEvent event : entry.getValue()) {
                    writeLine("    - " + event.getDescription());
                }
            }
        }
    }
    
    /**
     * Displays all villagers with detailed information.
     */
    private void displayAllVillagers(Village village) {
        writeLine("");
        writeLine("All Living Villagers:");
        
        List<Person> villagers = village.getLivingVillagers();
        villagers.sort(Comparator.comparing(Person::getAge).reversed()
            .thenComparing(Person::getName));
        
        for (Person person : villagers) {
            String playerMarker = person == engine.getCurrentPlayer() ? " [PLAYER]" : "";
            String marriageInfo = person.getMarriedTo() != null ? 
                String.format(", Married to: %s", person.getMarriedTo().getName()) : ", Single";
            String childrenInfo = person.hasChildren() ? 
                String.format(", Children: %d", person.getChildren().size()) : "";
            
            writeLine(String.format("  %s (%d, %s, %s, %s)%s%s%s",
                person.getName(),
                person.getAge(),
                person.getSex(),
                person.getOccupation(),
                person.getOriginStatus(),
                playerMarker,
                marriageInfo,
                childrenInfo                
            ));
            
            // Show parents for natives or if data exists
            if (!person.isBornOutsideVillage() || person.getMother() != null || person.getFather() != null) {
                StringBuilder parentInfo = new StringBuilder("    Parents: ");
                if (person.getMother() != null) {
                    parentInfo.append("Mother: ").append(person.getMother().getName());
                }
                if (person.getFather() != null) {
                    if (person.getMother() != null) parentInfo.append(", ");
                    parentInfo.append("Father: ").append(person.getFather().getName());
                }
                if (person.getMother() == null && person.getFather() == null && !person.isBornOutsideVillage()) {
                    parentInfo.append("Unknown (data missing)");
                }
                writeLine(parentInfo.toString());
            }
        }
    }
    
    /**
     * Displays all families in the village with improved formatting.
     */
    private void displayFamilies(Village village) {
        writeLine("");
        List<Family> families = village.getFamilies();
        writeLine(String.format("Families (Total: %d):", families.size()));
        
        if (families.isEmpty()) {
            writeLine("  None");
        } else {
            int familyNumber = 1;
            for (Family family : families) {
                writeLine(String.format("Family %d:", familyNumber++));
                writeLine(family.formatDetailed(engine.getCurrentPlayer()));
                writeLine(SUB_SECTION_SEPARATOR);
            }
        }
    }
    
    /**
     * Displays relationship analysis to help trace outsider influx.
     */
    private void displayRelationshipAnalysis(Village village) {
        writeLine("");
        writeLine("Relationship Analysis:");
        
        List<Person> living = village.getLivingVillagers();
        
        // Analyze marriages
        List<Person> marriedOutsiders = living.stream()
            .filter(p -> p.isBornOutsideVillage() && p.getMarriedTo() != null)
            .collect(Collectors.toList());
        
        writeLine(String.format("  Outsiders who married into village: %d", marriedOutsiders.size()));
        for (Person outsider : marriedOutsiders) {
            Person spouse = outsider.getMarriedTo();
            String spouseOrigin = spouse.isBornOutsideVillage() ? "outsider" : "native";
            writeLine(String.format("    - %s married to %s (%s)",
                outsider.getName(), spouse.getName(), spouseOrigin));
        }
        
        // Analyze unmarried eligible villagers
        List<Person> eligibleForMarriage = living.stream()
            .filter(Person::isEligibleForMarriage)
            .collect(Collectors.toList());
        
        writeLine(String.format("  Eligible for marriage: %d", eligibleForMarriage.size()));
        long eligibleNatives = eligibleForMarriage.stream()
            .filter(p -> !p.isBornOutsideVillage())
            .count();
        writeLine(String.format("    - Natives: %d", eligibleNatives));
        writeLine(String.format("    - Outsiders: %d", eligibleForMarriage.size() - eligibleNatives));
        
        // Analyze why outsiders are needed
        Map<Person.Sex, List<Person>> eligibleBySex = eligibleForMarriage.stream()
            .collect(Collectors.groupingBy(Person::getSex));
        
        writeLine("  Eligible by sex:");
        writeLine(String.format("    - Males: %d", eligibleBySex.getOrDefault(Person.Sex.MALE, Collections.emptyList()).size()));
        writeLine(String.format("    - Females: %d", eligibleBySex.getOrDefault(Person.Sex.FEMALE, Collections.emptyList()).size()));
        
        // Check for relative restrictions
        if (eligibleForMarriage.size() > 1) {
            int relativeRestrictions = 0;
            for (Person p1 : eligibleForMarriage) {
                for (Person p2 : eligibleForMarriage) {
                    if (p1 != p2 && p1.getSex() != p2.getSex() && village.areCloseRelatives(p1, p2)) {
                        relativeRestrictions++;
                    }
                }
            }
            writeLine(String.format("  Potential marriages blocked by family relations: %d", relativeRestrictions / 2));
        }
    }
    
    /**
     * Displays the final simulation summary.
     */
    public void displayFinalSummary(String endReason) {
        writeLine("");
        writeLine("========== SIMULATION ENDED ==========");
        writeLine("Reason: " + endReason);
        writeLine(String.format("Total years simulated: %d", engine.getCurrentYear()));
        writeLine(String.format("Final population: %d", engine.getVillage().getPopulation()));
        
        Person player = engine.getCurrentPlayer();
        if (player != null && player.isAlive()) {
            writeLine(String.format("Final player: %s (age %d)", player.getName(), player.getAge()));
        }
        
        // Final statistics
        Village village = engine.getVillage();
        List<Person> finalLiving = village.getLivingVillagers();
        if (!finalLiving.isEmpty()) {
            long finalNatives = finalLiving.stream().filter(p -> !p.isBornOutsideVillage()).count();
            long finalOutsiders = finalLiving.stream().filter(Person::isBornOutsideVillage).count();
            
            writeLine("");
            writeLine("Final Population Breakdown:");
            writeLine(String.format("  Natives: %d (%.1f%%)", finalNatives, (finalNatives * 100.0) / finalLiving.size()));
            writeLine(String.format("  Outsiders: %d (%.1f%%)", finalOutsiders, (finalOutsiders * 100.0) / finalLiving.size()));
        }
        
        console.println("Simulation ended. See output file for details.");
    }
    
    /**
     * Prompts the user to continue or pause the simulation.
     */
    public boolean promptContinue() {
        console.print("Press Enter to continue (or 'q' to quit): ");
        String input = scanner.nextLine();
        return !input.equalsIgnoreCase("q");
    }
    
    /**
     * Writes a line to both file and tracks it.
     */
    private void writeLine(String line) {
        fileWriter.println(line);
        fileWriter.flush(); // Ensure data is written immediately
    }
    
    /**
     * Closes the file writer.
     */
    public void close() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
