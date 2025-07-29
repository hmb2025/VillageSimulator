package service;

import model.Person;
import java.util.*;

/**
 * Service for generating random names for villagers.
 * This class provides culturally appropriate names based on sex.
 */
public class NameGenerator {
    private static final List<String> MALE_NAMES = Arrays.asList(
        "Adam", "Alaric", "Albert", "Alfred", "Alistair", "Ambrose", "Andrew", "Anthony", "Arnold", "Arthur",
        "Baldwin", "Bartholomew", "Benedict", "Bernard", "Bertram", "Boris", "Brian", "Caspian", "Charles", "Christopher",
        "Clement", "Conrad", "Constantine", "Cuthbert", "Cyril", "Damian", "Daniel", "David", "Dominic", "Duncan",
        "Edgar", "Edmund", "Edward", "Edwin", "Elias", "Elijah", "Eric", "Ernest", "Eugene", "Felix",
        "Ferdinand", "Francis", "Frederick", "Gabriel", "Gareth", "Geoffrey", "George", "Gerald", "Gilbert", "Godric",
        "Gregory", "Harold", "Harry", "Henry", "Herbert", "Herman", "Hugh", "Ian", "Isaac", "Isaiah",
        "James", "Jasper", "Jeremiah", "John", "Jonathan", "Joseph", "Julian", "Laurence", "Leo", "Leonard",
        "Lewis", "Liam", "Louis", "Luke", "Magnus", "Malcolm", "Martin", "Matthew", "Michael", "Nathaniel",
        "Nicholas", "Nigel", "Oliver", "Oswin", "Patrick", "Paul", "Peter", "Philip", "Ralph", "Raymond",
        "Reginald", "Richard", "Robert", "Roger", "Rupert", "Samuel", "Simon", "Stephen", "Theodore", "Thomas",
        "Victor", "Vincent", "Walter", "William"
    );

    private static final List<String> FEMALE_NAMES = Arrays.asList(
        "Adelaide", "Agnes", "Alice", "Amelia", "Anastasia", "Annabel", "Anne", "Beatrice", "Bridget", "Catherine",
        "Cecilia", "Charlotte", "Clara", "Clementine", "Constance", "Cora", "Daisy", "Dorothy", "Edith", "Eleanor",
        "Eliza", "Elizabeth", "Ella", "Ellen", "Eloise", "Elsie", "Emilia", "Emily", "Emma", "Esther",
        "Ethel", "Evangeline", "Evelyn", "Fiona", "Flora", "Florence", "Frances", "Genevieve", "Georgiana", "Gertrude",
        "Giselle", "Grace", "Hannah", "Harriet", "Hazel", "Helen", "Ida", "Irene", "Isabel", "Isadora",
        "Jane", "Jeanette", "Joan", "Josephine", "Judith", "Julia", "Katherine", "Laura", "Lillian", "Lily",
        "Louisa", "Lucy", "Lydia", "Mabel", "Margaret", "Maria", "Marianne", "Martha", "Mary", "Matilda",
        "Maud", "Mildred", "Millicent", "Miriam", "Nancy", "Naomi", "Nora", "Olive", "Patricia", "Pauline",
        "Pearl", "Penelope", "Phoebe", "Priscilla", "Rebecca", "Rose", "Rosemary", "Ruth", "Sarah", "Sophia",
        "Alize", "Susanna", "Sybil", "Theresa", "Victoria", "Violet", "Virginia", "Vivian", "Winifred", "Yvonne"
    );

    private final Random random;
    private final Set<String> usedNames;

    public NameGenerator() {
        this(new Random());
    }

    public NameGenerator(Random random) {
        this.random = random;
        this.usedNames = new HashSet<>();
    }

    /**
     * Generates a random name based on sex.
     * Tries to avoid recently used names when possible.
     */
    public String generateName(Person.Sex sex) {
        Objects.requireNonNull(sex, "Sex cannot be null");
        
        List<String> namePool = (sex == Person.Sex.MALE) ? MALE_NAMES : FEMALE_NAMES;
        List<String> availableNames = new ArrayList<>(namePool);
        
        // Remove recently used names if we have enough alternatives
        if (availableNames.size() > usedNames.size()) {
            availableNames.removeAll(usedNames);
        }
        
        // If all names have been used, clear the used names set
        if (availableNames.isEmpty()) {
            usedNames.clear();
            availableNames = new ArrayList<>(namePool);
        }
        
        String selectedName = availableNames.get(random.nextInt(availableNames.size()));
        usedNames.add(selectedName);
        
        // Keep the used names set from growing too large
        if (usedNames.size() > namePool.size() / 2) {
            usedNames.clear();
        }
        
        return selectedName;
    }

    /**
     * Gets a random occupation based on sex and era.
     */
    public String generateOccupation(Person.Sex sex) {
        List<String> occupations = (sex == Person.Sex.MALE) 
            ? Arrays.asList("Farmer", "Blacksmith", "Merchant", "Carpenter", "Miller", "Baker", "Fisherman", "Hunter", "Cook", "Miner", "Shepherd")
            : Arrays.asList("Homemaker", "Seamstress", "Merchant", "Herbalist", "Shepherd", "Baker", "Cook", "Farmer");
            
        return occupations.get(random.nextInt(occupations.size()));
    }

    /**
     * Resets the used names tracking.
     */
    public void resetUsedNames() {
        usedNames.clear();
    }
}
