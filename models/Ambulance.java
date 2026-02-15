package models;

public class Ambulance {
    private final String id;
    private String status;  
    private final String driver;
    private final String phone;

    public Ambulance(String id, String status, String driver, String phone) {
        this.id = id;
        this.status = status;
        this.driver = driver;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriver() {
        return driver;
    }

    public String getPhone() {
        return phone;
    }

    public String toCSV() {
        return id + "," + status + "," + driver.replace(",", " ") + "," + phone;
    }

    public static Ambulance fromCSV(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length < 4) return null;
        return new Ambulance(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "Ambulance[" + id + "] Status: " + status + " | Driver: " + driver + " | Phone: " + phone;
    }
}
