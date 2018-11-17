package entity;


import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Objects;

import util.StringUtils;

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
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="Author" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="AuthorDescription" type="{www.Course.com}String" minOccurs="0"/>
 *         &lt;element name="DomainId" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="CategoryId" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="OverviewDescription" type="{www.Course.com}String"/>
 *         &lt;element name="AuthorImageURL" type="{www.Course.com}URL" minOccurs="0"/>
 *         &lt;element name="Rating">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;maxInclusive value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RatingNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Cost" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Duration" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="Syllabus" type="{www.Course.com}String"/>
 *         &lt;element name="PreviewVideoURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ImageURL" type="{www.Course.com}URL"/>
 *         &lt;element name="SourceURL" type="{www.Course.com}URL"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
		"id",
		"name",
		"author",
		"authorDescription",
		"domainId",
		"categoryId",
		"overviewDescription",
		"authorImageUrl",
		"rating",
		"ratingNumber",
		"cost",
		"duration",
		"syllabus",
		"previewVideoUrl",
		"imageUrl",
		"sourceUrl",
		"hash",
		"dataFixed"
})
@XmlRootElement(name = "Course", namespace = "www.Course.com")
@Entity
@Table(name = "Course", schema = "course_source")
@NamedQueries(
		{
				@NamedQuery(name = "CourseEntity.findCourseByHashing", query = "select c from CourseEntity c where c.hash = :hash")
				, @NamedQuery(name = "CourseEntity.findCourseByNameQuery", query = "SELECT c FROM CourseEntity c where c.name like concat('%',:query,'%')")
				, @NamedQuery(name = "CourseEntity.findCourseById", query = "select c from CourseEntity  c where c.id = :courseId")
				, @NamedQuery(name = "CourseEntity.findAllCourse", query = "select c from CourseEntity  c ")

		}
)
public class CourseEntity {
	private int id;
	private String name;
	private String author;
	private String authorDescription;
	private Integer domainId;
	private Integer categoryId;
	private String overviewDescription;
	private String authorImageUrl;
	private Double rating;
	private Integer ratingNumber;
	private Double cost;
	private int duration;
	private String syllabus;
	private String previewVideoUrl;
	private String imageUrl;
	private String sourceUrl;
	private int hash;
	private boolean dataFixed;


	@Id
	@Column(name = "Id", nullable = false)
	@XmlElement(name = "Id", namespace = "www.Course.com", required = false)
	@XmlSchemaType(name = "nonNegativeInteger")

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Basic
	@Column(name = "Name", nullable = true, length = 255)
	@XmlElement(name = "Name", namespace = "www.Course.com", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "Author", nullable = true, length = 255)
	@XmlElement(name = "Author", namespace = "www.Course.com", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Basic
	@Column(name = "AuthorDescription", nullable = true, length = -1)
	@XmlElement(name = "AuthorDescription", namespace = "www.Course.com")

	public String getAuthorDescription() {
		return authorDescription;
	}

	public void setAuthorDescription(String authorDescription) {
		this.authorDescription = authorDescription;
	}

	@Basic
	@Column(name = "DomainId", nullable = true)
	@XmlElement(name = "DomainId", namespace = "www.Course.com", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	@Basic
	@Column(name = "CategoryId", nullable = true)
	@XmlElement(name = "CategoryId", namespace = "www.Course.com", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	@Basic
	@Column(name = "OverviewDescription", nullable = true, length = -1)
	@XmlElement(name = "OverviewDescription", namespace = "www.Course.com", required = true)

	public String getOverviewDescription() {
		return overviewDescription;
	}

	public void setOverviewDescription(String overviewDescription) {
		this.overviewDescription = overviewDescription;
	}

	@Basic
	@Column(name = "AuthorImageURL", nullable = true, length = 255)
	@XmlElement(name = "AuthorImageURL", namespace = "www.Course.com")

	public String getAuthorImageUrl() {
		return authorImageUrl;
	}

	public void setAuthorImageUrl(String authorImageUrl) {
		this.authorImageUrl = authorImageUrl;
	}

	@Basic
	@Column(name = "Rating", nullable = true, precision = 0)
	@XmlElement(name = "Rating", namespace = "www.Course.com", required = true)

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Basic
	@Column(name = "RatingNumber", nullable = true)
	@XmlElement(name = "RatingNumber", namespace = "www.Course.com")

	public Integer getRatingNumber() {
		return ratingNumber;
	}

	public void setRatingNumber(Integer ratingNumber) {
		this.ratingNumber = ratingNumber;
	}

	@Basic
	@Column(name = "Cost", nullable = true, precision = 0)
	@XmlElement(name = "Cost", namespace = "www.Course.com", required = true)

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	@Basic
	@Column(name = "Duration", nullable = true, length = 255)
	@XmlElement(name = "Duration", namespace = "www.Course.com", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Basic
	@Column(name = "Syllabus", nullable = true, length = -1)
	@XmlElement(name = "Syllabus", namespace = "www.Course.com", required = true)

	public String getSyllabus() {
		return syllabus;
	}

	public void setSyllabus(String syllabus) {
		this.syllabus = syllabus;
	}

	@Basic
	@Column(name = "PreviewVideoURL", nullable = true, length = 255)
	@XmlElement(name = "PreviewVideoURL", namespace = "www.Course.com")

	public String getPreviewVideoUrl() {
		return previewVideoUrl;
	}

	public void setPreviewVideoUrl(String previewVideoUrl) {
		this.previewVideoUrl = previewVideoUrl;
	}

	@Basic
	@Column(name = "ImageURL", nullable = true, length = 255)
	@XmlElement(name = "ImageURL", namespace = "www.Course.com", required = true)

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Basic
	@Column(name = "SourceURL", nullable = true, length = 255)
	@XmlElement(name = "SourceURL", namespace = "www.Course.com", required = true)

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}


	@Basic
	@Column(name = "Hash", nullable = true)
	@XmlElement(name = "Hash", namespace = "www.Course.com", required = false)
	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}


	@Basic
	@Column(name = "DataFixed", nullable = true)
	@XmlElement(name = "DataFixed", namespace = "www.Course.com", required = false)
	public boolean isDataFixed() {
		return dataFixed;
	}

	public void setDataFixed(boolean dataFixed) {
		this.dataFixed = dataFixed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CourseEntity that = (CourseEntity) o;
		return id == that.id &&
				Objects.equals(name, that.name) &&
				Objects.equals(author, that.author) &&
				Objects.equals(authorDescription, that.authorDescription) &&
				Objects.equals(domainId, that.domainId) &&
				Objects.equals(categoryId, that.categoryId) &&
				Objects.equals(overviewDescription, that.overviewDescription) &&
				Objects.equals(authorImageUrl, that.authorImageUrl) &&
				Objects.equals(rating, that.rating) &&
				Objects.equals(ratingNumber, that.ratingNumber) &&
				Objects.equals(cost, that.cost) &&
				Objects.equals(duration, that.duration) &&
				Objects.equals(syllabus, that.syllabus) &&
				Objects.equals(previewVideoUrl, that.previewVideoUrl) &&
				Objects.equals(imageUrl, that.imageUrl) &&
				Objects.equals(sourceUrl, that.sourceUrl);
	}


	@Override
	public String toString() {
		return "CourseEntity{" +
				"id=" + id +
				", name='" + name + '\'' +
				", author='" + author + '\'' +
				", authorDescription='" + authorDescription + '\'' +
				", domainId=" + domainId +
				", categoryId=" + categoryId +
				", overviewDescription='" + overviewDescription + '\'' +
				", authorImageUrl='" + authorImageUrl + '\'' +
				", rating=" + rating +
				", ratingNumber=" + ratingNumber +
				", cost=" + cost +
				", duration=" + duration +
				", syllabus='" + syllabus + '\'' +
				", previewVideoUrl='" + previewVideoUrl + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				", sourceUrl='" + sourceUrl + '\'' +
				'}';
	}

	public int hashCourse() {
		String combine = "";
		if (name != null && author != null) {
			combine = StringUtils.toRawString(name) + StringUtils.toRawString(author);

		}

		return StringUtils.hashingString(combine);
	}

}
