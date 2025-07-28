package model;

/**
 * Represents an event that occurs during the simulation.
 */
public class SimulationEvent {
    private final String description;
    private final int year;
    private final EventType type;
    
    public enum EventType {
        BIRTH, DEATH, MARRIAGE, PLAYER_CHANGE, SIMULATION_END, OUTSIDER_ARRIVAL
    }
    
    private SimulationEvent(String description, int year, EventType type) {
        this.description = description;
        this.year = year;
        this.type = type;
    }
    
    // Factory methods for creating different event types
    
    public static SimulationEvent birth(Person child, Person father, Person mother, int year) {
        String desc = String.format("Birth: %s born to %s and %s", 
            child.getName(), father.getName(), mother.getName());
        return new SimulationEvent(desc, year, EventType.BIRTH);
    }
    
    public static SimulationEvent death(Person person, int year, boolean wasPlayer) {
        String desc = String.format("Death: %s died at age %d%s", 
            person.getName(), person.getAge(), wasPlayer ? " (was player)" : "");
        return new SimulationEvent(desc, year, EventType.DEATH);
    }
    
    public static SimulationEvent marriage(Person person1, Person person2, int year) {
        String p1Info = String.format("%s (%s)", person1.getName(), person1.getOriginStatus());
        String p2Info = String.format("%s (%s)", person2.getName(), person2.getOriginStatus());
        String desc = String.format("Marriage: %s married %s", p1Info, p2Info);
        return new SimulationEvent(desc, year, EventType.MARRIAGE);
    }
    
    public static SimulationEvent outsiderArrival(Person person, int year, String reason) {
        String desc = String.format("Outsider Arrival: %s arrived (needed because: %s)", 
            person.getName(), reason);
        return new SimulationEvent(desc, year, EventType.OUTSIDER_ARRIVAL);
    }
    
    public static SimulationEvent playerChange(Person newPlayer, int year) {
        String desc = String.format("Player Change: Control passed to %s", newPlayer.getName());
        return new SimulationEvent(desc, year, EventType.PLAYER_CHANGE);
    }
    
    public static SimulationEvent simulationEnd(String reason, int year) {
        return new SimulationEvent("Simulation End: " + reason, year, EventType.SIMULATION_END);
    }
    
    // Getters
    public String getDescription() { return description; }
    public int getYear() { return year; }
    public EventType getType() { return type; }
    
    @Override
    public String toString() {
        return String.format("Year %d: %s", year, description);
    }
}
