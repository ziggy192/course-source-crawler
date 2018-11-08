package model;

public class Domain {
	private int id;
	private String name;
	private String domainURL;

	public Domain(int id, String name, String domainURL) {
		this.id = id;
		this.name = name;
		this.domainURL = domainURL;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDomainURL() {
		return domainURL;
	}
}
