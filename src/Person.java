import java.util.ArrayList;
import java.util.List;

public class Person {
    public enum Sex { MALE, FEMALE }

    private boolean bornOutsideVillage;
    private String name;
    private int age;
    private Sex sex;
    private boolean isAlive;
    private Person marriedTo;
    private List<Person> children;
    private String occupation;

    // Constructor
    public Person(boolean bornOutsideVillage, String name, int age, Sex sex, boolean isAlive, String occupation) {
        this.bornOutsideVillage = bornOutsideVillage;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.isAlive = isAlive;
        this.marriedTo = null;
        this.children = new ArrayList<>();
        this.occupation = occupation;
    }

    // Getters
    public boolean isBornOutsideVillage() {
        return bornOutsideVillage;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Person getMarriedTo() {
        return marriedTo;
    }

    public List<Person> getChildren() {
        return new ArrayList<>(children); // Return a copy to prevent external modification
    }

    public String getOccupation() {
        return occupation;
    }

    // Setters
    public void setBornOutsideVillage(boolean bornOutsideVillage) {
        this.bornOutsideVillage = bornOutsideVillage;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }

    public void setSex(Sex sex) {
        if (sex != null) {
            this.sex = sex;
        } else {
            throw new IllegalArgumentException("Sex cannot be null");
        }
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation != null ? occupation : "";
    }

    // Marriage logic: Ensures opposite sex and both are alive
    public void marry(Person spouse) {
        if (spouse == null) {
            throw new IllegalArgumentException("Spouse cannot be null");
        }
        if (!this.isAlive || !spouse.isAlive()) {
            throw new IllegalStateException("Cannot marry: one or both persons are not alive");
        }
        if (this.marriedTo != null || spouse.getMarriedTo() != null) {
            throw new IllegalStateException("Cannot marry: one or both persons are already married");
        }
        if (this.sex == spouse.getSex()) {
            throw new IllegalArgumentException("Cannot marry: spouses must be of opposite sex");
        }
        this.marriedTo = spouse;
        spouse.marriedTo = this;
    }

    // Divorce logic: Clears marriage for both
    public void divorce() {
        if (this.marriedTo != null) {
            Person spouse = this.marriedTo;
            this.marriedTo = null;
            spouse.marriedTo = null;
        }
    }

    // Add child to both parents
    public void addChild(Person child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (this.children.size() >= 2) {
            throw new IllegalStateException("Cannot add child: maximum of 2 children per parent");
        }
        this.children.add(child);
        if (this.marriedTo != null) {
            this.marriedTo.children.add(child);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person{name='").append(name)
          .append("', age=").append(age)
          .append(", sex=").append(sex)
          .append(", isAlive=").append(isAlive)
          .append(", bornOutsideVillage=").append(bornOutsideVillage)
          .append(", occupation='").append(occupation).append("'");
        if (marriedTo != null) {
            sb.append(", marriedTo='").append(marriedTo.getName()).append("'");
        }
        sb.append(", children=[");
        for (int i = 0; i < children.size(); i++) {
            sb.append(children.get(i).getName());
            if (i < children.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}