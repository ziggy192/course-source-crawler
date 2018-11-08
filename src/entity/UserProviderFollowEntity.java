package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "UserProviderFollow", schema = "course_source", catalog = "")
public class UserProviderFollowEntity {
	private int id;
	private Integer userId;
	private Integer providerId;

	@Id
	@Column(name = "Id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "UserId", nullable = true)
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Basic
	@Column(name = "ProviderId", nullable = true)
	public Integer getProviderId() {
		return providerId;
	}

	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserProviderFollowEntity that = (UserProviderFollowEntity) o;
		return id == that.id &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(providerId, that.providerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, providerId);
	}
}
