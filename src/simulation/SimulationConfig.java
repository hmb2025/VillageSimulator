package simulation;

/**
 * Configuration settings for the village simulation.
 * Uses builder pattern for flexible configuration with clear parameter names.
 */
public class SimulationConfig {
    // Configuration parameters with descriptive names
    private final int maximumSimulationYears;
    private final double annualMarriageProbability;
    private final boolean verboseReportingEnabled;
    
    // Default values clearly defined as constants
    public static final int DEFAULT_MAXIMUM_YEARS = 150;
    public static final double DEFAULT_MARRIAGE_PROBABILITY = 0.20;
    public static final boolean DEFAULT_VERBOSE_REPORTING = true;
    
    // Validation constants
    private static final int MINIMUM_SIMULATION_YEARS = 1;
    private static final int MAXIMUM_SIMULATION_YEARS = 1000;
    private static final double MINIMUM_MARRIAGE_PROBABILITY = 0.0;
    private static final double MAXIMUM_MARRIAGE_PROBABILITY = 1.0;
    
    private SimulationConfig(Builder builder) {
        this.maximumSimulationYears = builder.maximumSimulationYears;
        this.annualMarriageProbability = builder.annualMarriageProbability;
        this.verboseReportingEnabled = builder.verboseReportingEnabled;
    }
    
    /**
     * Creates a configuration with all default values.
     */
    public static SimulationConfig createDefault() {
        return new Builder().build();
    }
    
    /**
     * Creates a configuration optimized for quick testing.
     */
    public static SimulationConfig createQuickTest() {
        return new Builder()
            .withMaximumYears(50)
            .withMarriageProbability(0.30)
            .withVerboseReporting(true)
            .build();
    }
    
    /**
     * Creates a configuration optimized for long-term simulation.
     */
    public static SimulationConfig createLongTerm() {
        return new Builder()
            .withMaximumYears(500)
            .withMarriageProbability(0.15)
            .withVerboseReporting(false)
            .build();
    }
    
    // Accessor methods with clear names
    public int getMaximumSimulationYears() { 
        return maximumSimulationYears; 
    }
    
    public double getAnnualMarriageProbability() { 
        return annualMarriageProbability; 
    }
    
    public boolean isVerboseReportingEnabled() { 
        return verboseReportingEnabled; 
    }
    
    /**
     * Builder pattern for flexible configuration construction.
     */
    public static class Builder {
        private int maximumSimulationYears = DEFAULT_MAXIMUM_YEARS;
        private double annualMarriageProbability = DEFAULT_MARRIAGE_PROBABILITY;
        private boolean verboseReportingEnabled = DEFAULT_VERBOSE_REPORTING;
        
        /**
         * Sets the maximum number of years the simulation will run.
         * 
         * @param years Maximum years (must be between 1 and 1000)
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if years is out of valid range
         */
        public Builder withMaximumYears(int years) {
            if (years < MINIMUM_SIMULATION_YEARS || years > MAXIMUM_SIMULATION_YEARS) {
                throw new IllegalArgumentException(
                    String.format("Maximum years must be between %d and %d (provided: %d)",
                        MINIMUM_SIMULATION_YEARS, MAXIMUM_SIMULATION_YEARS, years));
            }
            this.maximumSimulationYears = years;
            return this;
        }
        
        /**
         * Sets the annual probability of marriage for eligible villagers.
         * 
         * @param probability Probability value (must be between 0.0 and 1.0)
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if probability is out of valid range
         */
        public Builder withMarriageProbability(double probability) {
            if (probability < MINIMUM_MARRIAGE_PROBABILITY || probability > MAXIMUM_MARRIAGE_PROBABILITY) {
                throw new IllegalArgumentException(
                    String.format("Marriage probability must be between %.2f and %.2f (provided: %.2f)",
                        MINIMUM_MARRIAGE_PROBABILITY, MAXIMUM_MARRIAGE_PROBABILITY, probability));
            }
            this.annualMarriageProbability = probability;
            return this;
        }
        
        /**
         * Sets whether verbose reporting should be enabled.
         * 
         * @param enabled True for detailed annual reports, false for summary only
         * @return This builder instance for method chaining
         */
        public Builder withVerboseReporting(boolean enabled) {
            this.verboseReportingEnabled = enabled;
            return this;
        }
        
        /**
         * Builds the configuration with the specified parameters.
         * 
         * @return A new SimulationConfig instance
         */
        public SimulationConfig build() {
            return new SimulationConfig(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "SimulationConfig{maxYears=%d, marriageProbability=%.2f, verboseReporting=%s}",
            maximumSimulationYears, annualMarriageProbability, verboseReportingEnabled);
    }
}
