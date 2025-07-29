package service;

import model.Person;
import java.util.Random;

/**
 * Service for handling demographic calculations including mortality rates,
 * birth probabilities, and marriage dynamics.
 * All constants and calculations are clearly documented for transparency.
 */
public class DemographicsService {
    private final Random randomGenerator;
    
    // Mortality Configuration - Clear age boundaries
    private static final int MORTALITY_ONSET_AGE = 60;  // Age when mortality risk begins
    private static final int MORTALITY_CERTAINTY_AGE = 70;  // Age when death becomes certain
    private static final int MORTALITY_RISK_INCREASE_PER_YEAR = 10;  // Percentage increase per year
    
    // Marriage Configuration - Eligible age range
    private static final int MINIMUM_SPOUSE_AGE = 18;
    private static final int MAXIMUM_SPOUSE_AGE = 29;
    
    // Birth Configuration
    private static final double BASE_BIRTH_PROBABILITY = 1.0;  // 100% chance if eligible
    private static final double MALE_BIRTH_PROBABILITY = 0.5;  // 50% chance for male child
    
    public DemographicsService() {
        this(new Random());
    }
    
    public DemographicsService(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
    
    /**
     * Calculates whether a person should die based on their age.
     * Mortality model: No death before 60, linear increase 60-70, certain death after 70.
     * 
     * @param person The person to evaluate
     * @return true if the person should die this year
     */
    public boolean shouldPersonDie(Person person) {
        if (!person.isAlive()) {
            return false;
        }
        
        int currentAge = person.getAge();
        
        // No natural death before mortality onset
        if (currentAge < MORTALITY_ONSET_AGE) {
            return false;
        }
        
        // Certain death after maximum age
        if (currentAge > MORTALITY_CERTAINTY_AGE) {
            return true;
        }
        
        // Calculate mortality probability (linear increase)
        // Age 60: 0%, Age 61: 10%, Age 62: 20%, ... Age 70: 100%
        int yearsAfterOnset = currentAge - MORTALITY_ONSET_AGE;
        int mortalityPercentage = yearsAfterOnset * MORTALITY_RISK_INCREASE_PER_YEAR;
        
        // Generate random number 0-99 and check against mortality percentage
        return randomGenerator.nextInt(100) < mortalityPercentage;
    }
    
    /**
     * Generates an appropriate age for a new spouse entering the village.
     * 
     * @return Age between minimum and maximum spouse age (inclusive)
     */
    public int generateSpouseAge() {
        int ageRange = MAXIMUM_SPOUSE_AGE - MINIMUM_SPOUSE_AGE + 1;
        return MINIMUM_SPOUSE_AGE + randomGenerator.nextInt(ageRange);
    }
    
    /**
     * Determines if a marriage should occur this year for an eligible person.
     * 
     * @param annualMarriageProbability The configured probability of marriage
     * @return true if marriage should occur
     */
    public boolean shouldMarriageOccur(double annualMarriageProbability) {
        return randomGenerator.nextDouble() < annualMarriageProbability;
    }
    
    /**
     * Determines if a child should be born to a married couple.
     * Currently implements a deterministic model (always have children if possible).
     * 
     * @param father The father in the couple
     * @param mother The mother in the couple
     * @return true if a child should be born
     */
    public boolean shouldChildBeBorn(Person father, Person mother) {
        // Validate basic eligibility
        if (!father.canHaveMoreChildren() || !mother.canHaveMoreChildren()) {
            return false;
        }
        
        // Current implementation: Always have children if eligible
        // This ensures population growth in the simulation
        return BASE_BIRTH_PROBABILITY > randomGenerator.nextDouble();
        
        // Alternative implementations could consider:
        // - Parent ages (reduced fertility with age)
        // - Economic factors (if implemented)
        // - Population pressure
        // - Random variation
    }
    
    /**
     * Determines the sex of a new child with equal probability.
     * 
     * @return Sex of the new child
     */
    public Person.Sex generateChildSex() {
        return randomGenerator.nextDouble() < MALE_BIRTH_PROBABILITY 
            ? Person.Sex.MALE 
            : Person.Sex.FEMALE;
    }
    
    /**
     * Calculates life expectancy for reporting purposes.
     * 
     * @return Average life expectancy based on mortality model
     */
    public double calculateLifeExpectancy() {
        // With current model: guaranteed survival to 60, average death at 65
        // This is a simplified calculation
        return (MORTALITY_ONSET_AGE + MORTALITY_CERTAINTY_AGE) / 2.0;
    }
    
    /**
     * Gets a descriptive string of the mortality model for reporting.
     * 
     * @return Description of mortality rules
     */
    public String getMortalityModelDescription() {
        return String.format(
            "Mortality Model: No death before age %d, " +
            "linear increase from %d-%d (10%% per year), " +
            "certain death after age %d",
            MORTALITY_ONSET_AGE,
            MORTALITY_ONSET_AGE,
            MORTALITY_CERTAINTY_AGE,
            MORTALITY_CERTAINTY_AGE
        );
    }
    
    /**
     * Gets a descriptive string of marriage eligibility rules.
     * 
     * @return Description of marriage rules
     */
    public String getMarriageRulesDescription() {
        return String.format(
            "Marriage Rules: Eligible age %d-%d, " +
            "must be unmarried, " +
            "cannot have existing children, " +
            "cannot marry close relatives",
            MINIMUM_SPOUSE_AGE,
            MAXIMUM_SPOUSE_AGE
        );
    }
}
