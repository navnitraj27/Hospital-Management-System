package models;

public class Admission {
    private final String admissionId;
    private final String patientId;   // linked to patient module
    private final String patientName;
    private final String disease;     
    private String doctorId;    // linked to doctor module
    private String doctorName;
    private int daysInHospital;
    private double billAmount;
    private String status;  
    private boolean paid; 

    public Admission(String admissionId, String patientId, String patientName, String disease,
                     String doctorId, String doctorName, int daysInHospital, double billAmount, String status, boolean paid) {
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.disease = disease;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.daysInHospital = daysInHospital;
        this.billAmount = billAmount;
        this.status = status;
        this.paid = paid;
    }

    public String getAdmissionId() {
        return admissionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDisease() {
        return disease;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public int getDaysInHospital() {
        return daysInHospital;
    }

    public void setDaysInHospital(int days) {
        this.daysInHospital = days;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double amount) {
        this.billAmount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDoctorId(String id) {
        this.doctorId = id;
    }

    public void setDoctorName(String name) {
        this.doctorName = name;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String toCSV() {
        return admissionId + "," + patientId + "," + patientName.replace(",", " ") + "," +
               disease.replace(",", " ") + "," + doctorId + "," + doctorName.replace(",", " ") + "," +
               daysInHospital + "," + billAmount + "," + status + "," + paid;
    }

    public static Admission fromCSV(String line) {
        String[] parts = line.split(",", 10);
        if (parts.length < 9) return null;
        try {
            int days = Integer.parseInt(parts[6]);
            double bill = Double.parseDouble(parts[7]);
            String status = parts[8];
            boolean paid = false;
            if (parts.length >= 10) {
                paid = Boolean.parseBoolean(parts[9]);
            }
            return new Admission(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], days, bill, status, paid);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
         return "Admission[" + admissionId + "] Patient: " + patientName + " | Disease: " + disease +
             " | Doctor: " + doctorName + " | Days: " + daysInHospital + " | Bill: Rs." + billAmount + " | Status: " + status + " | Paid: " + (paid ? "Yes" : "No");
    }
}
