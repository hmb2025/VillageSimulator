package ui;

import model.*;
import simulation.SimulationEngine;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the village simulation.
 */
public class ConsoleUI {
    private final SimulationEngine engine;
    private final Scanner scanner;
    
    // Display constants
    private static final String YEAR_SEPARATOR = "=== Year %d ===";
    private static final String SECTION_SEPARATOR = "------------";
    
    public ConsoleUI(SimulationEngine engine, Scanner scanner) {
        this.engine = engine;
        this.scanner = scanner;
    }
    
    /**
     * Gets the initial player name from the user.
     */
    public String getPlayerName() {
        System.out.print("Enter starting player name (male): ");
        return scanner.nextLine();
    }
    
    /**
     * Displays the yearly summary to the console.
     */
    public void displayYearSummary() {
        int year = engine.getCurrentYear() - 1; // Display previous year since we just incremented
        Village village = engine.getVillage();
        List<SimulationEvent> events = engine.getEventsForYear(year);
        
        System.out.println();
        System.out.printf(YEAR_SEPARATOR + "%n", year);
        
        // Display population
        displayPopulation(village);
        
        // Display events
        displayEvents(events);
        
        // Display families
        displayFamilies(village);
        
        System.out.println(SECTION_SEPARATOR);
    }
    
    /**
     * Displays the population count.
     */
    private void displayPopulation(Village village) {
        System.out.printf("Population: %d%n", village.getPopulation());
    }
    
    /**
     * Displays the events that occurred during the year.
     */
    private void displayEvents(List<SimulationEvent> events) {
        System.out.println("Events:");
        if (events.isEmpty()) {
            System.out.println("  None");
        } else {
            for (SimulationEvent event : events) {
                System.out.println("  " + event.getDescription());
            }
        }
    }
    
    /**
     * Displays all families in the village.
     */
    private void displayFamilies(Village village) {
        System.out.println("Families:");
        List<Family> families = village.getFamilies();
        
        if (families.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Family family : families) {
                System.out.println("  " + family.format(engine.getCurrentPlayer()));
            }
        }
    }
    
    /**
     * Displays the final simulation summary.
     */
    public void displayFinalSummary(String endReason) {
        System.out.println();
        System.out.println("=== SIMULATION ENDED ===");
        System.out.println("Reason: " + endReason);
        System.out.printf("Total years simulated: %d%n", engine.getCurrentYear());
        System.out.printf("Final population: %d%n", engine.getVillage().getPopulation());
        
        Person player = engine.getCurrentPlayer();
        if (player != null && player.isAlive()) {
            System.out.printf("Final player: %s (age %d)%n", player.getName(), player.getAge());
        }
    }
    
    /**
     * Prompts the user to continue or pause the simulation.
     */
    public boolean promptContinue() {
        System.out.print("Press Enter to continue (or 'q' to quit): ");
        String input = scanner.nextLine();
        return !input.equalsIgnoreCase("q");
    }
}
