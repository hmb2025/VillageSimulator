import model.*;
import simulation.*;
import ui.ConsoleUI;
import java.util.Scanner;
import java.io.IOException;

/**
 * Main entry point for the Village Simulator application.
 * This class coordinates the initialization and execution of the simulation.
 */
public class Main {
    
    public static void main(String[] args) {
        // Initialize components with clear variable names
        Scanner userInputScanner = new Scanner(System.in);
        SimulationConfig simulationConfig = SimulationConfig.createDefault();
        Village simulationVillage = new Village(simulationConfig.getDefaultVillageName());
        SimulationEngine simulationEngine = new SimulationEngine(simulationVillage, simulationConfig);
        ConsoleUI userInterface = null;
        
        try {
            userInterface = new ConsoleUI(simulationEngine, userInputScanner, simulationConfig);
            
            // Initialize simulation: Get player details and create initial population
            String playerCharacterName = userInterface.requestPlayerName();
            Person playerCharacter = createInitialPlayerCharacter(playerCharacterName, simulationConfig);
            simulationEngine.setInitialPlayer(playerCharacter);
            
            // Initialize village population based on user preference
            int numberOfStartingCouples = userInterface.requestStartingPopulationSize();
            simulationEngine.initializeStartingPopulation(numberOfStartingCouples);
            
            // Execute main simulation loop
            executeSimulationLoop(simulationEngine, userInterface, simulationConfig);
            
        } catch (IOException ioException) {
            System.err.println("ERROR: Failed to create simulation output file - " + ioException.getMessage());
        } finally {
            // Ensure proper resource cleanup
            userInputScanner.close();
            if (userInterface != null) {
                userInterface.close();
            }
        }
    }
    
    /**
     * Creates the initial player character with predefined starting attributes.
     * 
     * @param characterName The name chosen by the player
     * @param config The simulation configuration
     * @return A new Person object representing the player character
     */
    private static Person createInitialPlayerCharacter(String characterName, SimulationConfig config) {
        return new Person(
            characterName,
            config.getInitialPlayerAge(),
            Person.Sex.MALE,
            false, // Native to village (not outsider)
            config.getDefaultPlayerOccupation()
        );
    }
    
    /**
     * Executes the main simulation loop, processing years until termination conditions are met.
     * 
     * @param simulationEngine The engine managing simulation logic
     * @param userInterface The UI for displaying results
     * @param simulationConfig Configuration parameters for the simulation
     */
    private static void executeSimulationLoop(SimulationEngine simulationEngine, 
                                             ConsoleUI userInterface, 
                                             SimulationConfig simulationConfig) {
        boolean simulationActive = true;
        
        while (simulationActive) {
            // Process one year of simulation
            SimulationResult yearResult = simulationEngine.processSimulationYear();
            
            // Display comprehensive year report if verbose mode enabled
            if (simulationConfig.isVerboseReportingEnabled()) {
                userInterface.displayAnnualReport();
            }
            
            // Check simulation termination conditions
            if (!yearResult.shouldContinue()) {
                userInterface.displaySimulationSummary(yearResult.getTerminationReason());
                simulationActive = false;
            } else if (simulationEngine.getCurrentYear() >= simulationConfig.getMaximumSimulationYears()) {
                userInterface.displaySimulationSummary("Maximum simulation duration reached");
                simulationActive = false;
            }
            
            // Note: User interaction for continuation could be enabled here if desired
            // simulationActive = simulationActive && userInterface.promptUserToContinue();
        }
    }
}
