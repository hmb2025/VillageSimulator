import model.*;
import simulation.*;
import ui.ConsoleUI;
import java.util.Scanner;

/**
 * Main entry point for the Village Simulator application.
 * This class coordinates the initialization and execution of the simulation.
 */
public class Main {
    private static final int STARTING_PLAYER_AGE = 20;
    private static final String STARTING_OCCUPATION = "Farmer";
    
    public static void main(String[] args) {
        // Initialize components
        Scanner scanner = new Scanner(System.in);
        SimulationConfig config = SimulationConfig.createDefault();
        Village village = new Village("Haven");
        SimulationEngine engine = new SimulationEngine(village, config);
        ConsoleUI ui = new ConsoleUI(engine, scanner);
        
        try {
            // Get player name and create initial player
            String playerName = ui.getPlayerName();
            Person player = createInitialPlayer(playerName);
            engine.setInitialPlayer(player);
            
            // Run simulation
            runSimulation(engine, ui, config);
            
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Creates the initial player character.
     */
    private static Person createInitialPlayer(String name) {
        return new Person(
            name,
            STARTING_PLAYER_AGE,
            Person.Sex.MALE,
            false, // Born in village
            STARTING_OCCUPATION
        );
    }
    
    /**
     * Runs the main simulation loop.
     */
    private static void runSimulation(SimulationEngine engine, ConsoleUI ui, SimulationConfig config) {
        boolean userWantsToContinue = true;
        
        while (userWantsToContinue) {
            // Simulate one year
            SimulationResult result = engine.simulateYear();
            
            // Display results
            if (config.isVerboseOutput()) {
                ui.displayYearSummary();
            }
            
            // Check if simulation should end
            if (!result.shouldContinue()) {
                ui.displayFinalSummary(result.getEndReason());
                break;
            }
            
            // Check if we've reached max years
            if (engine.getCurrentYear() >= config.getMaxYears()) {
                ui.displayFinalSummary("Maximum years reached");
                break;
            }
            
            // Allow user to pause or quit (optional)
            // userWantsToContinue = ui.promptContinue();
        }
    }
}
