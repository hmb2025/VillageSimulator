package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a person in the village simulation.
 * Encapsulates all personal attributes, relationships, and life events.
 */
public class Person {
    /**
     * Biological sex enumeration with utility methods.
     */
    public enum Sex { 
        MALE("Male", "M"),
        FEMALE("Female", "F");
        
        private final String fullName;
        private final String abbreviation;
        
        Sex(String fullName, String abbreviation) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
        }
        
        public Sex getOpposite() {
            return this == MALE ? FEMALE : MALE;
        }
        
        public String getFullName() { return fullName; }
        public String getAbbreviation() { return abbreviation; }
        
        @Override
        public String toString() { return fullName; }
    }

    // Personal Identity Attributes
    private String fullName;
    private int ageInYears;
    private final Sex biologicalSex;
    private boolean livingStatus;
    private final boolean outsiderOrigin;  // true if born outside village
    private String primaryOccupation;
    
    // Family Relationships
    private Person marriagePartner;
    private final List<Person> offspring;
    private Person biologicalMother;
    private Person biologicalFather;
    
    // Life Event Configuration Constants
    public static final int MINIMUM_MARRIAGE_AGE = 18;
    public static final int MAXIMUM_MARRIAGE_AGE = 29;
    public static final int MAXIMUM_CHILDREN = 2;
    public static final int PLAYER_LINEAGE_CHILD_LIMIT = 1;

    /**
     * Creates a new person with specified attributes.
     * 
     * @param fullName The person's full name
     * @param ageInYears Initial age in years
     * @param biologicalSex Biological sex (Male/Female)
     * @param outsiderOrigin True if born outside the village
     * @param primaryOccupation Person's occupation or trade
     */
    public Person(String fullName, int ageInYears, Sex biologicalSex, 
                  boolean outsiderOrigin, String primaryOccupation) {
        validateName(fullName);
        validateAge(ageInYears);
        Objects.requireNonNull(biologicalSex, "Biological sex cannot be null");
        
        this.fullName = fullName;
        this.ageInYears = ageInYears;
        this.biologicalSex = biologicalSex;
        this.livingStatus = true;
        this.outsiderOrigin = outsiderOrigin;
        this.primaryOccupation = primaryOccupation != null ? primaryOccupation : "None";
        this.offspring = new ArrayList<>();
    }

    // Validation Methods
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name cannot exceed 50 characters");
        }
    }
    
    private void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        if (age > 150) {
            throw new IllegalArgumentException("Age cannot exceed 150 years");
        }
    }

    // Marriage and Relationship Methods
    
    /**
     * Establishes a marriage between this person and the specified partner.
     * 
     * @param partner The person to marry
     * @throws IllegalStateException if either person is dead or already married
     * @throws IllegalArgumentException if partners are of the same sex
     */
    public void marry(Person partner) {
        Objects.requireNonNull(partner, "Marriage partner cannot be null");
        
        if (!this.livingStatus || !partner.livingStatus) {
            throw new IllegalStateException(
                "Cannot establish marriage: one or both persons are deceased");
        }
        if (this.marriagePartner != null || partner.marriagePartner != null) {
            throw new IllegalStateException(
                "Cannot establish marriage: one or both persons are already married");
        }
        if (this.biologicalSex == partner.biologicalSex) {
            throw new IllegalArgumentException(
                "Cannot establish marriage: partners must be of opposite biological sex");
        }
        
        this.marriagePartner = partner;
        partner.marriagePartner = this;
    }

    /**
     * Dissolves the marriage for both partners.
     */
    public void dissolveMarriage() {
        if (this.marriagePartner != null) {
            Person formerSpouse = this.marriagePartner;
            this.marriagePartner = null;
            formerSpouse.marriagePartner = null;
        }
    }

    /**
     * Records a child for this parent and their spouse.
     * 
     * @param child The child to add
     * @throws IllegalStateException if maximum children limit is reached
     */
    public void addChild(Person child) {
        Objects.requireNonNull(child, "Child cannot be null");
        
        if (this.offspring.size() >= MAXIMUM_CHILDREN) {
            throw new IllegalStateException(
                String.format("Cannot add child: maximum of %d children per parent reached", 
                    MAXIMUM_CHILDREN));
        }
        
        this.offspring.add(child);
        
        // Also add to spouse's children if married
        if (this.marriagePartner != null && !this.marriagePartner.offspring.contains(child)) {
            this.marriagePartner.offspring.add(child);
        }
    }

    /**
     * Sets the biological parents of this person.
     * 
     * @param mother Biological mother
     * @param father Biological father
     */
    public void setParents(Person mother, Person father) {
        this.biologicalMother = mother;
        this.biologicalFather = father;
    }

    // Life Cycle Methods
    
    /**
     * Ages the person by one year.
     */
    public void ageOneYear() {
        this.ageInYears++;
    }

    /**
     * Processes the death of this person.
     * Handles all necessary relationship updates.
     */
    public void die() {
        this.livingStatus = false;
        
        // Handle widowhood
        if (this.marriagePartner != null) {
            this.marriagePartner.marriagePartner = null;
            this.marriagePartner = null;
        }
    }

    // Eligibility Check Methods
    
    /**
     * Determines if this person is eligible for marriage.
     * 
     * @return true if person meets all marriage eligibility criteria
     */
    public boolean isEligibleForMarriage() {
        return livingStatus && 
               marriagePartner == null && 
               ageInYears >= MINIMUM_MARRIAGE_AGE && 
               ageInYears <= MAXIMUM_MARRIAGE_AGE && 
               offspring.isEmpty();
    }

    /**
     * Determines if this person can have additional children.
     * 
     * @return true if person can have more children
     */
    public boolean canHaveMoreChildren() {
        return livingStatus && 
               marriagePartner != null && 
               marriagePartner.livingStatus && 
               offspring.size() < MAXIMUM_CHILDREN;
    }

    // Accessor Methods (Getters)
    public String getName() { return fullName; }
    public int getAge() { return ageInYears; }
    public Sex getSex() { return biologicalSex; }
    public boolean isAlive() { return livingStatus; }
    public boolean isBornOutsideVillage() { return outsiderOrigin; }
    public String getOccupation() { return primaryOccupation; }
    public Person getMarriedTo() { return marriagePartner; }
    public List<Person> getChildren() { return new ArrayList<>(offspring); }
    public boolean hasChildren() { return !offspring.isEmpty(); }
    public Person getMother() { return biologicalMother; }
    public Person getFather() { return biologicalFather; }
    
    /**
     * Gets a comprehensive display name with key attributes.
     * 
     * @return Formatted display name
     */
    public String getDetailedDisplayName() {
        return String.format("%s (%d, %s, %s)", 
            fullName, ageInYears, biologicalSex.getFullName(), getOriginStatus());
    }
    
    /**
     * Gets the origin status as a descriptive string.
     * 
     * @return "Outsider" or "Native"
     */
    public String getOriginStatus() {
        return outsiderOrigin ? "Outsider" : "Native";
    }
    
    /**
     * Gets a summary of the person's family status.
     * 
     * @return Description of marital and parental status
     */
    public String getFamilyStatus() {
        StringBuilder status = new StringBuilder();
        
        if (marriagePartner != null) {
            status.append("Married to ").append(marriagePartner.getName());
        } else {
            status.append("Unmarried");
        }
        
        if (!offspring.isEmpty()) {
            status.append(", ").append(offspring.size()).append(" child");
            if (offspring.size() > 1) status.append("ren");
        }
        
        return status.toString();
    }
    
    // Mutator Methods (Setters) - only for mutable properties
    public void setName(String newName) {
        validateName(newName);
        this.fullName = newName;
    }
    
    public void setOccupation(String newOccupation) {
        this.primaryOccupation = newOccupation != null ? newOccupation : "None";
    }

    @Override
    public String toString() {
        return String.format(
            "Person{name='%s', age=%d, sex=%s, status=%s, origin=%s, occupation='%s'}", 
            fullName, 
            ageInYears, 
            biologicalSex.getFullName(), 
            livingStatus ? "Living" : "Deceased", 
            getOriginStatus(),
            primaryOccupation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return ageInYears == person.ageInYears && 
               livingStatus == person.livingStatus && 
               outsiderOrigin == person.outsiderOrigin && 
               Objects.equals(fullName, person.fullName) && 
               biologicalSex == person.biologicalSex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, ageInYears, biologicalSex, livingStatus, outsiderOrigin);
    }
}
