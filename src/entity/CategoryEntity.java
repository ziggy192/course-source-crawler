package entity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Objects;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{www.Category.com}ID"/>
 *         &lt;element name="Name" type="{www.Category.com}Name"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
		"id",
		"name"
})
@XmlRootElement(name = "Category", namespace = "www.Category.com")
@Entity
@Table(name = "Category", schema = "course_source")
@NamedQueries(
		@NamedQuery(name = "CategoryEntity.getCategoryByName",query = "select d from CategoryEntity  d where d.name like :categoryName")
)
public class CategoryEntity {
	private int id;
	private String name;

	@Id
	@Column(name = "Id", nullable = false)
	@XmlElement(name = "ID", namespace = "www.Category.com", required = false)
	@XmlSchemaType(name = "positiveInteger")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "Name", nullable = true, length = 255)
	@XmlElement(name = "Name", namespace = "www.Category.com", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CategoryEntity that = (CategoryEntity) o;
		return id == that.id &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
