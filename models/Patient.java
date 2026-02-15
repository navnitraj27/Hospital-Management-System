package models;

public class Patient {
    private final String id;
    private final String name;
    private final int age;

    public Patient(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String toCSV() {
        return id + "," + name.replace(",", " ") + "," + age;
    }
    
    public static Patient fromCSV(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length < 3)
            return null;
        try {
            int age = Integer.parseInt(parts[2]);
            return new Patient(parts[0], parts[1], age);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Patient[" + id + "] " + name + ", age=" + age;
    }
}