package model;

import java.util.Objects;

/**
 * Represents a significant event that occurs during the simulation.
 * Provides detailed, consistently formatted descriptions for all event types.
 */
public class SimulationEvent {
    private final String eventDescription;
    private final int occurredInYear;
    private final EventType eventType;
    private final EventPriority eventPriority;
    
    /**
     * Enumeration of all possible event types in the simulation.
     */
    public enum EventType {
        BIRTH("Birth", "A new villager is born"),
        DEATH("Death", "A villager has died"),
        MARRIAGE("Marriage", "Two villagers have married"),
        PLAYER_CHANGE("Succession", "Player control has transferred"),
        SIMULATION_END("Termination", "Simulation has ended"),
        OUTSIDER_ARRIVAL("Immigration", "An outsider joined the village");
        
        private final String displayName;
        private final String description;
        
        EventType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Priority levels for events (for sorting and display purposes).
     */
    public enum EventPriority {
        CRITICAL(1, "Critical event requiring immediate attention"),
        HIGH(2, "Important event affecting village dynamics"),
        NORMAL(3, "Regular demographic event"),
        LOW(4, "Minor event for information only");
        
        private final int level;
        private final String description;
        
        EventPriority(int level, String description) {
            this.level = level;
            this.description = description;
        }
        
        public int getLevel() { return level; }
        public String getDescription() { return description; }
    }
    
    private SimulationEvent(String description, int year, EventType type, EventPriority priority) {
        this.eventDescription = Objects.requireNonNull(description, "Event description cannot be null");
        this.occurredInYear = year;
        this.eventType = Objects.requireNonNull(type, "Event type cannot be null");
        this.eventPriority = Objects.requireNonNull(priority, "Event priority cannot be null");
    }
    
    // Factory methods for creating specific event types with consistent formatting
    
    /**
     * Creates a birth event with detailed parent information.
     */
    public static SimulationEvent birth(Person child, Person father, Person mother, int year) {
        String description = String.format(
            "%s was born to %s (father, %s) and %s (mother, %s)",
            child.getName(),
            father.getName(), father.getOccupation(),
            mother.getName(), mother.getOccupation()
        );
        return new SimulationEvent(description, year, EventType.BIRTH, EventPriority.NORMAL);
    }
    
    /**
     * Creates a death event with age and role information.
     */
    public static SimulationEvent death(Person person, int year, boolean wasPlayer) {
        EventPriority priority = wasPlayer ? EventPriority.CRITICAL : EventPriority.HIGH;
        String roleInfo = wasPlayer ? " [PLAYER CHARACTER]" : "";
        String description = String.format(
            "%s (%s, %s) died at age %d%s",
            person.getName(),
            person.getSex() == Person.Sex.MALE ? "Male" : "Female",
            person.getOccupation(),
            person.getAge(),
            roleInfo
        );
        return new SimulationEvent(description, year, EventType.DEATH, priority);
    }
    
    /**
     * Creates a marriage event with origin status information.
     */
    public static SimulationEvent marriage(Person person1, Person person2, int year) {
        // Format with complete information about both partners
        String p1Info = String.format("%s (%d, %s, %s)",
            person1.getName(),
            person1.getAge(),
            person1.getOccupation(),
            person1.isBornOutsideVillage() ? "Outsider" : "Native"
        );
        
        String p2Info = String.format("%s (%d, %s, %s)",
            person2.getName(),
            person2.getAge(),
            person2.getOccupation(),
            person2.isBornOutsideVillage() ? "Outsider" : "Native"
        );
        
        String description = String.format("%s married %s", p1Info, p2Info);
        
        // Higher priority if involves outsider (affects demographics)
        EventPriority priority = (person1.isBornOutsideVillage() || person2.isBornOutsideVillage()) 
            ? EventPriority.HIGH : EventPriority.NORMAL;
            
        return new SimulationEvent(description, year, EventType.MARRIAGE, priority);
    }
    
    /**
     * Creates an outsider arrival event with detailed reasoning.
     */
    public static SimulationEvent outsiderArrival(Person person, int year, String arrivalReason) {
        String description = String.format(
            "%s (%d, %s, %s) arrived from outside | Reason: %s",
            person.getName(),
            person.getAge(),
            person.getSex() == Person.Sex.MALE ? "Male" : "Female",
            person.getOccupation(),
            arrivalReason
        );
        return new SimulationEvent(description, year, EventType.OUTSIDER_ARRIVAL, EventPriority.HIGH);
    }
    
    /**
     * Creates a player succession event.
     */
    public static SimulationEvent playerChange(Person newPlayer, int year) {
        String description = String.format(
            "Player control transferred to %s (%d, %s, %s)",
            newPlayer.getName(),
            newPlayer.getAge(),
            newPlayer.getSex() == Person.Sex.MALE ? "Male" : "Female",
            newPlayer.getOccupation()
        );
        return new SimulationEvent(description, year, EventType.PLAYER_CHANGE, EventPriority.CRITICAL);
    }
    
    /**
     * Creates a simulation termination event.
     */
    public static SimulationEvent simulationEnd(String reason, int year) {
        String description = "Simulation terminated: " + reason;
        return new SimulationEvent(description, year, EventType.SIMULATION_END, EventPriority.CRITICAL);
    }
    
    // Accessor methods
    public String getDescription() { 
        return eventDescription; 
    }
    
    public int getYear() { 
        return occurredInYear; 
    }
    
    public EventType getType() { 
        return eventType; 
    }
    
    public EventPriority getPriority() { 
        return eventPriority; 
    }
    
    /**
     * Gets a formatted string representation suitable for reports.
     */
    public String getFormattedReport() {
        return String.format("[Year %d] %s: %s", 
            occurredInYear, 
            eventType.getDisplayName().toUpperCase(), 
            eventDescription);
    }
    
    /**
     * Gets a brief summary of the event.
     */
    public String getBriefSummary() {
        return String.format("%s in Year %d", eventType.getDisplayName(), occurredInYear);
    }
    
    @Override
    public String toString() {
        return getFormattedReport();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationEvent that = (SimulationEvent) o;
        return occurredInYear == that.occurredInYear &&
               Objects.equals(eventDescription, that.eventDescription) &&
               eventType == that.eventType;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventDescription, occurredInYear, eventType);
    }
}
