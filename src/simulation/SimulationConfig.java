package simulation;

/**
 * Configuration settings for the village simulation.
 */
public class SimulationConfig {
    private final int maxYears;
    private final double marriageChance;
    private final boolean verboseOutput;
    
    // Default values
    public static final int DEFAULT_MAX_YEARS = 100;
    public static final double DEFAULT_MARRIAGE_CHANCE = 0.2;
    
    private SimulationConfig(Builder builder) {
        this.maxYears = builder.maxYears;
        this.marriageChance = builder.marriageChance;
        this.verboseOutput = builder.verboseOutput;
    }
    
    public static SimulationConfig createDefault() {
        return new Builder().build();
    }
    
    // Getters
    public int getMaxYears() { return maxYears; }
    public double getMarriageChance() { return marriageChance; }
    public boolean isVerboseOutput() { return verboseOutput; }
    
    // Builder pattern for easy configuration
    public static class Builder {
        private int maxYears = DEFAULT_MAX_YEARS;
        private double marriageChance = DEFAULT_MARRIAGE_CHANCE;
        private boolean verboseOutput = true;
        
        public Builder maxYears(int maxYears) {
            if (maxYears <= 0) {
                throw new IllegalArgumentException("Max years must be positive");
            }
            this.maxYears = maxYears;
            return this;
        }
        
        public Builder marriageChance(double marriageChance) {
            if (marriageChance < 0 || marriageChance > 1) {
                throw new IllegalArgumentException("Marriage chance must be between 0 and 1");
            }
            this.marriageChance = marriageChance;
            return this;
        }
        
        public Builder verboseOutput(boolean verboseOutput) {
            this.verboseOutput = verboseOutput;
            return this;
        }
        
        public SimulationConfig build() {
            return new SimulationConfig(this);
        }
    }
}
