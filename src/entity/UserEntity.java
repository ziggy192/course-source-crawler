package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "User", schema = "course_source", catalog = "")
public class UserEntity {
	private int id;
	private String name;
	private String location;
	private String jobTitle;
	private String about;

	@Id
	@Column(name = "Id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "Name", nullable = true, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "Location", nullable = true, length = 255)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Basic
	@Column(name = "JobTitle", nullable = true, length = 255)
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	@Basic
	@Column(name = "About", nullable = true, length = 1000)
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserEntity that = (UserEntity) o;
		return id == that.id &&
				Objects.equals(name, that.name) &&
				Objects.equals(location, that.location) &&
				Objects.equals(jobTitle, that.jobTitle) &&
				Objects.equals(about, that.about);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, location, jobTitle, about);
	}
}
