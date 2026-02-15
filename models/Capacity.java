package models;

public class Capacity {
    private final int patientSeats;
    private int patientOccupied;
    private final int doctorSeats;
    private int doctorOccupied;
    private final int nurseSeats;
    private int nurseOccupied;

    public Capacity() {
        this.patientSeats = 20;
        this.patientOccupied = 0;
        this.doctorSeats = 3;
        this.doctorOccupied = 0;
        this.nurseSeats = 8;
        this.nurseOccupied = 0;
    }

    public Capacity(int patientSeats, int patientOccupied, int doctorSeats,
            int doctorOccupied, int nurseSeats, int nurseOccupied) {
        this.patientSeats = patientSeats;
        this.patientOccupied = patientOccupied;
        this.doctorSeats = doctorSeats;
        this.doctorOccupied = doctorOccupied;
        this.nurseSeats = nurseSeats;
        this.nurseOccupied = nurseOccupied;
    }

    public int getPatientSeats() {
        return patientSeats;
    }

    public int getPatientOccupied() {
        return patientOccupied;
    }

    public int getPatientAvailable() {
        return patientSeats - patientOccupied;
    }

    public boolean addPatient() {
        if (patientOccupied < patientSeats) {
            patientOccupied++;
            return true;
        }
        return false;
    }

    public void removePatient() {
        if (patientOccupied > 0) {
            patientOccupied--;
        }
    }

    public int getDoctorSeats() {
        return doctorSeats;
    }

    public int getDoctorOccupied() {
        return doctorOccupied;
    }

    public int getDoctorAvailable() {
        return doctorSeats - doctorOccupied;
    }

    public boolean addDoctor() {
        if (doctorOccupied < doctorSeats) {
            doctorOccupied++;
            return true;
        }
        return false;
    }

    public void removeDoctor() {
        if (doctorOccupied > 0) {
            doctorOccupied--;
        }
    }

    public int getNurseSeats() {
        return nurseSeats;
    }

    public int getNurseOccupied() {
        return nurseOccupied;
    }

    public int getNurseAvailable() {
        return nurseSeats - nurseOccupied;
    }

    public boolean addNurse() {
        if (nurseOccupied < nurseSeats) {
            nurseOccupied++;
            return true;
        }
        return false;
    }

    public void removeNurse() {
        if (nurseOccupied > 0) {
            nurseOccupied--;
        }
    }

    public String toCSV() {
        return patientSeats + "," + patientOccupied + "," + doctorSeats + "," +
                doctorOccupied + "," + nurseSeats + "," + nurseOccupied;
    }

    public static Capacity fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6)
            return new Capacity();
        try {
            return new Capacity(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]));
        } catch (NumberFormatException e) {
            return new Capacity();
        }
    }

    @Override
    public String toString() {
        return """
                HOSPITAL CAPACITY:
                Patients: %s/%s (%s available)
                Doctors: %s/%s (%s available)
                Nurses: %s/%s (%s available)
                """.formatted(
                patientOccupied, patientSeats, getPatientAvailable(),
                doctorOccupied, doctorSeats, getDoctorAvailable(),
                nurseOccupied, nurseSeats, getNurseAvailable());
    }
}
