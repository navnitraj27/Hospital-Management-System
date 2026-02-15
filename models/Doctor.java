package models;

public class Doctor {
	private final String id;
	private final String name;
	private final String specialization;

	public Doctor(String id, String name, String specialization) {
		this.id = id;
		this.name = name;
		this.specialization = specialization;
	}

	public String toCSV() {
		return id + "," + name.replace(",", " ") + "," + specialization.replace(",", " ");
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static Doctor fromCSV(String line) {
		String[] parts = line.split(",", 3);
		if (parts.length < 3)
			return null;
		return new Doctor(parts[0], parts[1], parts[2]);
	}

	@Override
	public String toString() {
		return "Doctor[" + id + "] " + name + " (" + specialization + ")";
	}
}