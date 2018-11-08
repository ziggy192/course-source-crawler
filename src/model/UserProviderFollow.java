package model;

public class UserProviderFollow {
	private int id;
	private int userId;
	private int providerId;

	public UserProviderFollow(int id, int userId, int providerId) {
		this.id = id;
		this.userId = userId;
		this.providerId = providerId;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getProviderId() {
		return providerId;
	}
}
