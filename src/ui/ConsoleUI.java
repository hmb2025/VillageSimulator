package ui;

import model.*;
import simulation.SimulationEngine;
import simulation.SimulationConfig;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Console-based user interface for the village simulation.
 * Provides comprehensive reporting and user interaction capabilities.
 * All formatting and display constants are now centralized in SimulationConfig.
 */
public class ConsoleUI {
    private final SimulationEngine simulationEngine;
    private final Scanner userInputScanner;
    private final PrintWriter reportFileWriter;
    private final PrintStream consoleOutput;
    private final SimulationConfig config;
    
    // Report Formatting - using configuration
    private final String majorSectionDivider;
    private final String minorSectionDivider;
    private final String subSectionDivider;
    
    // Other formatting constants
    private static final String ANNUAL_REPORT_HEADER = "YEAR %d ANNUAL REPORT";
    private static final String SECTION_INDENT = "  ";
    private static final String SUBSECTION_INDENT = "    ";
    private static final String ITEM_INDENT = "      ";
    
    // Date/Time Formatting
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    // Box drawing characters for UI
    private static final String BOX_TOP_LEFT = "╔";
    private static final String BOX_TOP_RIGHT = "╗";
    private static final String BOX_BOTTOM_LEFT = "╚";
    private static final String BOX_BOTTOM_RIGHT = "╝";
    private static final String BOX_HORIZONTAL = "═";
    private static final String BOX_VERTICAL = "║";
    private static final String BOX_CROSS = "╠";
    private static final String BOX_CROSS_RIGHT = "╣";
    
    // UI width constants
    private static final int CONSOLE_BOX_WIDTH = 65;
    private static final int FILE_NAME_PADDING = 51;
    private static final int YEAR_PADDING = 52;
    private static final int POPULATION_PADDING = 46;
    private static final int DETAILS_PADDING = 29;
    
    // Percentage formatting
    private static final double PERCENTAGE_MULTIPLIER = 100.0;
    private static final int PERCENTAGE_DECIMAL_PLACES = 1;
    
    // Division by 2 for pairing calculations
    private static final int PAIR_DIVISOR = 2;
    
    // Population per couple
    private static final int PEOPLE_PER_COUPLE = 2;
    
    public ConsoleUI(SimulationEngine simulationEngine, Scanner userInputScanner, 
                     SimulationConfig config) throws IOException {
        this.simulationEngine = simulationEngine;
        this.userInputScanner = userInputScanner;
        this.config = config;
        this.consoleOutput = System.out;
        
        // Initialize dividers from configuration
        this.majorSectionDivider = "=".repeat(config.getMajorDividerWidth());
        this.minorSectionDivider = "-".repeat(config.getMinorDividerWidth());
        this.subSectionDivider = ".".repeat(config.getSubDividerWidth());
        
        // Create timestamped output file for detailed reporting
        String currentTimestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMATTER);
        String outputFileName = String.format("village_simulation_report_%s.txt", currentTimestamp);
        this.reportFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
        
        // Display initialization message
        displayInitializationMessage(outputFileName);
    }
    
    /**
     * Displays the initialization message with box drawing.
     */
    private void displayInitializationMessage(String outputFileName) {
        String horizontalLine = BOX_HORIZONTAL.repeat(CONSOLE_BOX_WIDTH);
        
        consoleOutput.println(BOX_TOP_LEFT + horizontalLine + BOX_TOP_RIGHT);
        consoleOutput.println(BOX_VERTICAL + centerTextInBox("VILLAGE SIMULATION - OUTPUT REPORT CREATED", CONSOLE_BOX_WIDTH) + BOX_VERTICAL);
        consoleOutput.println(BOX_CROSS + horizontalLine + BOX_CROSS_RIGHT);
        consoleOutput.println(BOX_VERTICAL + " Report File: " + outputFileName + 
            " ".repeat(Math.max(0, FILE_NAME_PADDING - outputFileName.length())) + BOX_VERTICAL);
        consoleOutput.println(BOX_BOTTOM_LEFT + horizontalLine + BOX_BOTTOM_RIGHT);
        consoleOutput.println();
    }
    
    /**
     * Centers text within a box of specified width.
     */
    private String centerTextInBox(String text, int width) {
        int padding = (width - text.length()) / PAIR_DIVISOR;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    /**
     * Requests the player's character name with clear prompting.
     */
    public String requestPlayerName() {
        consoleOutput.print("Enter your character's name (male character): ");
        String playerName = userInputScanner.nextLine().trim();
        
        // Validate input
        while (playerName.isEmpty()) {
            consoleOutput.print("Name cannot be empty. Please enter a valid name: ");
            playerName = userInputScanner.nextLine().trim();
        }
        
        return playerName;
    }
    
    /**
     * Requests the initial village population size with settlement type explanation.
     */
    public int requestStartingPopulationSize() {
        int additionalCouples = 0;
        boolean validInputReceived = false;
        
        // Display settlement options using configuration
        displaySettlementOptions();
        
        while (!validInputReceived) {
            consoleOutput.print(String.format("\nSelect number of starting couples (%d-%d): ", 
                config.getMinimumStartingCouples(), 
                config.getMaximumStartingCouples()));
            try {
                additionalCouples = Integer.parseInt(userInputScanner.nextLine());
                if (additionalCouples >= config.getMinimumStartingCouples() && 
                    additionalCouples <= config.getMaximumStartingCouples()) {
                    validInputReceived = true;
                } else {
                    consoleOutput.println(String.format("ERROR: Please enter a number between %d and %d.",
                        config.getMinimumStartingCouples(), 
                        config.getMaximumStartingCouples()));
                }
            } catch (NumberFormatException e) {
                consoleOutput.println("ERROR: Invalid input. Please enter a whole number.");
            }
        }
        
        // Initialize report with header information
        initializeReportHeader(additionalCouples);
        
        return additionalCouples;
    }
    
    /**
     * Displays settlement configuration options.
     */
    private void displaySettlementOptions() {
        String horizontalLine = BOX_HORIZONTAL.repeat(CONSOLE_BOX_WIDTH);
        
        consoleOutput.println("\n" + BOX_TOP_LEFT + horizontalLine + BOX_TOP_RIGHT);
        consoleOutput.println(BOX_VERTICAL + centerTextInBox("SETTLEMENT CONFIGURATION OPTIONS", CONSOLE_BOX_WIDTH) + BOX_VERTICAL);
        consoleOutput.println(BOX_CROSS + horizontalLine + BOX_CROSS_RIGHT);
        
        // Display each settlement type
        consoleOutput.println(BOX_VERTICAL + formatSettlementOption(
            SimulationConfig.SOLO_SETTLEMENT_COUPLES + "", "Solo Start (just you)") + BOX_VERTICAL); // added: + ""
        consoleOutput.println(BOX_VERTICAL + formatSettlementOption(
            SimulationConfig.FARMSTEAD_MIN_COUPLES + "-" + SimulationConfig.FARMSTEAD_MAX_COUPLES, 
            "Farmstead (small rural settlement)") + BOX_VERTICAL);
        consoleOutput.println(BOX_VERTICAL + formatSettlementOption(
            SimulationConfig.THORP_MIN_COUPLES + "-" + SimulationConfig.THORP_MAX_COUPLES, 
            "Thorp (tiny village)") + BOX_VERTICAL);
        consoleOutput.println(BOX_VERTICAL + formatSettlementOption(
            SimulationConfig.HAMLET_MIN_COUPLES + "-" + SimulationConfig.HAMLET_MAX_COUPLES, 
            "Hamlet (small village)") + BOX_VERTICAL);
        consoleOutput.println(BOX_VERTICAL + formatSettlementOption(
            SimulationConfig.VILLAGE_MIN_COUPLES + "", "Village (established community)") + BOX_VERTICAL);
        
        consoleOutput.println(BOX_BOTTOM_LEFT + horizontalLine + BOX_BOTTOM_RIGHT);
    }
    
    /**
     * Formats settlement option for display.
     */
    private String formatSettlementOption(String couples, String description) {
        String option = String.format("  %s couples = %s", 
            couples.length() == 1 ? couples + " " : couples, description);
        return option + " ".repeat(Math.max(0, CONSOLE_BOX_WIDTH - option.length()));
    }
    
    /**
     * Initializes the report file with comprehensive header information.
     */
    private void initializeReportHeader(int startingCouples) {
        Village village = simulationEngine.getVillage();
        String settlementType = config.getSettlementType(startingCouples);
        
        writeToReport(majorSectionDivider);
        writeToReport(centerText("VILLAGE SIMULATION DETAILED REPORT", config.getReportColumnWidth()));
        writeToReport(centerText(settlementType + " of " + village.getName(), config.getReportColumnWidth()));
        writeToReport(majorSectionDivider);
        writeToReport("");
        writeToReport("SIMULATION PARAMETERS:");
        writeToReport(SECTION_INDENT + "Start Time: " + LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        writeToReport(SECTION_INDENT + "Settlement Type: " + settlementType);
        writeToReport(SECTION_INDENT + "Initial Couples: " + startingCouples);
        writeToReport(SECTION_INDENT + "Total Initial Population: " + 
            (startingCouples * PEOPLE_PER_COUPLE + 1) + " (including player)");
        writeToReport(SECTION_INDENT + "Configuration: " + config.toString());
        writeToReport(minorSectionDivider);
        writeToReport("");
    }
    
    /**
     * Displays comprehensive annual report for the current year.
     */
    public void displayAnnualReport() {
        int currentYear = simulationEngine.getCurrentYear();
        Village village = simulationEngine.getVillage();
        List<SimulationEvent> yearEvents = simulationEngine.getEventsForYear(currentYear);
        
        // Report Header
        writeToReport("");
        writeToReport(majorSectionDivider);
        writeToReport(centerText(String.format(ANNUAL_REPORT_HEADER, currentYear), config.getReportColumnWidth()));
        writeToReport(majorSectionDivider);
        
        // Section 1: Year Events Summary
        displayEventsSummary(yearEvents, currentYear);
        
        // Section 2: Population Demographics
        displayPopulationDemographics(village);
        
        // Section 3: Living Villager Registry
        displayVillagerRegistry(village);
        
        // Section 4: Family Structure Analysis
        displayFamilyStructures(village);
        
        // Section 5: Marriage Market Analysis
        displayMarriageMarketAnalysis(village);
        
        // Report Footer
        writeToReport(minorSectionDivider);
        
        // Console progress indicator
        consoleOutput.printf("Year %d completed | Population: %d | Events: %d%n", 
            currentYear, village.getPopulation(), yearEvents.size());
    }
    
    /**
     * Displays categorized events summary with detailed statistics.
     */
    private void displayEventsSummary(List<SimulationEvent> events, int year) {
        writeToReport("");
        writeToReport("▶ SECTION 1: EVENTS THIS YEAR");
        writeToReport(subSectionDivider);
        
        if (events.isEmpty()) {
            writeToReport(SECTION_INDENT + "No significant events occurred this year.");
        } else {
            // Categorize events by type
            Map<SimulationEvent.EventType, List<SimulationEvent>> eventsByType = 
                events.stream().collect(Collectors.groupingBy(SimulationEvent::getType));
            
            writeToReport(SECTION_INDENT + "Total Events: " + events.size());
            writeToReport("");
            
            // Display events by category with counts
            for (SimulationEvent.EventType type : SimulationEvent.EventType.values()) {
                List<SimulationEvent> typeEvents = eventsByType.getOrDefault(type, new ArrayList<>());
                if (!typeEvents.isEmpty()) {
                    String categoryName = formatEventTypeName(type);
                    writeToReport(SECTION_INDENT + categoryName + " (" + typeEvents.size() + "):");
                    for (SimulationEvent event : typeEvents) {
                        writeToReport(SUBSECTION_INDENT + "• " + event.getDescription());
                    }
                    writeToReport("");
                }
            }
        }
    }
    
    /**
     * Formats event type names for display.
     */
    private String formatEventTypeName(SimulationEvent.EventType type) {
        return switch (type) {
            case BIRTH -> "BIRTHS";
            case DEATH -> "DEATHS";
            case MARRIAGE -> "MARRIAGES";
            case OUTSIDER_ARRIVAL -> "NEW ARRIVALS";
            case PLAYER_CHANGE -> "SUCCESSION";
            case SIMULATION_END -> "TERMINATION";
        };
    }
    
    /**
     * Displays comprehensive population demographics with statistical analysis.
     */
    private void displayPopulationDemographics(Village village) {
        writeToReport("");
        writeToReport("▶ SECTION 2: POPULATION DEMOGRAPHICS");
        writeToReport(subSectionDivider);
        
        List<Person> livingPopulation = village.getLivingVillagers();
        
        if (livingPopulation.isEmpty()) {
            writeToReport(SECTION_INDENT + "No living villagers (population extinct)");
            return;
        }
        
        // Calculate comprehensive statistics
        long totalPopulation = livingPopulation.size();
        long nativePopulation = livingPopulation.stream().filter(p -> !p.isBornOutsideVillage()).count();
        long outsiderPopulation = totalPopulation - nativePopulation;
        long malePopulation = livingPopulation.stream().filter(p -> p.getSex() == Person.Sex.MALE).count();
        long femalePopulation = totalPopulation - malePopulation;
        long marriedPopulation = livingPopulation.stream().filter(p -> p.getMarriedTo() != null).count();
        long unmarriedPopulation = totalPopulation - marriedPopulation;
        
        // Age statistics
        IntSummaryStatistics ageStatistics = livingPopulation.stream()
            .mapToInt(Person::getAge)
            .summaryStatistics();
        
        // Display formatted statistics
        writeToReport(SECTION_INDENT + "TOTAL POPULATION: " + totalPopulation);
        writeToReport("");
        
        writeToReport(SECTION_INDENT + "Population by Origin:");
        writeToReport(SUBSECTION_INDENT + String.format("• Native-born: %d (%.1f%%)", 
            nativePopulation, (nativePopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(SUBSECTION_INDENT + String.format("• Outsiders: %d (%.1f%%)", 
            outsiderPopulation, (outsiderPopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
        
        writeToReport("");
        writeToReport(SECTION_INDENT + "Population by Gender:");
        writeToReport(SUBSECTION_INDENT + String.format("• Males: %d (%.1f%%)", 
            malePopulation, (malePopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(SUBSECTION_INDENT + String.format("• Females: %d (%.1f%%)", 
            femalePopulation, (femalePopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(SUBSECTION_INDENT + String.format("• Gender Ratio: %.2f:1 (M:F)", 
            femalePopulation > 0 ? (double)malePopulation / femalePopulation : malePopulation));
        
        writeToReport("");
        writeToReport(SECTION_INDENT + "Age Distribution:");
        writeToReport(SUBSECTION_INDENT + String.format("• Minimum Age: %d years", ageStatistics.getMin()));
        writeToReport(SUBSECTION_INDENT + String.format("• Maximum Age: %d years", ageStatistics.getMax()));
        writeToReport(SUBSECTION_INDENT + String.format("• Average Age: %.1f years", ageStatistics.getAverage()));
        
        // Age cohorts using configuration
        long children = livingPopulation.stream().filter(p -> p.getAge() < config.getAdultAge()).count();
        long adults = livingPopulation.stream().filter(p -> p.getAge() >= config.getAdultAge() && 
                                                           p.getAge() < config.getElderAge()).count();
        long elders = livingPopulation.stream().filter(p -> p.getAge() >= config.getElderAge()).count();
        
        writeToReport(SUBSECTION_INDENT + "• Age Cohorts:");
        writeToReport(ITEM_INDENT + String.format("- Children (0-%d): %d (%.1f%%)", 
            config.getAdultAge() - 1, children, (children * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(ITEM_INDENT + String.format("- Adults (%d-%d): %d (%.1f%%)", 
            config.getAdultAge(), config.getElderAge() - 1, adults, 
            (adults * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(ITEM_INDENT + String.format("- Elders (%d+): %d (%.1f%%)", 
            config.getElderAge(), elders, (elders * PERCENTAGE_MULTIPLIER) / totalPopulation));
        
        writeToReport("");
        writeToReport(SECTION_INDENT + "Marital Status:");
        writeToReport(SUBSECTION_INDENT + String.format("• Married: %d (%.1f%%)", 
            marriedPopulation, (marriedPopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
        writeToReport(SUBSECTION_INDENT + String.format("• Unmarried: %d (%.1f%%)", 
            unmarriedPopulation, (unmarriedPopulation * PERCENTAGE_MULTIPLIER) / totalPopulation));
    }
    
    /**
     * Displays comprehensive villager registry with detailed individual information.
     */
    private void displayVillagerRegistry(Village village) {
        writeToReport("");
        writeToReport("▶ SECTION 3: VILLAGER REGISTRY");
        writeToReport(subSectionDivider);
        
        List<Person> villagers = village.getLivingVillagers();
        
        if (villagers.isEmpty()) {
            writeToReport(SECTION_INDENT + "No living villagers registered");
            return;
        }
        
        // Sort by age (oldest first), then by name
        villagers.sort(Comparator.comparing(Person::getAge).reversed()
            .thenComparing(Person::getName));
        
        Person currentPlayer = simulationEngine.getCurrentPlayer();
        
        for (Person person : villagers) {
            // Build person entry with all relevant details
            StringBuilder personEntry = new StringBuilder();
            personEntry.append(SECTION_INDENT);
            
            // Name and basic info
            personEntry.append(String.format("• %s | Age: %d | %s | %s | %s",
                person.getName(),
                person.getAge(),
                person.getSex() == Person.Sex.MALE ? "Male" : "Female",
                person.getOccupation(),
                person.isBornOutsideVillage() ? "OUTSIDER" : "Native"
            ));
            
            // Special markers
            if (person == currentPlayer) {
                personEntry.append(" [CURRENT PLAYER]");
            }
            
            writeToReport(personEntry.toString());
            
            // Marriage information
            if (person.getMarriedTo() != null) {
                writeToReport(SUBSECTION_INDENT + "Spouse: " + person.getMarriedTo().getName());
            } else {
                writeToReport(SUBSECTION_INDENT + "Marital Status: Single");
            }
            
            // Children information
            if (person.hasChildren()) {
                List<String> childrenNames = person.getChildren().stream()
                    .map(Person::getName)
                    .collect(Collectors.toList());
                writeToReport(SUBSECTION_INDENT + "Children (" + childrenNames.size() + "): " + 
                    String.join(", ", childrenNames));
            }
            
            // Parent information for natives
            if (!person.isBornOutsideVillage()) {
                StringBuilder parentInfo = new StringBuilder(SUBSECTION_INDENT + "Parents: ");
                if (person.getMother() != null && person.getFather() != null) {
                    parentInfo.append(person.getMother().getName())
                             .append(" (mother) & ")
                             .append(person.getFather().getName())
                             .append(" (father)");
                } else {
                    parentInfo.append("Unknown (records missing)");
                }
                writeToReport(parentInfo.toString());
            }
            
            writeToReport("");
        }
    }
    
    /**
     * Displays family structure analysis with genealogical relationships.
     */
    private void displayFamilyStructures(Village village) {
        writeToReport("");
        writeToReport("▶ SECTION 4: FAMILY STRUCTURES");
        writeToReport(subSectionDivider);
        
        List<Family> families = village.getFamilies();
        
        if (families.isEmpty()) {
            writeToReport(SECTION_INDENT + "No established families");
            return;
        }
        
        writeToReport(SECTION_INDENT + "Total Families: " + families.size());
        writeToReport("");
        
        Person currentPlayer = simulationEngine.getCurrentPlayer();
        int familyNumber = 1;
        
        for (Family family : families) {
            writeToReport(SECTION_INDENT + "FAMILY #" + familyNumber++);
            writeToReport(family.formatDetailed(currentPlayer));
            writeToReport("");
        }
    }
    
    /**
     * Displays detailed marriage market analysis and relationship constraints.
     */
    private void displayMarriageMarketAnalysis(Village village) {
        writeToReport("");
        writeToReport("▶ SECTION 5: MARRIAGE MARKET ANALYSIS");
        writeToReport(subSectionDivider);
        
        List<Person> livingPopulation = village.getLivingVillagers();
        List<Person> eligibleForMarriage = livingPopulation.stream()
            .filter(Person::isEligibleForMarriage)
            .collect(Collectors.toList());
        
        writeToReport(SECTION_INDENT + "Eligible for Marriage: " + eligibleForMarriage.size());
        
        if (eligibleForMarriage.isEmpty()) {
            writeToReport(SUBSECTION_INDENT + "No eligible candidates for marriage");
            return;
        }
        
        // Analyze by gender
        Map<Person.Sex, List<Person>> eligibleByGender = eligibleForMarriage.stream()
            .collect(Collectors.groupingBy(Person::getSex));
        
        long eligibleMales = eligibleByGender.getOrDefault(Person.Sex.MALE, Collections.emptyList()).size();
        long eligibleFemales = eligibleByGender.getOrDefault(Person.Sex.FEMALE, Collections.emptyList()).size();
        
        writeToReport("");
        writeToReport(SECTION_INDENT + "Eligible by Gender:");
        writeToReport(SUBSECTION_INDENT + "• Males: " + eligibleMales);
        writeToReport(SUBSECTION_INDENT + "• Females: " + eligibleFemales);
        
        // Analyze by origin
        long eligibleNatives = eligibleForMarriage.stream()
            .filter(p -> !p.isBornOutsideVillage())
            .count();
        long eligibleOutsiders = eligibleForMarriage.size() - eligibleNatives;
        
        writeToReport("");
        writeToReport(SECTION_INDENT + "Eligible by Origin:");
        writeToReport(SUBSECTION_INDENT + "• Natives: " + eligibleNatives);
        writeToReport(SUBSECTION_INDENT + "• Outsiders: " + eligibleOutsiders);
        
        // Calculate relationship constraints
        if (eligibleForMarriage.size() > 1) {
            int blockedByFamilyRelations = 0;
            for (Person p1 : eligibleForMarriage) {
                for (Person p2 : eligibleForMarriage) {
                    if (p1 != p2 && p1.getSex() != p2.getSex() && village.areCloseRelatives(p1, p2)) {
                        blockedByFamilyRelations++;
                    }
                }
            }
            
            writeToReport("");
            writeToReport(SECTION_INDENT + "Marriage Constraints:");
            writeToReport(SUBSECTION_INDENT + "• Potential matches blocked by family relations: " + 
                (blockedByFamilyRelations / PAIR_DIVISOR));
            
            // Calculate theoretical maximum matches
            int theoreticalMatches = Math.min((int)eligibleMales, (int)eligibleFemales);
            writeToReport(SUBSECTION_INDENT + "• Theoretical maximum marriages possible: " + theoreticalMatches);
        }
        
        // List recently married outsiders
        List<Person> recentOutsiderMarriages = livingPopulation.stream()
            .filter(p -> p.isBornOutsideVillage() && p.getMarriedTo() != null)
            .collect(Collectors.toList());
        
        if (!recentOutsiderMarriages.isEmpty()) {
            writeToReport("");
            writeToReport(SECTION_INDENT + "Outsiders Who Married Into Village:");
            for (Person outsider : recentOutsiderMarriages) {
                Person spouse = outsider.getMarriedTo();
                String spouseOrigin = spouse.isBornOutsideVillage() ? "outsider" : "native";
                writeToReport(SUBSECTION_INDENT + String.format("• %s married to %s (%s)",
                    outsider.getName(), spouse.getName(), spouseOrigin));
            }
        }
    }
    
    /**
     * Displays final simulation summary with comprehensive statistics.
     */
    public void displaySimulationSummary(String terminationReason) {
        writeToReport("");
        writeToReport(majorSectionDivider);
        writeToReport(centerText("SIMULATION FINAL SUMMARY", config.getReportColumnWidth()));
        writeToReport(majorSectionDivider);
        writeToReport("");
        
        writeToReport("TERMINATION DETAILS:");
        writeToReport(SECTION_INDENT + "Reason: " + terminationReason);
        writeToReport(SECTION_INDENT + "End Time: " + LocalDateTime.now().format(TIMESTAMP_FORMATTER));
        writeToReport(SECTION_INDENT + "Total Years Simulated: " + simulationEngine.getCurrentYear());
        writeToReport("");
        
        Village village = simulationEngine.getVillage();
        List<Person> finalPopulation = village.getLivingVillagers();
        
        writeToReport("FINAL STATISTICS:");
        writeToReport(SECTION_INDENT + "Final Population Count: " + finalPopulation.size());
        
        Person finalPlayer = simulationEngine.getCurrentPlayer();
        if (finalPlayer != null && finalPlayer.isAlive()) {
            writeToReport(SECTION_INDENT + "Final Player Character: " + finalPlayer.getName() + 
                " (Age: " + finalPlayer.getAge() + ")");
        } else {
            writeToReport(SECTION_INDENT + "Final Player Character: Deceased (no living heir)");
        }
        
        if (!finalPopulation.isEmpty()) {
            long finalNatives = finalPopulation.stream().filter(p -> !p.isBornOutsideVillage()).count();
            long finalOutsiders = finalPopulation.stream().filter(Person::isBornOutsideVillage).count();
            
            writeToReport("");
            writeToReport("POPULATION COMPOSITION:");
            writeToReport(SECTION_INDENT + String.format("• Native-born: %d (%.1f%%)", 
                finalNatives, (finalNatives * PERCENTAGE_MULTIPLIER) / finalPopulation.size()));
            writeToReport(SECTION_INDENT + String.format("• Outsiders: %d (%.1f%%)", 
                finalOutsiders, (finalOutsiders * PERCENTAGE_MULTIPLIER) / finalPopulation.size()));
            
            // Calculate generational statistics
            long hasChildren = finalPopulation.stream().filter(Person::hasChildren).count();
            long totalChildren = finalPopulation.stream()
                .mapToLong(p -> p.getChildren().size())
                .sum();
            
            writeToReport("");
            writeToReport("GENERATIONAL LEGACY:");
            writeToReport(SECTION_INDENT + "• Villagers with children: " + hasChildren);
            writeToReport(SECTION_INDENT + "• Total children born: " + totalChildren);
        }
        
        writeToReport("");
        writeToReport(majorSectionDivider);
        writeToReport(centerText("END OF SIMULATION REPORT", config.getReportColumnWidth()));
        writeToReport(majorSectionDivider);
        
        // Console notification
        displayFinalConsoleMessage(finalPopulation.size());
    }
    
    /**
     * Displays the final console message with box drawing.
     */
    private void displayFinalConsoleMessage(int finalPopulation) {
        String horizontalLine = BOX_HORIZONTAL.repeat(CONSOLE_BOX_WIDTH);
        
        consoleOutput.println("\n" + BOX_TOP_LEFT + horizontalLine + BOX_TOP_RIGHT);
        consoleOutput.println(BOX_VERTICAL + centerTextInBox("SIMULATION COMPLETED", CONSOLE_BOX_WIDTH) + BOX_VERTICAL);
        consoleOutput.println(BOX_CROSS + horizontalLine + BOX_CROSS_RIGHT);
        consoleOutput.println(BOX_VERTICAL + " Final Year: " + simulationEngine.getCurrentYear() + 
            " ".repeat(Math.max(0, YEAR_PADDING - String.valueOf(simulationEngine.getCurrentYear()).length())) + BOX_VERTICAL);
        consoleOutput.println(BOX_VERTICAL + " Final Population: " + finalPopulation + 
            " ".repeat(Math.max(0, POPULATION_PADDING - String.valueOf(finalPopulation).length())) + BOX_VERTICAL);
        consoleOutput.println(BOX_VERTICAL + " Details saved to report file" + 
            " ".repeat(CONSOLE_BOX_WIDTH - DETAILS_PADDING) + BOX_VERTICAL);
        consoleOutput.println(BOX_BOTTOM_LEFT + horizontalLine + BOX_BOTTOM_RIGHT);
    }
    
    /**
     * Prompts user to continue simulation (optional feature).
     */
    public boolean promptUserToContinue() {
        consoleOutput.print("Press Enter to continue to next year (or type 'quit' to end): ");
        String userInput = userInputScanner.nextLine().trim();
        return !userInput.equalsIgnoreCase("quit");
    }
    
    /**
     * Writes a line to the report file with immediate flush.
     */
    private void writeToReport(String reportLine) {
        reportFileWriter.println(reportLine);
        reportFileWriter.flush(); // Ensure immediate write to disk
    }
    
    /**
     * Centers text within a specified width.
     */
    private String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / PAIR_DIVISOR;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    /**
     * Closes the report file writer and releases resources.
     */
    public void close() {
        if (reportFileWriter != null) {
            reportFileWriter.close();
        }
    }
}
