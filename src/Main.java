import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static final RandomNamer namer = new RandomNamer();
    private static final List<Person> villagers = new ArrayList<>();
    private static Person currentPlayer = null;
    private static int currentYear = 0;
    private static final int MAX_YEARS = 100;
    private static final List<List<String>> yearEvents = new ArrayList<>();

    public static void main(String[] args) {
        // Get player name
        System.out.print("Enter starting player name (male): ");
        String playerName = scanner.nextLine();

        // Create initial player
        currentPlayer = new Person(true, playerName, 20, Person.Sex.MALE, true, "Farmer");
        villagers.add(currentPlayer);
        yearEvents.add(new ArrayList<>());

        // Simulate for 100 years
        while (currentYear < MAX_YEARS) {
            simulateYear();
            printYearSummary();
            currentYear++;
            yearEvents.add(new ArrayList<>());
        }
    }

    private static void simulateYear() {
        List<Person> toRemove = new ArrayList<>();
        List<String> events = yearEvents.get(currentYear);

        for (Person person : new ArrayList<>(villagers)) {
            // Age everyone
            person.setAge(person.getAge() + 1);

            // Check for death
            if (shouldDie(person)) {
                person.setAlive(false);
                toRemove.add(person);
                events.add(person.getName() + (person == currentPlayer ? " [Player]" : "") + " died (age " + person.getAge() + ")");
                if (person == currentPlayer && !person.getChildren().isEmpty()) {
                    currentPlayer = person.getChildren().get(0);
                    events.add(currentPlayer.getName() + " became new player");
                }
                continue;
            }

            // Handle marriage for unmarried males aged 18-29, including eligible widowers
            if (person.getSex() == Person.Sex.MALE && person.getMarriedTo() == null && person.isAlive()
                    && person.getAge() >= 18 && person.getAge() < 29 && (person.getChildren().isEmpty() || person.getMarriedTo() == null)) {
                Person spouse = marryPerson(person);
                if (spouse != null) {
                    events.add(person.getName() + " married " + spouse.getName());
                }
            }

            // Handle child birth for married couples
            if (person.getMarriedTo() != null && person.isAlive() && person.getMarriedTo().isAlive()) {
                if (person.getSex() == Person.Sex.MALE) {
                    int maxChildren = (person == currentPlayer || person.getChildren().contains(currentPlayer)) ? 1 : 2;
                    if (person.getChildren().size() < maxChildren) {
                        Person child = createChild(person);
                        events.add(child.getName() + " born to " + person.getName() + " & " + person.getMarriedTo().getName());
                    }
                }
            }
        }

        // Remove deceased villagers
        villagers.removeAll(toRemove);
    }

    private static boolean shouldDie(Person person) {
        if (!person.isAlive()) {
            return false;
        }
        int age = person.getAge();
        if (age >= 60 && age <= 70) {
            int deathChance = (age - 60) * 10;
            return random.nextInt(100) < deathChance;
        } else if (age > 70) {
            return true;
        }
        return false;
    }

    private static Person marryPerson(Person male) {
        Person spouse = findEligibleSpouse(male);
        if (spouse != null) {
            male.marry(spouse);
            return spouse;
        }
        // Create a new female spouse
        int minSpouseAge = 18;
        int maxSpouseAge = 29;
        int spouseAge = minSpouseAge + random.nextInt(maxSpouseAge - minSpouseAge + 1);
        String spouseName = namer.getRandomName(Person.Sex.FEMALE);
        spouse = new Person(false, spouseName, spouseAge, Person.Sex.FEMALE, true, "Homemaker");
        villagers.add(spouse);
        male.marry(spouse);
        return spouse;
    }

    private static Person findEligibleSpouse(Person male) {
        List<Person> candidates = new ArrayList<>();
        for (Person villager : villagers) {
            if (isEligibleForMarriage(male, villager)) {
                candidates.add(villager);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private static boolean isEligibleForMarriage(Person male, Person candidate) {
        if (candidate.getSex() != Person.Sex.FEMALE || !candidate.isAlive() || candidate.getMarriedTo() != null) {
            return false;
        }
        int minSpouseAge = 18;
        int maxSpouseAge = 29;
        if (candidate.getAge() < minSpouseAge || candidate.getAge() > maxSpouseAge) {
            return false;
        }
        if (!candidate.getChildren().isEmpty()) {
            return false; // No remarriage if candidate has children
        }
        return !isCloseRelative(male, candidate);
    }

    private static boolean isCloseRelative(Person p1, Person p2) {
        Set<Person> p1Ancestors = getAncestors(p1);
        Set<Person> p2Ancestors = getAncestors(p2);
        if (p1Ancestors.contains(p2) || p2Ancestors.contains(p1)) {
            return true;
        }
        Set<Person> p1Parents = getParents(p1);
        Set<Person> p2Parents = getParents(p2);
        p1Parents.retainAll(p2Parents);
        if (!p1Parents.isEmpty()) {
            return true;
        }
        Set<Person> p1Grandparents = getGrandparents(p1);
        Set<Person> p2Grandparents = getGrandparents(p2);
        p1Grandparents.retainAll(p2Grandparents);
        return !p1Grandparents.isEmpty();
    }

    private static Set<Person> getParents(Person person) {
        Set<Person> parents = new HashSet<>();
        for (Person villager : villagers) {
            if (villager.getChildren().contains(person)) {
                parents.add(villager);
            }
        }
        return parents;
    }

    private static Set<Person> getGrandparents(Person person) {
        Set<Person> grandparents = new HashSet<>();
        Set<Person> parents = getParents(person);
        for (Person parent : parents) {
            grandparents.addAll(getParents(parent));
        }
        return grandparents;
    }

    private static Set<Person> getAncestors(Person person) {
        Set<Person> ancestors = new HashSet<>();
        for (Person villager : villagers) {
            if (villager.getChildren().contains(person)) {
                ancestors.add(villager);
                ancestors.addAll(getAncestors(villager));
            }
        }
        return ancestors;
    }

    private static Person createChild(Person father) {
        Person mother = father.getMarriedTo();
        Person.Sex childSex = random.nextBoolean() ? Person.Sex.MALE : Person.Sex.FEMALE;
        String childName = namer.getRandomName(childSex);
        Person child = new Person(false, childName, 0, childSex, true, "None");
        father.addChild(child);
        villagers.add(child);
        return child;
    }

    private static void printYearSummary() {
        System.out.printf("\n=== Year %d ===%n", currentYear);
        long livingCount = villagers.stream().filter(Person::isAlive).count();
        System.out.printf("Population: %d%n", livingCount);

        // Events
        List<String> events = yearEvents.get(currentYear);
        System.out.println("Events:");
        if (events.isEmpty()) {
            System.out.println("  None");
        } else {
            for (String event : events) {
                System.out.println("  " + event);
            }
        }

        // Family units
        System.out.println("Families:");
        List<Person> processed = new ArrayList<>();
        for (Person person : villagers) {
            if (!person.isAlive() || processed.contains(person)) {
                continue;
            }
            if (person.getMarriedTo() != null && person.getMarriedTo().isAlive()) {
                // Married couple
                StringBuilder family = new StringBuilder();
                Person spouse = person.getMarriedTo();
                family.append(String.format("%s (%d, %s)", person.getName(), person.getAge(), person.getSex()));
                family.append(" & ").append(String.format("%s (%d, %s)", spouse.getName(), spouse.getAge(), spouse.getSex()));
                if (person == currentPlayer) {
                    family.append(" [Player]");
                } else if (spouse == currentPlayer) {
                    family.append(" [Spouse is Player]");
                }
                List<Person> unmarriedChildren = person.getChildren().stream()
                        .filter(child -> child.isAlive() && child.getMarriedTo() == null)
                        .limit(2)
                        .toList();
                if (!unmarriedChildren.isEmpty()) {
                    family.append(", Children: ");
                    for (int i = 0; i < unmarriedChildren.size(); i++) {
                        Person child = unmarriedChildren.get(i);
                        family.append(String.format("%s (%d, %s)", child.getName(), child.getAge(), child.getSex()));
                        if (child == currentPlayer) {
                            family.append(" [Player]");
                        }
                        if (i < unmarriedChildren.size() - 1) {
                            family.append(", ");
                        }
                    }
                }
                System.out.println("  " + family);
                processed.add(person);
                processed.add(spouse);
            } else if (person.getMarriedTo() == null || !person.getMarriedTo().isAlive()) {
                // Single or widowed adult
                StringBuilder family = new StringBuilder();
                family.append(String.format("%s (%d, %s)", person.getName(), person.getAge(), person.getSex()));
                if (person == currentPlayer) {
                    family.append(" [Player]");
                }
                List<Person> unmarriedChildren = person.getChildren().stream()
                        .filter(child -> child.isAlive() && child.getMarriedTo() == null)
                        .limit(2)
                        .toList();
                if (!unmarriedChildren.isEmpty()) {
                    family.append(", Children: ");
                    for (int i = 0; i < unmarriedChildren.size(); i++) {
                        Person child = unmarriedChildren.get(i);
                        family.append(String.format("%s (%d, %s)", child.getName(), child.getAge(), child.getSex()));
                        if (child == currentPlayer) {
                            family.append(" [Player]");
                        }
                        if (i < unmarriedChildren.size() - 1) {
                            family.append(",");
                        }
                    }
                }
                System.out.println("  " + family);
                processed.add(person);
            }
        }
        System.out.println("------------");
    }
}