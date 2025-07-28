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
    private static final double MARRIAGE_CHANCE = 0.2;

    public static void main(String[] args) {
        System.out.print("Enter starting player name (male): ");
        String playerName = scanner.nextLine();
        currentPlayer = new Person(true, playerName, 20, Person.Sex.MALE, true, "Farmer");
        villagers.add(currentPlayer);
        yearEvents.add(new ArrayList<>());

        while (currentYear < MAX_YEARS && currentPlayer != null) {
            simulateYear();
            printYearSummary();
            currentYear++;
            yearEvents.add(new ArrayList<>());
        }
    }

    private static void simulateYear() {
        List<Person> toRemove = new ArrayList<>();
        List<String> events = yearEvents.get(currentYear);

        // Handle aging and deaths
        for (Person person : new ArrayList<>(villagers)) {
            person.setAge(person.getAge() + 1);
            if (shouldDie(person)) {
                person.setAlive(false);
                toRemove.add(person);
                events.add(person.getName() + (person == currentPlayer ? " [Player]" : "") + " died (age " + person.getAge() + ")");
                if (person == currentPlayer) {
                    currentPlayer = !person.getChildren().isEmpty() ? person.getChildren().get(0) : null;
                    if (currentPlayer != null) {
                        events.add(currentPlayer.getName() + " became new player");
                    } else {
                        events.add("No heir found. Simulation ends.");
                    }
                }
            }
        }

        // Handle marriages
        for (Person person : new ArrayList<>(villagers)) {
            if (person.isAlive() && person.getMarriedTo() == null && person.getAge() >= 18 && random.nextDouble() < MARRIAGE_CHANCE) {
                Person spouse = marryPerson(person);
                if (spouse != null) {
                    events.add(person.getName() + " married " + spouse.getName() +
                            (spouse.isBornOutsideVillage() ? " (from outside village)" : ""));
                }
            }
        }

        // Handle childbirth
        for (Person person : new ArrayList<>(villagers)) {
            if (person.getMarriedTo() != null && person.isAlive() && person.getMarriedTo().isAlive() && person.getSex() == Person.Sex.MALE) {
                int maxChildren = (person == currentPlayer || person.getChildren().contains(currentPlayer)) ? 1 : 2;
                if (person.getChildren().size() < maxChildren && person.getMarriedTo().getChildren().size() < maxChildren) {
                    Person child = createChild(person);
                    events.add(child.getName() + " born to " + person.getName() + " & " + person.getMarriedTo().getName());
                }
            }
        }

        villagers.removeAll(toRemove);
    }

    private static boolean shouldDie(Person person) {
        if (!person.isAlive()) return false;
        int age = person.getAge();
        if (age >= 60 && age <= 70) return random.nextInt(100) < (age - 60) * 10;
        return age > 70;
    }

    private static Person marryPerson(Person person) {
        Person spouse = findEligibleSpouse(person);
        if (spouse == null) {
            Person.Sex spouseSex = person.getSex() == Person.Sex.MALE ? Person.Sex.FEMALE : Person.Sex.MALE;
            int spouseAge = 18 + random.nextInt(12); // 18-29
            String spouseName = namer.getRandomName(spouseSex);
            String occupation = spouseSex == Person.Sex.MALE ? "Farmer" : "Homemaker";
            spouse = new Person(true, spouseName, spouseAge, spouseSex, true, occupation);
            villagers.add(spouse);
        }
        try {
            person.marry(spouse);
            return spouse;
        } catch (IllegalStateException | IllegalArgumentException e) {
            return null;
        }
    }

    private static Person findEligibleSpouse(Person person) {
        List<Person> candidates = new ArrayList<>();
        for (Person villager : villagers) {
            if (isEligibleForMarriage(person, villager)) {
                candidates.add(villager);
            }
        }
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    private static boolean isEligibleForMarriage(Person person, Person candidate) {
        if (candidate.getSex() == person.getSex() || !candidate.isAlive() || candidate.getMarriedTo() != null ||
                candidate.getAge() < 18 || candidate.getAge() > 29 || !candidate.getChildren().isEmpty()) {
            return false;
        }
        return !isCloseRelative(person, candidate);
    }

    private static boolean isCloseRelative(Person p1, Person p2) {
        Set<Person> p1Ancestors = getAncestors(p1);
        Set<Person> p2Ancestors = getAncestors(p2);
        if (p1Ancestors.contains(p2) || p2Ancestors.contains(p1)) return true;
        Set<Person> p1Parents = getParents(p1);
        Set<Person> p2Parents = getParents(p2);
        p1Parents.retainAll(p2Parents);
        if (!p1Parents.isEmpty()) return true;
        Set<Person> p1Grandparents = getGrandparents(p1);
        Set<Person> p2Grandparents = getGrandparents(p2);
        p1Grandparents.retainAll(p2Grandparents);
        return !p1Grandparents.isEmpty();
    }

    private static Set<Person> getParents(Person person) {
        Set<Person> parents = new HashSet<>();
        for (Person villager : villagers) {
            if (villager.getChildren().contains(person)) parents.add(villager);
        }
        return parents;
    }

    private static Set<Person> getGrandparents(Person person) {
        Set<Person> grandparents = new HashSet<>();
        for (Person parent : getParents(person)) {
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

        System.out.println("Events:");
        List<String> events = yearEvents.get(currentYear);
        if (events.isEmpty()) {
            System.out.println("  None");
        } else {
            for (String event : events) {
                System.out.println("  " + event);
            }
        }

        System.out.println("Families:");
        List<Person> processed = new ArrayList<>();
        for (Person person : villagers) {
            if (!person.isAlive() || processed.contains(person)) continue;
            StringBuilder family = new StringBuilder();
            if (person.getMarriedTo() != null && person.getMarriedTo().isAlive()) {
                Person spouse = person.getMarriedTo();
                family.append(String.format("%s (%d, %s)", person.getName(), person.getAge(), person.getSex()))
                      .append(" & ").append(String.format("%s (%d, %s)", spouse.getName(), spouse.getAge(), spouse.getSex()));
                if (person == currentPlayer) family.append(" [Player]");
                else if (spouse == currentPlayer) family.append(" [Spouse is Player]");
                List<Person> unmarriedChildren = person.getChildren().stream()
                        .filter(child -> child.isAlive() && child.getMarriedTo() == null)
                        .limit(2).toList();
                if (!unmarriedChildren.isEmpty()) {
                    family.append(", Children: ");
                    for (int i = 0; i < unmarriedChildren.size(); i++) {
                        Person child = unmarriedChildren.get(i);
                        family.append(String.format("%s (%d, %s)", child.getName(), child.getAge(), child.getSex()));
                        if (child == currentPlayer) family.append(" [Player]");
                        if (i < unmarriedChildren.size() - 1) family.append(", ");
                    }
                }
                processed.add(person);
                processed.add(spouse);
            } else {
                family.append(String.format("%s (%d, %s)", person.getName(), person.getAge(), person.getSex()));
                if (person == currentPlayer) family.append(" [Player]");
                List<Person> unmarriedChildren = person.getChildren().stream()
                        .filter(child -> child.isAlive() && child.getMarriedTo() == null)
                        .limit(2).toList();
                if (!unmarriedChildren.isEmpty()) {
                    family.append(", Children: ");
                    for (int i = 0; i < unmarriedChildren.size(); i++) {
                        Person child = unmarriedChildren.get(i);
                        family.append(String.format("%s (%d, %s)", child.getName(), child.getAge(), child.getSex()));
                        if (child == currentPlayer) family.append(" [Player]");
                        if (i < unmarriedChildren.size() - 1) family.append(", ");
                    }
                }
                processed.add(person);
            }
            System.out.println("  " + family);
        }
        System.out.println("------------");
    }
}