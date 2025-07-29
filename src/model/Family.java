package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Formats the family information for display with improved details.
     */
    public String formatDetailed(Person currentPlayer) {
        StringBuilder sb = new StringBuilder();
        
        // Format parents
        sb.append("  Parents: ");
        for (int i = 0; i < parents.size(); i++) {
            Person parent = parents.get(i);
            sb.append(formatPersonDetailed(parent));
            
            if (parent == currentPlayer) {
                sb.append(" [PLAYER]");
            } else if (parents.size() == 2 && parents.get(1 - i) == currentPlayer) {
                sb.append(" [SPOUSE]");
            }
            
            if (i == 0 && parents.size() == 2) {
                sb.append(" & ");
            }
        }
        
        // Add parent origins if they exist
        if (!parents.isEmpty()) {
            sb.append("\n    Parent Origins: ");
            for (int i = 0; i < parents.size(); i++) {
                Person parent = parents.get(i);
                if (parent.isBornOutsideVillage()) {
                    sb.append(parent.getName()).append(" - ").append(parent.getOriginStatus());
                } else {
                    sb.append(parent.getName()).append(" - ").append(parent.getOriginStatus());
                    // Always show parents for natives 
                    if (parent.getFather() != null && parent.getMother() != null) {
                        sb.append(" (Parents: ");
                        sb.append(parent.getFather().getName());
                        sb.append(" & ").append(parent.getMother().getName());
                        sb.append(")");
                    } else if (!parent.isBornOutsideVillage()) {
                        sb.append(" (Parents: Unknown - missing data)");
                    }
                }
                if (i < parents.size() - 1) sb.append("; ");
            }
        }
        
        // Format children
        if (!children.isEmpty()) {
            sb.append("\n  Children (").append(children.size()).append("): ");
            for (int i = 0; i < children.size(); i++) {
                Person child = children.get(i);
                sb.append(formatPersonDetailed(child));
                
                if (child == currentPlayer) {
                    sb.append(" [PLAYER]");
                }
                
                if (i < children.size() - 1) {
                    sb.append(", ");
                }
            }
        } else {
            sb.append("\n  Children: None");
        }
        
        return sb.toString();
    }

    private String formatPersonDetailed(Person person) {
        return String.format("%s (%d, %s, %s)", 
            person.getName(), 
            person.getAge(), 
            person.getSex(),
            person.getOccupation()
        );
    }

    /**
     * Formats the family information in a compact single-line format.
     */
    public String formatCompact(Person currentPlayer) {
        StringBuilder sb = new StringBuilder();
        
        // Format parents
        for (int i = 0; i < parents.size(); i++) {
            Person parent = parents.get(i);
            sb.append(formatPersonCompact(parent));
            
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
                sb.append(formatPersonCompact(child));
                
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

    private String formatPersonCompact(Person person) {
        return String.format("%s (%d, %s)", 
            person.getName(), 
            person.getAge(), 
            person.getSex()
        );
    }
}
