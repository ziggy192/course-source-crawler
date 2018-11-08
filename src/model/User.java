package model;

public class User {
	private int id;
	private String name;
	private String location;
	private String jobTitle;
	private String about;

	public User(int id, String name, String location, String jobTitle, String about) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.jobTitle = jobTitle;
		this.about = about;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public String getAbout() {
		return about;
	}
}
