package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a person in the village simulation.
 * This class encapsulates all personal attributes and relationships.
 */
public class Person {
    public enum Sex { 
        MALE, FEMALE;
        
        public Sex getOpposite() {
            return this == MALE ? FEMALE : MALE;
        }
    }

    // Personal attributes
    private String name;
    private int age;
    private final Sex sex;
    private boolean isAlive;
    private final boolean bornOutsideVillage;
    private String occupation;
    
    // Relationships
    private Person marriedTo;
    private final List<Person> children;
    
    // Constants
    public static final int MINIMUM_MARRIAGE_AGE = 18;
    public static final int MAXIMUM_MARRIAGE_AGE = 29;
    public static final int MAXIMUM_CHILDREN = 2;

    /**
     * Creates a new person with the specified attributes.
     */
    public Person(String name, int age, Sex sex, boolean bornOutsideVillage, String occupation) {
        validateName(name);
        validateAge(age);
        Objects.requireNonNull(sex, "Sex cannot be null");
        
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.isAlive = true;
        this.bornOutsideVillage = bornOutsideVillage;
        this.occupation = occupation != null ? occupation : "None";
        this.children = new ArrayList<>();
    }

    // Validation methods
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }
    
    private void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    // Relationship methods
    /**
     * Marries this person to the specified spouse.
     * @throws IllegalStateException if either person is dead or already married
     * @throws IllegalArgumentException if spouses are of the same sex
     */
    public void marry(Person spouse) {
        Objects.requireNonNull(spouse, "Spouse cannot be null");
        
        if (!this.isAlive || !spouse.isAlive) {
            throw new IllegalStateException("Cannot marry: one or both persons are not alive");
        }
        if (this.marriedTo != null || spouse.marriedTo != null) {
            throw new IllegalStateException("Cannot marry: one or both persons are already married");
        }
        if (this.sex == spouse.sex) {
            throw new IllegalArgumentException("Cannot marry: spouses must be of opposite sex");
        }
        
        this.marriedTo = spouse;
        spouse.marriedTo = this;
    }

    /**
     * Ends the marriage for both spouses.
     */
    public void divorce() {
        if (this.marriedTo != null) {
            Person spouse = this.marriedTo;
            this.marriedTo = null;
            spouse.marriedTo = null;
        }
    }

    /**
     * Adds a child to both parents' child lists.
     * @throws IllegalStateException if maximum children limit is reached
     */
    public void addChild(Person child) {
        Objects.requireNonNull(child, "Child cannot be null");
        
        if (this.children.size() >= MAXIMUM_CHILDREN) {
            throw new IllegalStateException(
                String.format("Cannot add child: maximum of %d children per parent", MAXIMUM_CHILDREN)
            );
        }
        
        this.children.add(child);
        if (this.marriedTo != null && !this.marriedTo.children.contains(child)) {
            this.marriedTo.children.add(child);
        }
    }

    /**
     * Ages the person by one year.
     */
    public void ageOneYear() {
        this.age++;
    }

    /**
     * Marks the person as dead.
     */
    public void die() {
        this.isAlive = false;
        // Widow the spouse
        if (this.marriedTo != null) {
            this.marriedTo.marriedTo = null;
            this.marriedTo = null;
        }
    }

    /**
     * Checks if this person can marry based on age and marital status.
     */
    public boolean isEligibleForMarriage() {
        return isAlive && 
               marriedTo == null && 
               age >= MINIMUM_MARRIAGE_AGE && 
               age <= MAXIMUM_MARRIAGE_AGE && 
               children.isEmpty();
    }

    /**
     * Checks if this person can have more children.
     */
    public boolean canHaveMoreChildren() {
        return isAlive && 
               marriedTo != null && 
               marriedTo.isAlive && 
               children.size() < MAXIMUM_CHILDREN;
    }

    // Getters
    public String getName() { return name + (bornOutsideVillage ? " (outsider)" : " (native)"); }
    public int getAge() { return age; }
    public Sex getSex() { return sex; }
    public boolean isAlive() { return isAlive; }
    public boolean isBornOutsideVillage() { return bornOutsideVillage; }
    public String getOccupation() { return occupation; }
    public Person getMarriedTo() { return marriedTo; }
    public List<Person> getChildren() { return new ArrayList<>(children); }
    public boolean hasChildren() { return !children.isEmpty(); }
    
    // Setters (only for mutable properties)
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation != null ? occupation : "None";
    }

    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d, sex=%s, alive=%s}", 
            name, age, sex, isAlive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && 
               isAlive == person.isAlive && 
               bornOutsideVillage == person.bornOutsideVillage && 
               Objects.equals(name, person.name) && 
               sex == person.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, sex, isAlive, bornOutsideVillage);
    }
}
