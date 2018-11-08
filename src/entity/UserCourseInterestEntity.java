package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "UserCourseInterest", schema = "course_source", catalog = "")
public class UserCourseInterestEntity {
	private int id;
	private Integer userId;
	private Integer courseId;

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
	@Column(name = "CourseId", nullable = true)
	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserCourseInterestEntity that = (UserCourseInterestEntity) o;
		return id == that.id &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(courseId, that.courseId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, courseId);
	}
}
