package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents events that occur during the simulation.
 */
public class SimulationEvent {
    public enum EventType {
        BIRTH("Birth"),
        DEATH("Death"),
        MARRIAGE("Marriage"),
        PLAYER_CHANGE("Player Change"),
        SIMULATION_END("Simulation End");

        private final String displayName;

        EventType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final EventType type;
    private final String description;
    private final List<Person> involvedPeople;
    private final int year;

    public SimulationEvent(EventType type, String description, int year) {
        this.type = type;
        this.description = description;
        this.year = year;
        this.involvedPeople = new ArrayList<>();
    }

    public SimulationEvent(EventType type, String description, int year, Person... people) {
        this(type, description, year);
        for (Person person : people) {
            if (person != null) {
                this.involvedPeople.add(person);
            }
        }
    }

    // Factory methods for common events
    public static SimulationEvent birth(Person child, Person father, Person mother, int year) {
        String desc = String.format("%s born to %s & %s", 
            child.getName(), father.getName(), mother.getName());
        return new SimulationEvent(EventType.BIRTH, desc, year, child, father, mother);
    }

    public static SimulationEvent death(Person person, int year, boolean isPlayer) {
        String desc = String.format("%s%s died (age %d)", 
            person.getName(), 
            isPlayer ? " [Player]" : "", 
            person.getAge());
        return new SimulationEvent(EventType.DEATH, desc, year, person);
    }

    public static SimulationEvent marriage(Person person1, Person person2, int year) {
        String desc = String.format("%s married %s%s", 
            person1.getName(), 
            person2.getName(),
            person2.isBornOutsideVillage() ? " (from outside village)" : "");
        return new SimulationEvent(EventType.MARRIAGE, desc, year, person1, person2);
    }

    public static SimulationEvent playerChange(Person newPlayer, int year) {
        String desc = String.format("%s became new player", newPlayer.getName());
        return new SimulationEvent(EventType.PLAYER_CHANGE, desc, year, newPlayer);
    }

    public static SimulationEvent simulationEnd(String reason, int year) {
        return new SimulationEvent(EventType.SIMULATION_END, reason, year);
    }

    // Getters
    public EventType getType() { return type; }
    public String getDescription() { return description; }
    public List<Person> getInvolvedPeople() { return new ArrayList<>(involvedPeople); }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return description;
    }
}
