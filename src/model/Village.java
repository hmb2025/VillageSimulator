package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the village and manages all villagers.
 * This class provides a central place for villager management and queries.
 */
public class Village {
    private final List<Person> villagers;
    private final String name;

    public Village(String name) {
        this.name = name;
        this.villagers = new ArrayList<>();
    }

    /**
     * Adds a new villager to the village.
     */
    public void addVillager(Person person) {
        if (person != null && !villagers.contains(person)) {
            villagers.add(person);
        }
    }

    /**
     * Removes deceased villagers from the village records.
     */
    public void removeDeceased() {
        villagers.removeIf(person -> !person.isAlive());
    }

    /**
     * Gets all living villagers.
     */
    public List<Person> getLivingVillagers() {
        return villagers.stream()
                .filter(Person::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Gets the total population (living villagers).
     */
    public int getPopulation() {
        return (int) villagers.stream()
                .filter(Person::isAlive)
                .count();
    }

    /**
     * Finds all eligible marriage candidates for a given person.
     */
    public List<Person> findEligibleSpouses(Person person) {
        if (person == null || !person.isEligibleForMarriage()) {
            return new ArrayList<>();
        }

        return villagers.stream()
                .filter(candidate -> isValidMarriageCandidate(person, candidate))
                .collect(Collectors.toList());
    }

    /**
     * Checks if two people are valid marriage candidates.
     */
    private boolean isValidMarriageCandidate(Person person, Person candidate) {
        if (candidate == person) return false;
        if (candidate.getSex() == person.getSex()) return false;
        if (!candidate.isEligibleForMarriage()) return false;
        
        return !areCloseRelatives(person, candidate);
    }

    /**
     * Checks if two people are close relatives (up to 2nd cousins).
     */
    public boolean areCloseRelatives(Person p1, Person p2) {
        // Check if one is ancestor of the other
        Set<Person> p1Ancestors = findAllAncestors(p1);
        Set<Person> p2Ancestors = findAllAncestors(p2);
        
        if (p1Ancestors.contains(p2) || p2Ancestors.contains(p1)) {
            return true;
        }

        // Check if they share parents (siblings)
        Set<Person> p1Parents = findParents(p1);
        Set<Person> p2Parents = findParents(p2);
        
        if (!Collections.disjoint(p1Parents, p2Parents)) {
            return true;
        }

        // Check if they share grandparents (first cousins)
        Set<Person> p1Grandparents = findGrandparents(p1);
        Set<Person> p2Grandparents = findGrandparents(p2);
        
        return !Collections.disjoint(p1Grandparents, p2Grandparents);
    }

    /**
     * Finds all parents of a person using the new parent tracking.
     */
    private Set<Person> findParents(Person person) {
        Set<Person> parents = new HashSet<>();
        
        // Use the new parent tracking first
        if (person.getMother() != null) {
            parents.add(person.getMother());
        }
        if (person.getFather() != null) {
            parents.add(person.getFather());
        }
        
        // Fallback to old method if no parent tracking exists
        if (parents.isEmpty()) {
            parents.addAll(villagers.stream()
                .filter(v -> v.getChildren().contains(person))
                .collect(Collectors.toSet()));
        }
        
        return parents;
    }

    /**
     * Finds all grandparents of a person.
     */
    private Set<Person> findGrandparents(Person person) {
        Set<Person> grandparents = new HashSet<>();
        for (Person parent : findParents(person)) {
            grandparents.addAll(findParents(parent));
        }
        return grandparents;
    }

    /**
     * Finds all ancestors of a person recursively.
     */
    private Set<Person> findAllAncestors(Person person) {
        Set<Person> ancestors = new HashSet<>();
        Set<Person> parents = findParents(person);
        
        for (Person parent : parents) {
            ancestors.add(parent);
            ancestors.addAll(findAllAncestors(parent));
        }
        
        return ancestors;
    }

    /**
     * Gets all families in the village.
     */
    public List<Family> getFamilies() {
        List<Family> families = new ArrayList<>();
        Set<Person> processed = new HashSet<>();

        for (Person person : getLivingVillagers()) {
            if (processed.contains(person)) continue;

            Family family = new Family();
            
            if (person.getMarriedTo() != null && person.getMarriedTo().isAlive()) {
                // Married couple family
                family.addParent(person);
                family.addParent(person.getMarriedTo());
                processed.add(person);
                processed.add(person.getMarriedTo());
                
                // Add unmarried children
                for (Person child : person.getChildren()) {
                    if (child.isAlive() && child.getMarriedTo() == null) {
                        family.addChild(child);
                    }
                }
            } else if (person.hasChildren() && person.getMarriedTo() == null) {
                // Single parent family (widowed with children)
                family.addParent(person);
                processed.add(person);
                
                // Add unmarried children
                for (Person child : person.getChildren()) {
                    if (child.isAlive() && child.getMarriedTo() == null) {
                        family.addChild(child);
                    }
                }
            }
            
            if (!family.isEmpty()) {
                families.add(family);
            }
        }

        return families;
    }

    public String getName() {
        return name;
    }

    public List<Person> getAllVillagers() {
        return new ArrayList<>(villagers);
    }
}
