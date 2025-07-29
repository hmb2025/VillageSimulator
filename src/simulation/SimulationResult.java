package simulation;

import model.SimulationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a simulation year, including events and continuation status.
 * Provides clear information about simulation state and termination conditions.
 */
public class SimulationResult {
    private final boolean shouldContinueSimulation;
    private final String terminationReason;
    private final List<SimulationEvent> yearEvents;
    private final SimulationStatus status;
    
    /**
     * Enumeration of possible simulation states.
     */
    public enum SimulationStatus {
        CONTINUING("Simulation continuing normally"),
        ENDED_PLAYER_DEATH("Simulation ended - player lineage extinct"),
        ENDED_POPULATION_EXTINCT("Simulation ended - population extinct"),
        ENDED_MAX_YEARS("Simulation ended - maximum years reached"),
        ENDED_USER_REQUEST("Simulation ended - user requested termination"),
        ENDED_OTHER("Simulation ended - other reason");
        
        private final String description;
        
        SimulationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private SimulationResult(boolean shouldContinue, String terminationReason, 
                           List<SimulationEvent> events, SimulationStatus status) {
        this.shouldContinueSimulation = shouldContinue;
        this.terminationReason = terminationReason;
        this.yearEvents = new ArrayList<>(events);
        this.status = status;
    }
    
    /**
     * Creates a result indicating the simulation should continue.
     * 
     * @param events Events that occurred during the year
     * @return A continuation result
     */
    public static SimulationResult continued(List<SimulationEvent> events) {
        return new SimulationResult(
            true, 
            null, 
            Objects.requireNonNull(events, "Events list cannot be null"),
            SimulationStatus.CONTINUING
        );
    }
    
    /**
     * Creates a result indicating the simulation has ended.
     * 
     * @param reason Detailed reason for termination
     * @return A termination result
     */
    public static SimulationResult ended(String reason) {
        SimulationStatus status = determineStatusFromReason(reason);
        return new SimulationResult(
            false, 
            Objects.requireNonNull(reason, "Termination reason cannot be null"),
            new ArrayList<>(),
            status
        );
    }
    
    /**
     * Creates a result indicating the simulation has ended with events.
     * 
     * @param reason Detailed reason for termination
     * @param finalEvents Events that occurred before termination
     * @return A termination result with events
     */
    public static SimulationResult endedWithEvents(String reason, List<SimulationEvent> finalEvents) {
        SimulationStatus status = determineStatusFromReason(reason);
        return new SimulationResult(
            false, 
            Objects.requireNonNull(reason, "Termination reason cannot be null"),
            Objects.requireNonNull(finalEvents, "Events list cannot be null"),
            status
        );
    }
    
    /**
     * Determines the appropriate status based on termination reason.
     */
    private static SimulationStatus determineStatusFromReason(String reason) {
        if (reason == null) return SimulationStatus.ENDED_OTHER;
        
        String lowerReason = reason.toLowerCase();
        
        if (lowerReason.contains("player") || lowerReason.contains("heir")) {
            return SimulationStatus.ENDED_PLAYER_DEATH;
        } else if (lowerReason.contains("population") || lowerReason.contains("extinct")) {
            return SimulationStatus.ENDED_POPULATION_EXTINCT;
        } else if (lowerReason.contains("maximum") || lowerReason.contains("years")) {
            return SimulationStatus.ENDED_MAX_YEARS;
        } else if (lowerReason.contains("user") || lowerReason.contains("quit")) {
            return SimulationStatus.ENDED_USER_REQUEST;
        } else {
            return SimulationStatus.ENDED_OTHER;
        }
    }
    
    // Accessor methods
    public boolean shouldContinue() {
        return shouldContinueSimulation;
    }
    
    public String getTerminationReason() {
        return terminationReason;
    }
    
    public List<SimulationEvent> getYearEvents() {
        return new ArrayList<>(yearEvents);
    }
    
    public SimulationStatus getStatus() {
        return status;
    }
    
    /**
     * Gets a formatted description of the result.
     */
    public String getFormattedDescription() {
        if (shouldContinueSimulation) {
            return String.format("%s (Events: %d)", 
                status.getDescription(), yearEvents.size());
        } else {
            return String.format("%s - %s", 
                status.getDescription(), terminationReason);
        }
    }
    
    @Override
    public String toString() {
        return String.format("SimulationResult{continuing=%s, status=%s, events=%d, reason='%s'}",
            shouldContinueSimulation, status, yearEvents.size(), terminationReason);
    }
}
