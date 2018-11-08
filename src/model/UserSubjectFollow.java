package model;

public class UserSubjectFollow {
	private int id;
	private int userId;
	private int subjectId;

	public UserSubjectFollow(int id, int userId, int subjectId) {
		this.id = id;
		this.userId = userId;
		this.subjectId = subjectId;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getSubjectId() {
		return subjectId;
	}
}
