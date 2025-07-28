package service;

import model.Person;
import java.util.Random;

/**
 * Service for handling demographic calculations like mortality rates.
 */
public class DemographicsService {
    private final Random random;
    
    // Age thresholds
    private static final int MORTALITY_START_AGE = 60;
    private static final int MORTALITY_CERTAIN_AGE = 70;
    
    // Child bearing ages
    private static final int MIN_SPOUSE_AGE = 18;
    private static final int MAX_SPOUSE_AGE = 29;
    
    public DemographicsService() {
        this(new Random());
    }
    
    public DemographicsService(Random random) {
        this.random = random;
    }
    
    /**
     * Calculates if a person should die based on their age.
     * Mortality increases linearly from age 60 to 70, then becomes certain.
     */
    public boolean shouldPersonDie(Person person) {
        if (!person.isAlive()) {
            return false;
        }
        
        int age = person.getAge();
        
        // No natural death before 60
        if (age < MORTALITY_START_AGE) {
            return false;
        }
        
        // Certain death after 70
        if (age > MORTALITY_CERTAIN_AGE) {
            return true;
        }
        
        // Linear increase in mortality rate from 60 to 70
        // At 60: 0% chance, at 70: 100% chance
        int ageDiff = age - MORTALITY_START_AGE;
        int mortalityChance = ageDiff * 10; // 0%, 10%, 20%, ..., 100%
        
        return random.nextInt(100) < mortalityChance;
    }
    
    /**
     * Generates a random age for a new spouse.
     */
    public int generateSpouseAge() {
        return MIN_SPOUSE_AGE + random.nextInt(MAX_SPOUSE_AGE - MIN_SPOUSE_AGE + 1);
    }
    
    /**
     * Determines if a marriage should occur this year.
     */
    public boolean shouldMarriageOccur(double marriageChance) {
        return random.nextDouble() < marriageChance;
    }
    
    /**
     * Determines if a child should be born this year.
     * Can be extended to include factors like parent ages, economic conditions, etc.
     */
    public boolean shouldChildBeBorn(Person father, Person mother) {
        if (!father.canHaveMoreChildren() || !mother.canHaveMoreChildren()) {
            return false;
        }
        
        // Simple implementation - always have children if possible
        // Could be enhanced with probabilistic model
        return true;
    }
    
    /**
     * Determines the sex of a new child.
     */
    public Person.Sex generateChildSex() {
        return random.nextBoolean() ? Person.Sex.MALE : Person.Sex.FEMALE;
    }
}
