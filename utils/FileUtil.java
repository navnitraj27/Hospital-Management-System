package utils;

import models.Doctor;
import models.Patient;
import models.Ambulance;
import models.Admission;
import models.Capacity;
import java.io.*;
import java.util.*;


public class FileUtil {
	private static final String DOCTORS_FILE = "data/doctors.txt";
	private static final String PATIENTS_FILE = "data/patients.txt";
	private static final String AMBULANCES_FILE = "data/ambulances.txt";
	private static final String ADMISSIONS_FILE = "data/admissions.txt";
	private static final String CAPACITY_FILE = "data/capacity.txt";

	public static void ensureDataFolder() throws IOException {
		File dir = new File("data");
		if (!dir.exists()) {
			if (!dir.mkdirs()) throw new IOException("Failed to create data folder");
		}
		new File(DOCTORS_FILE).createNewFile();
		new File(PATIENTS_FILE).createNewFile();
		new File(AMBULANCES_FILE).createNewFile();
		new File(ADMISSIONS_FILE).createNewFile();
		new File(CAPACITY_FILE).createNewFile();
	}

	public static List<Doctor> readDoctors(String unused) throws Exception {
		List<Doctor> out = new ArrayList<>();
		List<String> lines = readLines(DOCTORS_FILE);
		for (String l : lines) {
			Doctor d = Doctor.fromCSV(l);
			if (d != null) out.add(d);
		}
		return out;
	}

	public static void writeDoctors(List<Doctor> list, String unused) throws Exception {
		List<String> lines = new ArrayList<>();
		for (Doctor d : list) lines.add(d.toCSV());
		writeLines(DOCTORS_FILE, lines);
	}

	public static List<Patient> readPatients(String unused) throws Exception {
		List<Patient> out = new ArrayList<>();
		List<String> lines = readLines(PATIENTS_FILE);
		for (String l : lines) {
			Patient p = Patient.fromCSV(l);
			if (p != null) out.add(p);
		}
		return out;
	}

	public static void writePatients(List<Patient> list, String unused) throws Exception {
		List<String> lines = new ArrayList<>();
		for (Patient p : list) lines.add(p.toCSV());
		writeLines(PATIENTS_FILE, lines);
	}

	public static List<Ambulance> readAmbulances(String unused) throws Exception {
		List<Ambulance> out = new ArrayList<>();
		List<String> lines = readLines(AMBULANCES_FILE);
		for (String l : lines) {
			if (!l.trim().isEmpty()) {
				Ambulance a = Ambulance.fromCSV(l);
				if (a != null) out.add(a);
			}
		}
		return out;
	}

	public static void writeAmbulances(List<Ambulance> list, String unused) throws Exception {
		List<String> lines = new ArrayList<>();
		for (Ambulance a : list) lines.add(a.toCSV());
		writeLines(AMBULANCES_FILE, lines);
	}

	public static List<Admission> readAdmissions(String unused) throws Exception {
		List<Admission> out = new ArrayList<>();
		List<String> lines = readLines(ADMISSIONS_FILE);
		for (String l : lines) {
			if (!l.trim().isEmpty()) {
				Admission ad = Admission.fromCSV(l);
				if (ad != null) out.add(ad);
			}
		}
		return out;
	}

	public static void writeAdmissions(List<Admission> list, String unused) throws Exception {
		List<String> lines = new ArrayList<>();
		for (Admission ad : list) lines.add(ad.toCSV());
		writeLines(ADMISSIONS_FILE, lines);
	}

	public static Capacity readCapacity(String unused) throws Exception {
		List<String> lines = readLines(CAPACITY_FILE);
		if (lines.isEmpty() || lines.get(0).trim().isEmpty()) {
			return new Capacity();
		}
		Capacity c = Capacity.fromCSV(lines.get(0));
		return c != null ? c : new Capacity();
	}

	public static void writeCapacity(Capacity cap, String unused) throws Exception {
		List<String> lines = new ArrayList<>();
		lines.add(cap.toCSV());
		writeLines(CAPACITY_FILE, lines);
	}

	private static List<String> readLines(String path) throws IOException {
		File f = new File(path);
		List<String> l = new ArrayList<>();
		if (!f.exists() || f.length() == 0) return l;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) l.add(line);
		}
		return l;
	}

	private static void writeLines(String path, List<String> lines) throws IOException {
		String joined = String.join("\n", lines);
		try (FileOutputStream fos = new FileOutputStream(path)) {
			fos.write(joined.getBytes("UTF-8"));
		}
	}
}