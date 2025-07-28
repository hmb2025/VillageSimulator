package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a family unit in the village.
 * A family consists of parents and their unmarried children.
 */
public class Family {
    private final List<Person> parents;
    private final List<Person> children;

    public Family() {
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public void addParent(Person parent) {
        if (parent != null && parents.size() < 2) {
            parents.add(parent);
        }
    }

    public void addChild(Person child) {
        if (child != null) {
            children.add(child);
        }
    }

    public List<Person> getParents() {
        return new ArrayList<>(parents);
    }

    public List<Person> getChildren() {
        return new ArrayList<>(children);
    }

    public boolean isEmpty() {
        return parents.isEmpty();
    }

    public boolean hasMarriedParents() {
        return parents.size() == 2 && 
               parents.get(0).getMarriedTo() == parents.get(1);
    }

    /**
     * Formats the family information for display.
     */
    public String format(Person currentPlayer) {
        StringBuilder sb = new StringBuilder();
        
        // Format parents
        for (int i = 0; i < parents.size(); i++) {
            Person parent = parents.get(i);
            sb.append(formatPerson(parent));
            
            if (parent == currentPlayer) {
                sb.append(" [Player]");
            }
            
            if (i == 0 && parents.size() == 2) {
                sb.append(" & ");
            }
        }
        
        // Check if spouse is player
        if (parents.size() == 2 && parents.get(1) == currentPlayer) {
            sb.append(" [Spouse is Player]");
        }
        
        // Format children
        if (!children.isEmpty()) {
            sb.append(", Children: ");
            for (int i = 0; i < children.size(); i++) {
                Person child = children.get(i);
                sb.append(formatPerson(child));
                
                if (child == currentPlayer) {
                    sb.append(" [Player]");
                }
                
                if (i < children.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        
        return sb.toString();
    }

    private String formatPerson(Person person) {
        return String.format("%s (%d, %s)", 
            person.getName(), 
            person.getAge(), 
            person.getSex()
        );
    }
}
