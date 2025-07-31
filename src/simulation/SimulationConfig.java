package simulation;

/**
 * Comprehensive configuration settings for the village simulation.
 * Centralizes all simulation parameters for easy tuning and what-if scenarios.
 * Uses builder pattern for flexible configuration.
 */
public class SimulationConfig {
    
    // ═══════════════════════════════════════════════════════════════════
    // SIMULATION TIME PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    // Core simulation duration settings
    private final int maximumSimulationYears;
    public static final int DEFAULT_MAXIMUM_YEARS = 150;
    public static final int MINIMUM_SIMULATION_YEARS = 1;
    public static final int MAXIMUM_SIMULATION_YEARS_LIMIT = 1000;
    
    // Quick testing presets
    public static final int QUICK_TEST_YEARS = 50;
    public static final int LONG_TERM_YEARS = 500;
    
    // ═══════════════════════════════════════════════════════════════════
    // DEMOGRAPHICS PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    // Age boundaries and life stages
    private final int minimumMarriageAge;
    private final int maximumMarriageAge;
    private final int mortalityOnsetAge;
    private final int mortalityCertaintyAge;
    private final int adultAge;
    private final int elderAge;
    
    // Default age settings
    public static final int DEFAULT_MINIMUM_MARRIAGE_AGE = 18;
    public static final int DEFAULT_MAXIMUM_MARRIAGE_AGE = 29;
    public static final int DEFAULT_MORTALITY_ONSET_AGE = 60;
    public static final int DEFAULT_MORTALITY_CERTAINTY_AGE = 70;
    public static final int DEFAULT_ADULT_AGE = 18;
    public static final int DEFAULT_ELDER_AGE = 60;
    
    // Age generation ranges for initial population
    private final int initialPopulationMinAge;
    private final int initialPopulationMaxAge;
    public static final int DEFAULT_INITIAL_POP_MIN_AGE = 18;
    public static final int DEFAULT_INITIAL_POP_MAX_AGE = 29;
    
    // ═══════════════════════════════════════════════════════════════════
    // MARRIAGE AND RELATIONSHIP PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final double annualMarriageProbability;
    private final double outsiderMarriageThreshold;
    private final int outsiderMarriageMinAge;
    
    // Default marriage settings
    public static final double DEFAULT_MARRIAGE_PROBABILITY = 0.20;
    public static final double DEFAULT_OUTSIDER_MARRIAGE_THRESHOLD = 0.30;
    public static final int DEFAULT_OUTSIDER_MARRIAGE_MIN_AGE = 25;
    
    // Marriage probability ranges (for validation)
    public static final double MINIMUM_MARRIAGE_PROBABILITY = 0.0;
    public static final double MAXIMUM_MARRIAGE_PROBABILITY = 1.0;
    
    // Quick test and long-term presets
    public static final double QUICK_TEST_MARRIAGE_PROBABILITY = 0.30;
    public static final double LONG_TERM_MARRIAGE_PROBABILITY = 0.15;
    
    // ═══════════════════════════════════════════════════════════════════
    // FERTILITY AND BIRTH PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final int maximumChildrenPerFamily;
    private final int playerLineageChildLimit;
    private final double baseBirthProbability;
    private final double maleChildProbability;
    
    // Default fertility settings
    public static final int DEFAULT_MAX_CHILDREN_PER_FAMILY = 2;
    public static final int DEFAULT_PLAYER_LINEAGE_CHILD_LIMIT = 1;
    public static final double DEFAULT_BASE_BIRTH_PROBABILITY = 1.0; // 100% if eligible
    public static final double DEFAULT_MALE_CHILD_PROBABILITY = 0.5; // 50/50 gender split
    
    // ═══════════════════════════════════════════════════════════════════
    // MORTALITY PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final int mortalityRiskIncreasePerYear;
    public static final int DEFAULT_MORTALITY_RISK_INCREASE_PER_YEAR = 10; // Percentage
    
    // ═══════════════════════════════════════════════════════════════════
    // INITIAL POPULATION PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final int minimumStartingCouples;
    private final int maximumStartingCouples;
    private final int initialPlayerAge;
    
    // Default initial population settings
    public static final int DEFAULT_MIN_STARTING_COUPLES = 0;
    public static final int DEFAULT_MAX_STARTING_COUPLES = 10;
    public static final int DEFAULT_INITIAL_PLAYER_AGE = 18;
    
    // Settlement type thresholds
    public static final int SOLO_SETTLEMENT_COUPLES = 0;
    public static final int FARMSTEAD_MIN_COUPLES = 1;
    public static final int FARMSTEAD_MAX_COUPLES = 2;
    public static final int THORP_MIN_COUPLES = 3;
    public static final int THORP_MAX_COUPLES = 4;
    public static final int HAMLET_MIN_COUPLES = 5;
    public static final int HAMLET_MAX_COUPLES = 9;
    public static final int VILLAGE_MIN_COUPLES = 10;
    
    // ═══════════════════════════════════════════════════════════════════
    // PLAYER CHARACTER PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final String defaultPlayerOccupation;
    private final String defaultVillageName;
    
    // Default player settings
    public static final String DEFAULT_PLAYER_OCCUPATION = "Farmer";
    public static final String DEFAULT_VILLAGE_NAME = "Haven";
    
    // ═══════════════════════════════════════════════════════════════════
    // REPORTING AND UI PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    private final boolean verboseReportingEnabled;
    private final int reportColumnWidth;
    private final int majorDividerWidth;
    private final int minorDividerWidth;
    private final int subDividerWidth;
    
    // Default reporting settings
    public static final boolean DEFAULT_VERBOSE_REPORTING = true;
    public static final int DEFAULT_REPORT_COLUMN_WIDTH = 80;
    public static final int DEFAULT_MAJOR_DIVIDER_WIDTH = 80;
    public static final int DEFAULT_MINOR_DIVIDER_WIDTH = 80;
    public static final int DEFAULT_SUB_DIVIDER_WIDTH = 40;
    
    // ═══════════════════════════════════════════════════════════════════
    // VALIDATION PARAMETERS
    // ═══════════════════════════════════════════════════════════════════
    
    // Name and text validation
    public static final int MAXIMUM_NAME_LENGTH = 50;
    public static final int MAXIMUM_PERSON_AGE = 150;
    
    // ═══════════════════════════════════════════════════════════════════
    // CONSTRUCTOR (PRIVATE - USE BUILDER)
    // ═══════════════════════════════════════════════════════════════════
    
    private SimulationConfig(Builder builder) {
        // Time parameters
        this.maximumSimulationYears = builder.maximumSimulationYears;
        
        // Demographics parameters
        this.minimumMarriageAge = builder.minimumMarriageAge;
        this.maximumMarriageAge = builder.maximumMarriageAge;
        this.mortalityOnsetAge = builder.mortalityOnsetAge;
        this.mortalityCertaintyAge = builder.mortalityCertaintyAge;
        this.adultAge = builder.adultAge;
        this.elderAge = builder.elderAge;
        this.initialPopulationMinAge = builder.initialPopulationMinAge;
        this.initialPopulationMaxAge = builder.initialPopulationMaxAge;
        
        // Marriage parameters
        this.annualMarriageProbability = builder.annualMarriageProbability;
        this.outsiderMarriageThreshold = builder.outsiderMarriageThreshold;
        this.outsiderMarriageMinAge = builder.outsiderMarriageMinAge;
        
        // Fertility parameters
        this.maximumChildrenPerFamily = builder.maximumChildrenPerFamily;
        this.playerLineageChildLimit = builder.playerLineageChildLimit;
        this.baseBirthProbability = builder.baseBirthProbability;
        this.maleChildProbability = builder.maleChildProbability;
        
        // Mortality parameters
        this.mortalityRiskIncreasePerYear = builder.mortalityRiskIncreasePerYear;
        
        // Initial population parameters
        this.minimumStartingCouples = builder.minimumStartingCouples;
        this.maximumStartingCouples = builder.maximumStartingCouples;
        this.initialPlayerAge = builder.initialPlayerAge;
        
        // Player character parameters
        this.defaultPlayerOccupation = builder.defaultPlayerOccupation;
        this.defaultVillageName = builder.defaultVillageName;
        
        // Reporting parameters
        this.verboseReportingEnabled = builder.verboseReportingEnabled;
        this.reportColumnWidth = builder.reportColumnWidth;
        this.majorDividerWidth = builder.majorDividerWidth;
        this.minorDividerWidth = builder.minorDividerWidth;
        this.subDividerWidth = builder.subDividerWidth;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PRESET CONFIGURATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Creates a configuration with all default values.
     */
    public static SimulationConfig createDefault() {
        return new Builder().build();
    }
    
    /**
     * Creates a configuration optimized for quick testing.
     * - Shorter duration (50 years)
     * - Higher marriage rate (30%)
     * - Verbose reporting enabled
     */
    public static SimulationConfig createQuickTest() {
        return new Builder()
            .withMaximumYears(QUICK_TEST_YEARS)
            .withMarriageProbability(QUICK_TEST_MARRIAGE_PROBABILITY)
            .withVerboseReporting(true)
            .build();
    }
    
    /**
     * Creates a configuration optimized for long-term simulation.
     * - Extended duration (500 years)  
     * - Lower marriage rate (15%)
     * - Minimal reporting for performance
     */
    public static SimulationConfig createLongTerm() {
        return new Builder()
            .withMaximumYears(LONG_TERM_YEARS)
            .withMarriageProbability(LONG_TERM_MARRIAGE_PROBABILITY)
            .withVerboseReporting(false)
            .build();
    }
    
    /**
     * Creates a configuration for high birth rate scenarios.
     * - Maximum children increased to 4
     * - Player lineage can have 2 children
     * - Higher marriage probability
     */
    public static SimulationConfig createHighBirthRate() {
        return new Builder()
            .withMaximumChildrenPerFamily(4)
            .withPlayerLineageChildLimit(2)
            .withMarriageProbability(0.35)
            .build();
    }
    
    /**
     * Creates a configuration for harsh survival scenarios.
     * - Earlier mortality onset (age 50)
     * - Lower marriage probability
     * - Single child limit for all families
     */
    public static SimulationConfig createHarshConditions() {
        return new Builder()
            .withMortalityOnsetAge(50)
            .withMortalityCertaintyAge(60)
            .withMaximumChildrenPerFamily(1)
            .withMarriageProbability(0.15)
            .build();
    }
    
    /**
     * Creates a configuration for extended lifespan scenarios.
     * - Later mortality onset (age 75)
     * - Death certainty at 90
     * - Extended marriage age range
     */
    public static SimulationConfig createExtendedLifespan() {
        return new Builder()
            .withMortalityOnsetAge(75)
            .withMortalityCertaintyAge(90)
            .withMaximumMarriageAge(40)
            .build();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ACCESSOR METHODS
    // ═══════════════════════════════════════════════════════════════════
    
    // Time parameters
    public int getMaximumSimulationYears() { return maximumSimulationYears; }
    
    // Demographics parameters
    public int getMinimumMarriageAge() { return minimumMarriageAge; }
    public int getMaximumMarriageAge() { return maximumMarriageAge; }
    public int getMortalityOnsetAge() { return mortalityOnsetAge; }
    public int getMortalityCertaintyAge() { return mortalityCertaintyAge; }
    public int getAdultAge() { return adultAge; }
    public int getElderAge() { return elderAge; }
    public int getInitialPopulationMinAge() { return initialPopulationMinAge; }
    public int getInitialPopulationMaxAge() { return initialPopulationMaxAge; }
    public int getInitialPopulationAgeRange() { 
        return initialPopulationMaxAge - initialPopulationMinAge + 1; 
    }
    
    // Marriage parameters
    public double getAnnualMarriageProbability() { return annualMarriageProbability; }
    public double getOutsiderMarriageThreshold() { return outsiderMarriageThreshold; }
    public int getOutsiderMarriageMinAge() { return outsiderMarriageMinAge; }
    
    // Fertility parameters
    public int getMaximumChildrenPerFamily() { return maximumChildrenPerFamily; }
    public int getPlayerLineageChildLimit() { return playerLineageChildLimit; }
    public double getBaseBirthProbability() { return baseBirthProbability; }
    public double getMaleChildProbability() { return maleChildProbability; }
    
    // Mortality parameters
    public int getMortalityRiskIncreasePerYear() { return mortalityRiskIncreasePerYear; }
    
    // Initial population parameters
    public int getMinimumStartingCouples() { return minimumStartingCouples; }
    public int getMaximumStartingCouples() { return maximumStartingCouples; }
    public int getInitialPlayerAge() { return initialPlayerAge; }
    
    // Player character parameters
    public String getDefaultPlayerOccupation() { return defaultPlayerOccupation; }
    public String getDefaultVillageName() { return defaultVillageName; }
    
    // Reporting parameters
    public boolean isVerboseReportingEnabled() { return verboseReportingEnabled; }
    public int getReportColumnWidth() { return reportColumnWidth; }
    public int getMajorDividerWidth() { return majorDividerWidth; }
    public int getMinorDividerWidth() { return minorDividerWidth; }
    public int getSubDividerWidth() { return subDividerWidth; }
    
    // ═══════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Determines settlement type based on number of couples.
     */
    public String getSettlementType(int couples) {
        if (couples == SOLO_SETTLEMENT_COUPLES) return "Solo Settlement";
        if (couples >= FARMSTEAD_MIN_COUPLES && couples <= FARMSTEAD_MAX_COUPLES) return "Farmstead";
        if (couples >= THORP_MIN_COUPLES && couples <= THORP_MAX_COUPLES) return "Thorp";
        if (couples >= HAMLET_MIN_COUPLES && couples <= HAMLET_MAX_COUPLES) return "Hamlet";
        if (couples >= VILLAGE_MIN_COUPLES) return "Village";
        return "Settlement";
    }
    
    /**
     * Gets a descriptive string of the mortality model.
     */
    public String getMortalityModelDescription() {
        return String.format(
            "Mortality Model: No death before age %d, " +
            "linear increase from %d-%d (%d%% per year), " +
            "certain death after age %d",
            mortalityOnsetAge,
            mortalityOnsetAge,
            mortalityCertaintyAge,
            mortalityRiskIncreasePerYear,
            mortalityCertaintyAge
        );
    }
    
    /**
     * Gets a descriptive string of marriage eligibility rules.
     */
    public String getMarriageRulesDescription() {
        return String.format(
            "Marriage Rules: Eligible age %d-%d, " +
            "must be unmarried, " +
            "cannot have existing children, " +
            "cannot marry close relatives, " +
            "annual probability: %.0f%%",
            minimumMarriageAge,
            maximumMarriageAge,
            annualMarriageProbability * 100
        );
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Builder class for flexible configuration construction.
     */
    public static class Builder {
        // Time parameters
        private int maximumSimulationYears = DEFAULT_MAXIMUM_YEARS;
        
        // Demographics parameters
        private int minimumMarriageAge = DEFAULT_MINIMUM_MARRIAGE_AGE;
        private int maximumMarriageAge = DEFAULT_MAXIMUM_MARRIAGE_AGE;
        private int mortalityOnsetAge = DEFAULT_MORTALITY_ONSET_AGE;
        private int mortalityCertaintyAge = DEFAULT_MORTALITY_CERTAINTY_AGE;
        private int adultAge = DEFAULT_ADULT_AGE;
        private int elderAge = DEFAULT_ELDER_AGE;
        private int initialPopulationMinAge = DEFAULT_INITIAL_POP_MIN_AGE;
        private int initialPopulationMaxAge = DEFAULT_INITIAL_POP_MAX_AGE;
        
        // Marriage parameters
        private double annualMarriageProbability = DEFAULT_MARRIAGE_PROBABILITY;
        private double outsiderMarriageThreshold = DEFAULT_OUTSIDER_MARRIAGE_THRESHOLD;
        private int outsiderMarriageMinAge = DEFAULT_OUTSIDER_MARRIAGE_MIN_AGE;
        
        // Fertility parameters
        private int maximumChildrenPerFamily = DEFAULT_MAX_CHILDREN_PER_FAMILY;
        private int playerLineageChildLimit = DEFAULT_PLAYER_LINEAGE_CHILD_LIMIT;
        private double baseBirthProbability = DEFAULT_BASE_BIRTH_PROBABILITY;
        private double maleChildProbability = DEFAULT_MALE_CHILD_PROBABILITY;
        
        // Mortality parameters
        private int mortalityRiskIncreasePerYear = DEFAULT_MORTALITY_RISK_INCREASE_PER_YEAR;
        
        // Initial population parameters
        private int minimumStartingCouples = DEFAULT_MIN_STARTING_COUPLES;
        private int maximumStartingCouples = DEFAULT_MAX_STARTING_COUPLES;
        private int initialPlayerAge = DEFAULT_INITIAL_PLAYER_AGE;
        
        // Player character parameters
        private String defaultPlayerOccupation = DEFAULT_PLAYER_OCCUPATION;
        private String defaultVillageName = DEFAULT_VILLAGE_NAME;
        
        // Reporting parameters
        private boolean verboseReportingEnabled = DEFAULT_VERBOSE_REPORTING;
        private int reportColumnWidth = DEFAULT_REPORT_COLUMN_WIDTH;
        private int majorDividerWidth = DEFAULT_MAJOR_DIVIDER_WIDTH;
        private int minorDividerWidth = DEFAULT_MINOR_DIVIDER_WIDTH;
        private int subDividerWidth = DEFAULT_SUB_DIVIDER_WIDTH;
        
        // Time parameter setters
        public Builder withMaximumYears(int years) {
            if (years < MINIMUM_SIMULATION_YEARS || years > MAXIMUM_SIMULATION_YEARS_LIMIT) {
                throw new IllegalArgumentException(
                    String.format("Maximum years must be between %d and %d (provided: %d)",
                        MINIMUM_SIMULATION_YEARS, MAXIMUM_SIMULATION_YEARS_LIMIT, years));
            }
            this.maximumSimulationYears = years;
            return this;
        }
        
        // Demographics parameter setters
        public Builder withMinimumMarriageAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid minimum marriage age: " + age);
            }
            this.minimumMarriageAge = age;
            return this;
        }
        
        public Builder withMaximumMarriageAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid maximum marriage age: " + age);
            }
            this.maximumMarriageAge = age;
            return this;
        }
        
        public Builder withMortalityOnsetAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid mortality onset age: " + age);
            }
            this.mortalityOnsetAge = age;
            return this;
        }
        
        public Builder withMortalityCertaintyAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid mortality certainty age: " + age);
            }
            this.mortalityCertaintyAge = age;
            return this;
        }
        
        public Builder withAdultAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid adult age: " + age);
            }
            this.adultAge = age;
            return this;
        }
        
        public Builder withElderAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid elder age: " + age);
            }
            this.elderAge = age;
            return this;
        }
        
        public Builder withInitialPopulationAgeRange(int minAge, int maxAge) {
            if (minAge < 0 || minAge > MAXIMUM_PERSON_AGE || 
                maxAge < 0 || maxAge > MAXIMUM_PERSON_AGE || 
                minAge > maxAge) {
                throw new IllegalArgumentException("Invalid initial population age range");
            }
            this.initialPopulationMinAge = minAge;
            this.initialPopulationMaxAge = maxAge;
            return this;
        }
        
        // Marriage parameter setters
        public Builder withMarriageProbability(double probability) {
            if (probability < MINIMUM_MARRIAGE_PROBABILITY || probability > MAXIMUM_MARRIAGE_PROBABILITY) {
                throw new IllegalArgumentException(
                    String.format("Marriage probability must be between %.2f and %.2f (provided: %.2f)",
                        MINIMUM_MARRIAGE_PROBABILITY, MAXIMUM_MARRIAGE_PROBABILITY, probability));
            }
            this.annualMarriageProbability = probability;
            return this;
        }
        
        public Builder withOutsiderMarriageThreshold(double threshold) {
            if (threshold < 0.0 || threshold > 1.0) {
                throw new IllegalArgumentException("Outsider marriage threshold must be between 0.0 and 1.0");
            }
            this.outsiderMarriageThreshold = threshold;
            return this;
        }
        
        public Builder withOutsiderMarriageMinAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid outsider marriage minimum age: " + age);
            }
            this.outsiderMarriageMinAge = age;
            return this;
        }
        
        // Fertility parameter setters
        public Builder withMaximumChildrenPerFamily(int maxChildren) {
            if (maxChildren < 0 || maxChildren > 20) {
                throw new IllegalArgumentException("Maximum children must be between 0 and 20");
            }
            this.maximumChildrenPerFamily = maxChildren;
            return this;
        }
        
        public Builder withPlayerLineageChildLimit(int limit) {
            if (limit < 0 || limit > 20) {
                throw new IllegalArgumentException("Player lineage child limit must be between 0 and 20");
            }
            this.playerLineageChildLimit = limit;
            return this;
        }
        
        public Builder withBaseBirthProbability(double probability) {
            if (probability < 0.0 || probability > 1.0) {
                throw new IllegalArgumentException("Birth probability must be between 0.0 and 1.0");
            }
            this.baseBirthProbability = probability;
            return this;
        }
        
        public Builder withMaleChildProbability(double probability) {
            if (probability < 0.0 || probability > 1.0) {
                throw new IllegalArgumentException("Male child probability must be between 0.0 and 1.0");
            }
            this.maleChildProbability = probability;
            return this;
        }
        
        // Mortality parameter setters
        public Builder withMortalityRiskIncreasePerYear(int percentage) {
            if (percentage < 0 || percentage > 100) {
                throw new IllegalArgumentException("Mortality risk increase must be between 0 and 100 percent");
            }
            this.mortalityRiskIncreasePerYear = percentage;
            return this;
        }
        
        // Initial population parameter setters
        public Builder withStartingCouplesRange(int min, int max) {
            if (min < 0 || max < 0 || min > max) {
                throw new IllegalArgumentException("Invalid starting couples range");
            }
            this.minimumStartingCouples = min;
            this.maximumStartingCouples = max;
            return this;
        }
        
        public Builder withInitialPlayerAge(int age) {
            if (age < 0 || age > MAXIMUM_PERSON_AGE) {
                throw new IllegalArgumentException("Invalid initial player age: " + age);
            }
            this.initialPlayerAge = age;
            return this;
        }
        
        // Player character parameter setters
        public Builder withDefaultPlayerOccupation(String occupation) {
            if (occupation == null || occupation.trim().isEmpty()) {
                throw new IllegalArgumentException("Player occupation cannot be null or empty");
            }
            this.defaultPlayerOccupation = occupation;
            return this;
        }
        
        public Builder withDefaultVillageName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Village name cannot be null or empty");
            }
            this.defaultVillageName = name;
            return this;
        }
        
        // Reporting parameter setters
        public Builder withVerboseReporting(boolean enabled) {
            this.verboseReportingEnabled = enabled;
            return this;
        }
        
        public Builder withReportColumnWidth(int width) {
            if (width < 40 || width > 200) {
                throw new IllegalArgumentException("Report column width must be between 40 and 200");
            }
            this.reportColumnWidth = width;
            return this;
        }
        
        public Builder withDividerWidths(int major, int minor, int sub) {
            if (major < 20 || major > 200 || minor < 20 || minor > 200 || sub < 10 || sub > 100) {
                throw new IllegalArgumentException("Invalid divider widths");
            }
            this.majorDividerWidth = major;
            this.minorDividerWidth = minor;
            this.subDividerWidth = sub;
            return this;
        }
        
        /**
         * Builds the configuration with the specified parameters.
         */
        public SimulationConfig build() {
            // Validate configuration coherence
            if (minimumMarriageAge > maximumMarriageAge) {
                throw new IllegalStateException("Minimum marriage age cannot exceed maximum marriage age");
            }
            if (mortalityOnsetAge > mortalityCertaintyAge) {
                throw new IllegalStateException("Mortality onset age cannot exceed mortality certainty age");
            }
            if (playerLineageChildLimit > maximumChildrenPerFamily) {
                throw new IllegalStateException("Player lineage limit cannot exceed maximum children per family");
            }
            
            return new SimulationConfig(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "SimulationConfig{maxYears=%d, marriageProbability=%.2f, " +
            "marriageAge=%d-%d, mortalityAge=%d-%d, maxChildren=%d, " +
            "verboseReporting=%s}",
            maximumSimulationYears, annualMarriageProbability,
            minimumMarriageAge, maximumMarriageAge,
            mortalityOnsetAge, mortalityCertaintyAge,
            maximumChildrenPerFamily, verboseReportingEnabled);
    }
}
