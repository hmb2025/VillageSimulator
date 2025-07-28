import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomNamer {
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

    // Constructor
    public RandomNamer() {
        this.random = new Random();
    }

    // Method to get a random sex-appropriate name
    public String getRandomName(Person.Sex sex) {
        if (sex == null) {
            throw new IllegalArgumentException("Sex cannot be null");
        }
        List<String> names = (sex == Person.Sex.MALE) ? MALE_NAMES : FEMALE_NAMES;
        return names.get(random.nextInt(names.size()));
    }
}