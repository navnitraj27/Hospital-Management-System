import java.util.*;
import java.io.*;
import models.Doctor;
import models.Patient;
import models.Ambulance;
import models.Admission;
import models.Capacity;
import utils.FileUtil;
import utils.UserAuth;

public class HospitalManagement {
	Scanner sc = new Scanner(System.in);
	private final double BASE_CHARGE = 1000.0;
	private final double DAILY_RATE = 500.0;

	public void run() {
		try {
			FileUtil.ensureDataFolder();
			mainMenu();
		} catch (Exception e) {
			System.out.println("Fatal error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void mainMenu() {
		while (true) {
			System.out.println("\n--- Hospital Management ---");
			System.out.println("1. Faculty login (access doctor/patient data)");
			System.out.println("2. Patient signup (create patient account locally)");
			System.out.println("3. Ambulance Call");
			System.out.println("4. Patient Admission");
			System.out.println("5. View Bills");
			System.out.println("6. View Hospital Capacity");
			System.out.println("7. Discharge Patient");
			System.out.println("8. Exit");
			System.out.print("Choose: ");
			String c = sc.nextLine().trim();
			switch (c) {
				case "1":
					facultyFlow();
					break;
				case "2":
					patientSignup();
					break;
				case "3":
					ambulanceCall();
					break;
				case "4":
					patientAdmission();
					break;
				case "5":
					viewBills();
					break;
				case "6":
					viewCapacity();
					break;
				case "7":
					dischargePatient();
					break;
				case "8":
					System.out.println("Bye");
					return;
				default:
					System.out.println("Invalid choice");
			}
		}
	}

	private void facultyFlow() {
		try {
			System.out.print("Enter faculty password: ");
			String pwd = sc.nextLine();
			if (!UserAuth.verifyAdminPassword(pwd)) {
				System.out.println("Auth failed");
				return;
			}
			System.out.println("Authenticated. Choose action:");
			while (true) {
				System.out.println("1. Add doctor");
				System.out.println("2. View doctors");
				System.out.println("3. Add patient");
				System.out.println("4. View patients");
				System.out.println("5. Manage ambulances");
				System.out.println("6. View admissions");
				System.out.println("7. Logout");
				System.out.println("8. Remove doctor");
				System.out.print("Choose: ");
				String c = sc.nextLine().trim();
				switch (c) {
					case "1":
						addDoctor();
						break;
					case "2":
						viewDoctors();
						break;
					case "3":
						addPatient();
						break;
					case "4":
						viewPatients();
						break;
					case "5":
						manageAmbulances();
						break;
					case "6":
						viewAdmissions();
						break;
					case "7":
						return;
					case "8":
						removeDoctor();
						break;
					default:
						System.out.println("Invalid choice");
					}
			}
		} catch (Exception e) {
			System.out.println("Error in faculty flow: " + e.getMessage());
		}
	}

	private void addDoctor() {
		try {
			System.out.print("Doctor ID: ");
			String id = sc.nextLine();
			System.out.print("Name: ");
			String name = sc.nextLine();
			System.out.print("Specialization: ");
			String spec = sc.nextLine();
			Doctor d = new Doctor(id, name, spec);
			List<Doctor> list = FileUtil.readDoctors(decryptKey());
			list.add(d);
			FileUtil.writeDoctors(list, decryptKey());
			System.out.println("Doctor added.");
		} catch (Exception e) {
			System.out.println("Failed to add doctor: " + e.getMessage());
		}
	}

	private void viewDoctors() {
		try {
			List<Doctor> list = FileUtil.readDoctors(decryptKey());
			if (list.isEmpty())
				System.out.println("No doctors stored.");
			else
				list.forEach(d -> System.out.println(d));
		} catch (Exception e) {
			System.out.println("Failed to read doctors: " + e.getMessage());
		}
	}

	private void addPatient() {
		try {
			List<Doctor> doctors = FileUtil.readDoctors(decryptKey());
			if (doctors.isEmpty()) {
				System.out.println("Cannot add patient: no doctors are registered. Please add a doctor first.");
				return;
			}

			System.out.print("Patient ID: ");
			String id = sc.nextLine();
			System.out.print("Name: ");
			String name = sc.nextLine();
			System.out.print("Age: ");
			String ageS = sc.nextLine();
			int age = Integer.parseInt(ageS);
			Patient p = new Patient(id, name, age);
			List<Patient> list = FileUtil.readPatients(decryptKey());
			list.add(p);
			FileUtil.writePatients(list, decryptKey());
			System.out.println("Patient added.");
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid number for age.");
		} catch (Exception e) {
			System.out.println("Failed to add patient: " + e.getMessage());
		}
	}

	private void viewPatients() {
		try {
			List<Patient> list = FileUtil.readPatients(decryptKey());
			if (list.isEmpty())
				System.out.println("No patients stored.");
			else
				list.forEach(p -> System.out.println(p));
		} catch (Exception e) {
			System.out.println("Failed to read patients: " + e.getMessage());
		}
	}

	private String decryptKey() {
		return "";
	}

	private void patientSignup() {
		try {
			Capacity cap = readCapacity();
			if (cap.getPatientAvailable() <= 0) {
				System.out.println("Sorry! Hospital is at full capacity. No patient beds available.");
				System.out.println(cap);
				return;
			}

			List<Patient> existing = readPatients();
			int maxId = 0;
			for (Patient p : existing) {
				try {
					int id = Integer.parseInt(p.getId());
					if (id > maxId)
						maxId = id;
				} catch (NumberFormatException e) {
				}
			}
			String patientId = String.valueOf(maxId + 1);

			System.out.print("Patient Name: ");
			String name = sc.nextLine();
			System.out.print("Age: ");
			int age = Integer.parseInt(sc.nextLine());

			Patient p = new Patient(patientId, name, age);
			existing.add(p);
			writePatients(existing);

			cap.addPatient();
			writeCapacity(cap);

			File dir = new File("local_patients");
			if (!dir.exists())
				dir.mkdirs();
			String patientFilePath = "local_patients/" + patientId + ".txt";
			try (FileOutputStream fos = new FileOutputStream(patientFilePath)) {
				String content = "Patient ID: " + patientId + "\n" +
						"Name: " + name + "\n" +
						"Age: " + age + "\n" +
						"Stored: data/patients.txt\n";
				fos.write(content.getBytes("UTF-8"));
			} catch (Exception ex) {
				System.out.println("Warning: failed to write local patient file: " + ex.getMessage());
			}

			System.out.println("Patient registered successfully!");
			System.out.println("Patient ID: " + patientId);
			System.out.println("Name: " + name);
			System.out.println("Age: " + age);
			System.out.println("Local file created: " + patientFilePath);
			System.out.println("Note: Patient account also stored in data/patients.txt");
		} catch (NumberFormatException e) {
			System.out.println("Invalid age.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void ambulanceCall() {
		try {
			System.out.print("Enter pickup location: ");
			String location = sc.nextLine();

			String pwd = "admin"; 
			List<Ambulance> ambulances = FileUtil.readAmbulances(pwd);
			List<Ambulance> available = new ArrayList<>();
			for (Ambulance a : ambulances) {
				if ("available".equals(a.getStatus())) {
					available.add(a);
				}
			}

			if (available.isEmpty()) {
				System.out.println("ERROR: No ambulances available!");
				System.out.println("Please contact faculty to add ambulances first.");
				return;
			}

			System.out.println("\n--- Available Ambulances ---");
			for (int i = 0; i < available.size(); i++) {
				System.out.println((i + 1) + ". " + available.get(i));
			}
			System.out.print("Select ambulance (number): ");
			int choice = Integer.parseInt(sc.nextLine()) - 1;

			if (choice >= 0 && choice < available.size()) {
				Ambulance selected = available.get(choice);
				System.out.println("\n--- AMBULANCE DISPATCHED ---");
				System.out.println("Ambulance ID: " + selected.getId());
				System.out.println("Driver: " + selected.getDriver());
				System.out.println("Driver Phone: " + selected.getPhone());
				System.out.println("Pickup Location: " + location);
				System.out.println("Status: On the way!");
				System.out.println("-----------------------\n");

				selected.setStatus("busy");
				updateAmbulanceStatusDirect(selected, pwd);
			} else {
				System.out.println("Invalid selection.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void patientAdmission() {
		try {
			System.out.print("Enter patient ID: ");
			String patientId = sc.nextLine();

			Capacity cap = readCapacity();
			if (cap.getPatientAvailable() <= 0) {
				System.out.println("Sorry! Hospital is at full capacity. No patient beds available.");
				System.out.println(cap);
				return;
			}

			List<Admission> admissions = readAdmissions();
			int maxAdmissionId = 0;
			for (Admission ad : admissions) {
				try {
					int id = Integer.parseInt(ad.getAdmissionId());
					if (id > maxAdmissionId)
						maxAdmissionId = id;
				} catch (NumberFormatException e) {
				}
			}
			String admissionId = String.valueOf(maxAdmissionId + 1);

			List<Patient> patients = readPatients();
			Patient foundPatient = null;
			for (Patient p : patients) {
				if (p.getId().equals(patientId)) {
					foundPatient = p;
					break;
				}
			}

			if (foundPatient == null) {
				System.out.println("Patient not found!");
				return;
			}

			System.out.println("Patient: " + foundPatient.toString());
			System.out.print("Enter disease: ");
			String disease = sc.nextLine();
			List<Doctor> doctors = FileUtil.readDoctors(decryptKey());
			if (doctors.isEmpty()) {
				System.out.println("Cannot admit patient: no doctors are registered. Please add a doctor first.");
				return;
			}

			System.out.println("Available doctors:");
			for (Doctor d : doctors)
				System.out.println(d);
			System.out.print("Enter doctor ID: ");
			String doctorId = sc.nextLine().trim();
			String doctorName = null;
			for (Doctor d : doctors) {
				if (d.getId().equals(doctorId)) {
					doctorName = d.getName();
					break;
				}
			}
			if (doctorName == null || doctorName.isEmpty()) {
				System.out.print("Doctor not found. Enter doctor name (or leave blank to assign first doctor): ");
				doctorName = sc.nextLine().trim();
				if (doctorName.isEmpty() && !doctors.isEmpty()) {
					doctorName = doctors.get(0).getName();
					doctorId = doctors.get(0).getId();
				}
			}

			int daysInHospital = new Random().nextInt(19) + 2; 

			double billAmount = (daysInHospital * DAILY_RATE) + BASE_CHARGE;

			Admission admission = new Admission(admissionId, patientId, foundPatient.toString().split("] ")[1], disease,
					doctorId, doctorName, daysInHospital, billAmount, "admitted", false);

			admissions.add(admission);
			writeAdmissions(admissions);

			cap.addPatient();
			writeCapacity(cap);

			System.out.println("\nPatient admitted successfully!");
			System.out.println("Admission ID: " + admissionId);
			System.out.println("Disease: " + disease);
			System.out.println("Doctor: " + doctorName);
			System.out.println("Days in hospital: " + daysInHospital);
			System.out.println("Total Bill: Rs." + billAmount);
		} catch (NumberFormatException e) {
			System.out.println("Invalid input.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void viewBills() {
		try {
			System.out.print("Enter patient ID to view bill: ");
			String patientId = sc.nextLine();

			List<Admission> admissions = readAdmissions();
			boolean found = false;

			for (Admission a : admissions) {
				if (a.getPatientId().equals(patientId)) {
					found = true;
					System.out.println("\n--- HOSPITAL BILL ---");
					System.out.println("Admission ID: " + a.getAdmissionId());
					System.out.println("Patient: " + a.getPatientName());
					System.out.println("Disease: " + a.getDisease());
					System.out.println("Doctor: " + a.getDoctorName());
					System.out.println("Days in Hospital: " + a.getDaysInHospital());
					System.out.println("Rate: Rs.500/day + Rs.1000 base charge");
					System.out.println("Total Bill: Rs." + a.getBillAmount());
					System.out.println("Status: " + a.getStatus());
					System.out.println("-------------------\n");
				}
			}

			if (!found) {
				List<Patient> patients = readPatients();
				Patient foundPatient = null;
				for (Patient p : patients) {
					if (p.getId().equals(patientId)) {
						foundPatient = p;
						break;
					}
				}
				if (foundPatient == null) {
					System.out.println("No admission records found for patient ID: " + patientId);
					System.out.println("Also no patient registration found with this ID.");
					return;
				}

				System.out.println("No admission found for patient. Patient details:");
				System.out.println(foundPatient);
				System.out.print("Create an admission now for this patient? (y/n): ");
				String ans = sc.nextLine().trim().toLowerCase();
				if (ans.equals("y") || ans.equals("yes")) {
					createAdmissionForPatient(foundPatient);
				} else {
					System.out.println("No admission created.");
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void createAdmissionForPatient(Patient foundPatient) {
		try {
			String patientId = foundPatient.getId();
			List<Admission> admissions = readAdmissions();
			int maxAdmissionId = 0;
			for (Admission ad : admissions) {
				try {
					int id = Integer.parseInt(ad.getAdmissionId());
					if (id > maxAdmissionId)
						maxAdmissionId = id;
				} catch (NumberFormatException ex) {
				}
			}
			String admissionId = String.valueOf(maxAdmissionId + 1);

			System.out.print("Enter disease: ");
			String disease = sc.nextLine();
			List<Doctor> doctors = FileUtil.readDoctors(decryptKey());
			if (doctors.isEmpty()) {
				System.out.println("Cannot create admission: no doctors registered. Add a doctor first.");
				return;
			}
			System.out.println("Available doctors:");
			for (Doctor d : doctors)
				System.out.println(d);
			System.out.print("Enter doctor ID: ");
			String doctorId = sc.nextLine();
			System.out.print("Enter doctor name: ");
			String doctorName = sc.nextLine();
			System.out.print("Enter days in hospital: ");
			int daysInHospital = Integer.parseInt(sc.nextLine());

			double billAmount = (daysInHospital * DAILY_RATE) + BASE_CHARGE;
			Admission admission = new Admission(admissionId, patientId, foundPatient.toString().split("] ")[1], disease,
					doctorId, doctorName, daysInHospital, billAmount, "admitted", false);

			admissions.add(admission);
			writeAdmissions(admissions);

			Capacity cap = readCapacity();
			cap.addPatient();
			writeCapacity(cap);

			System.out.println("Admission created successfully. Admission ID: " + admissionId);
			System.out.println("Total Bill: Rs." + billAmount);
		} catch (Exception e) {
			System.out.println("Error creating admission: " + e.getMessage());
		}
	}

	private void manageAmbulances() {
		try {
			while (true) {
				System.out.println("\n--- Ambulance Management ---");
				System.out.println("1. Add ambulance");
				System.out.println("2. View ambulances");
				System.out.println("3. Mark as available");
				System.out.println("4. Back");
				System.out.print("Choose: ");
				String c = sc.nextLine().trim();

				switch (c) {
					case "1":
						addAmbulance();
						break;
					case "2":
						viewAmbulances();
						break;
					case "3":
						markAmbulanceAvailable();
						break;
					case "4":
						return;
					default:
						System.out.println("Invalid choice");
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void addAmbulance() {
		try {
			System.out.print("Ambulance ID: ");
			String id = sc.nextLine();
			System.out.print("Driver name: ");
			String driver = sc.nextLine();
			System.out.print("Driver phone: ");
			String phone = sc.nextLine();

			Ambulance a = new Ambulance(id, "available", driver, phone);
			List<Ambulance> list = readAmbulances();
			list.add(a);
			writeAmbulances(list);

			System.out.println("Ambulance added.");
		} catch (Exception e) {
			System.out.println("Failed to add ambulance: " + e.getMessage());
		}
	}

	private void viewAmbulances() {
		try {
			List<Ambulance> list = readAmbulances();
			if (list.isEmpty())
				System.out.println("No ambulances stored.");
			else {
				for (Ambulance a : list) {
					System.out.println(a);
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to read ambulances: " + e.getMessage());
		}
	}

	private void markAmbulanceAvailable() {
		try {
			System.out.print("Enter ambulance ID: ");
			String id = sc.nextLine();

			List<Ambulance> list = readAmbulances();
			for (Ambulance a : list) {
				if (a.getId().equals(id)) {
					a.setStatus("available");
					writeAmbulances(list);
					System.out.println("Ambulance " + id + " marked as available.");
					return;
				}
			}
			System.out.println("Ambulance not found.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void viewAdmissions() {
		try {
			List<Admission> list = readAdmissions();
			if (list.isEmpty())
				System.out.println("No admissions stored.");
			else {
				for (Admission a : list) {
					System.out.println(a);
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to read admissions: " + e.getMessage());
		}
	}

	private List<Ambulance> readAmbulances() throws Exception {
		return FileUtil.readAmbulances(decryptKey());
	}

	private void writeAmbulances(List<Ambulance> list) throws Exception {
		FileUtil.writeAmbulances(list, decryptKey());
	}

	private List<Admission> readAdmissions() throws Exception {
		return FileUtil.readAdmissions(decryptKey());
	}

	private void writeAdmissions(List<Admission> list) throws Exception {
		FileUtil.writeAdmissions(list, decryptKey());
	}

	private void updateAmbulanceStatus(Ambulance a) throws Exception {
		List<Ambulance> list = readAmbulances();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals(a.getId())) {
				list.set(i, a);
				break;
			}
		}
		writeAmbulances(list);
	}

	private void updateAmbulanceStatusDirect(Ambulance a, String password) throws Exception {
		List<Ambulance> list = FileUtil.readAmbulances(password);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals(a.getId())) {
				list.set(i, a);
				break;
			}
		}
		FileUtil.writeAmbulances(list, password);
	}

	private List<Patient> readPatients() throws Exception {
		return FileUtil.readPatients(decryptKey());
	}

	private void writePatients(List<Patient> list) throws Exception {
		FileUtil.writePatients(list, decryptKey());
	}

	private Capacity readCapacity() throws Exception {
		return FileUtil.readCapacity(decryptKey());
	}

	private void writeCapacity(Capacity cap) throws Exception {
		FileUtil.writeCapacity(cap, decryptKey());
	}

	private void viewCapacity() {
		try {
			Capacity cap = readCapacity();
			System.out.println("\n" + cap);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void dischargePatient() {
		try {
			System.out.print("Enter admission ID to discharge: ");
			String admissionId = sc.nextLine().trim();
			List<Admission> admissions = readAdmissions();
			Admission found = null;
			for (Admission a : admissions) {
				if (a.getAdmissionId().equals(admissionId)) {
					found = a;
					break;
				}
			}
			if (found == null) {
				System.out.println("Admission not found.");
				System.out.println("Options:\n1. Discharge by patient ID\n2. Delete patient record (no admission)\n3. Cancel");
				System.out.print("Choose: ");
				String opt = sc.nextLine().trim();
				if (opt.equals("1")) {
					System.out.print("Enter patient ID to discharge by patient: ");
					String patientId = sc.nextLine().trim();
					List<Admission> matches = new ArrayList<>();
					for (Admission a : admissions) {
						if (a.getPatientId().equals(patientId)) matches.add(a);
					}
					if (matches.isEmpty()) {
						System.out.println("No admissions found for patient ID: " + patientId);
						System.out.print("Do you want to delete the patient record instead? (y/n): ");
						String yn = sc.nextLine().trim().toLowerCase();
						if (yn.equals("y") || yn.equals("yes")) {
							List<Patient> patients = readPatients();
							boolean removed = false;
							Iterator<Patient> it = patients.iterator();
							while (it.hasNext()) {
								Patient p = it.next();
								if (p.getId().equals(patientId)) {
									it.remove();
									removed = true;
									break;
								}
							}
							if (removed) {
								writePatients(patients);
								System.out.println("Patient record " + patientId + " removed.");
							} else {
								System.out.println("Patient not found: " + patientId);
							}
						} else {
							System.out.println("Cancelled.");
						}
						return;
					}

					System.out.println("Found admissions for patient:");
					for (int i = 0; i < matches.size(); i++) {
						Admission a = matches.get(i);
						System.out.println((i + 1) + ") ID:" + a.getAdmissionId() + " Status:" + a.getStatus() + " Bill:" + a.getBillAmount());
					}
					System.out.print("Select admission to discharge (number): ");
					int idx = Integer.parseInt(sc.nextLine()) - 1;
					if (idx < 0 || idx >= matches.size()) {
						System.out.println("Invalid selection.");
						return;
					}
					Admission toDischarge = matches.get(idx);
					if ("discharged".equalsIgnoreCase(toDischarge.getStatus())) {
						System.out.println("Patient already discharged.");
						return;
					}
					toDischarge.setStatus("discharged");
					toDischarge.setPaid(true);
					writeAdmissions(admissions);

					Capacity cap = readCapacity();
					// decrement only if it was admitted
					cap.removePatient();
					writeCapacity(cap);

					System.out.println("Patient discharged and payment recorded successfully!");
					System.out.println("Admission ID: " + toDischarge.getAdmissionId());
					System.out.println("Total Bill: Rs." + toDischarge.getBillAmount());
					return;
				} else if (opt.equals("2")) {
					System.out.print("Enter patient ID to delete: ");
					String pid = sc.nextLine().trim();
					List<Patient> patients = readPatients();
					boolean removed = false;
					Iterator<Patient> it = patients.iterator();
					while (it.hasNext()) {
						Patient p = it.next();
						if (p.getId().equals(pid)) {
							it.remove();
							removed = true;
							break;
						}
					}
					if (removed) {
						writePatients(patients);
						System.out.println("Patient record " + pid + " removed.");
					} else {
						System.out.println("Patient not found: " + pid);
					}
					return;
				} else {
					System.out.println("Cancelled.");
					return;
				}
			}
			if ("discharged".equalsIgnoreCase(found.getStatus())) {
				System.out.println("Patient already discharged.");
				return;
			}

			found.setStatus("discharged");
			found.setPaid(true);

			writeAdmissions(admissions);

			Capacity cap = readCapacity();
			cap.removePatient();
			writeCapacity(cap);

			System.out.println("Patient discharged and payment recorded successfully!");
			System.out.println("Admission ID: " + found.getAdmissionId());
			System.out.println("Total Bill: Rs." + found.getBillAmount());
		} catch (Exception e) {
			System.out.println("Error discharging patient: " + e.getMessage());
		}
	}

	private void removeDoctor() {
		try {
			List<Doctor> doctors = FileUtil.readDoctors(decryptKey());
			if (doctors.isEmpty()) {
				System.out.println("No doctors stored.");
				return;
			}
			System.out.println("Doctors:");
			for (Doctor d : doctors)
				System.out.println(d);
			System.out.print("Enter doctor ID to remove: ");
			String id = sc.nextLine().trim();
			boolean found = false;
			Iterator<Doctor> it = doctors.iterator();
			while (it.hasNext()) {
				Doctor d = it.next();
				if (d.getId().equals(id)) {
					it.remove();
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println("Doctor not found.");
				return;
			}
			FileUtil.writeDoctors(doctors, decryptKey());

			Capacity cap = readCapacity();
			cap.removeDoctor();
			writeCapacity(cap);

			List<Admission> admissions = readAdmissions();
			for (Admission a : admissions) {
				if (id.equals(a.getDoctorId())) {
					a.setDoctorId("");
					a.setDoctorName("(left)");
				}
			}
			writeAdmissions(admissions);

			System.out.println("Doctor removed from hospital.");
		} catch (Exception e) {
			System.out.println("Error removing doctor: " + e.getMessage());
		}
	}
}