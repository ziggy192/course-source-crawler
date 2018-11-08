package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "UserSubjectFollow", schema = "course_source", catalog = "")
public class UserSubjectFollowEntity {
	private int id;
	private Integer userId;
	private Integer subjectId;

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
	@Column(name = "SubjectId", nullable = true)
	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserSubjectFollowEntity that = (UserSubjectFollowEntity) o;
		return id == that.id &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(subjectId, that.subjectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, subjectId);
	}
}
