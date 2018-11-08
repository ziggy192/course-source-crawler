package entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Domain", schema = "course_source", catalog = "")
@NamedQueries(
		@NamedQuery(name = "Domain.getDomainByDomainName",query = "select d from DomainEntity  d where d.name like :domainName")

)
public class DomainEntity {
	private int id;
	private String name;
	private String domainUrl;

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
	@Column(name = "DomainURL", nullable = true, length = 255)
	public String getDomainUrl() {
		return domainUrl;
	}

	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DomainEntity that = (DomainEntity) o;
		return id == that.id &&
				Objects.equals(name, that.name) &&
				Objects.equals(domainUrl, that.domainUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, domainUrl);
	}

	@Override
	public String toString() {
		return "DomainEntity{" +
				"id=" + id +
				", name='" + name + '\'' +
				", domainUrl='" + domainUrl + '\'' +
				'}';
	}
}
