package simulation;

import model.SimulationEvent;
import java.util.List;

/**
 * Result of a single year simulation.
 */
public class SimulationResult {
    private final boolean continueSimulation;
    private final List<SimulationEvent> events;
    private final String endReason;
    
    private SimulationResult(boolean continueSimulation, List<SimulationEvent> events, String endReason) {
        this.continueSimulation = continueSimulation;
        this.events = events;
        this.endReason = endReason;
    }
    
    public static SimulationResult continued(List<SimulationEvent> events) {
        return new SimulationResult(true, events, null);
    }
    
    public static SimulationResult ended(String reason) {
        return new SimulationResult(false, List.of(), reason);
    }
    
    public boolean shouldContinue() { return continueSimulation; }
    public List<SimulationEvent> getEvents() { return events; }
    public String getEndReason() { return endReason; }
}
