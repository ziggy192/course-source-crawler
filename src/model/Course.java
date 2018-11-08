package model;

public class Course {
	private int id;
	private String name;
	private String author;
	private String tag;
	private int providerId;
	private int subjectId;


	public Course(int id, String name, String author, String tag, int providerId, int subjectId) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.tag = tag;
		this.providerId = providerId;
		this.subjectId = subjectId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public String getTag() {
		return tag;
	}

	public int getProviderId() {
		return providerId;
	}

	public int getSubjectId() {
		return subjectId;
	}
}
