package service;

import model.Person;
import simulation.SimulationConfig;
import java.util.Random;

/**
 * Service for handling demographic calculations including mortality rates,
 * birth probabilities, and marriage dynamics.
 * All calculations use centralized configuration for easy tuning.
 */
public class DemographicsService {
    private final Random randomGenerator;
    private final SimulationConfig config;
    
    // Percentage calculation constants
    private static final int PERCENTAGE_MULTIPLIER = 100;
    
    public DemographicsService(SimulationConfig config) {
        this(new Random(), config);
    }
    
    public DemographicsService(Random randomGenerator, SimulationConfig config) {
        this.randomGenerator = randomGenerator;
        this.config = config;
    }
    
    /**
     * Calculates whether a person should die based on their age.
     * Mortality model: No death before onset age, linear increase to certainty age.
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
        if (currentAge < config.getMortalityOnsetAge()) {
            return false;
        }
        
        // Certain death after maximum age
        if (currentAge > config.getMortalityCertaintyAge()) {
            return true;
        }
        
        // Calculate mortality probability (linear increase)
        // Example: Age 60: 0%, Age 61: 10%, Age 62: 20%, ... Age 70: 100%
        int yearsAfterOnset = currentAge - config.getMortalityOnsetAge();
        int mortalityPercentage = yearsAfterOnset * config.getMortalityRiskIncreasePerYear();
        
        // Generate random number 0-99 and check against mortality percentage
        return randomGenerator.nextInt(PERCENTAGE_MULTIPLIER) < mortalityPercentage;
    }
    
    /**
     * Generates an appropriate age for a new spouse entering the village.
     * 
     * @return Age between minimum and maximum spouse age (inclusive)
     */
    public int generateSpouseAge() {
        int ageRange = config.getMaximumMarriageAge() - config.getMinimumMarriageAge() + 1;
        return config.getMinimumMarriageAge() + randomGenerator.nextInt(ageRange);
    }
    
    /**
     * Determines if a marriage should occur this year for an eligible person.
     * 
     * @return true if marriage should occur
     */
    public boolean shouldMarriageOccur() {
        return randomGenerator.nextDouble() < config.getAnnualMarriageProbability();
    }
    
    /**
     * Determines if a child should be born to a married couple.
     * Uses configuration to determine birth probability.
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
        
        // Use configured birth probability
        return config.getBaseBirthProbability() > randomGenerator.nextDouble();
        
        // Alternative implementations could consider:
        // - Parent ages (reduced fertility with age)
        // - Economic factors (if implemented)
        // - Population pressure
        // - Random variation
    }
    
    /**
     * Determines the sex of a new child using configured probability.
     * 
     * @return Sex of the new child
     */
    public Person.Sex generateChildSex() {
        return randomGenerator.nextDouble() < config.getMaleChildProbability() 
            ? Person.Sex.MALE 
            : Person.Sex.FEMALE;
    }
    
    /**
     * Calculates life expectancy for reporting purposes.
     * 
     * @return Average life expectancy based on mortality model
     */
    public double calculateLifeExpectancy() {
        // With current model: guaranteed survival to onset age, average death between onset and certainty
        return (config.getMortalityOnsetAge() + config.getMortalityCertaintyAge()) / 2.0;
    }
    
    /**
     * Gets a descriptive string of the mortality model for reporting.
     * 
     * @return Description of mortality rules
     */
    public String getMortalityModelDescription() {
        return config.getMortalityModelDescription();
    }
    
    /**
     * Gets a descriptive string of marriage eligibility rules.
     * 
     * @return Description of marriage rules
     */
    public String getMarriageRulesDescription() {
        return config.getMarriageRulesDescription();
    }
}
