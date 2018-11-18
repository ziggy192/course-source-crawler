
package config.model;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * <p>Java class for DomainType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DomainType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DomainUrl" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *         &lt;element name="CategoryMappingList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CategoryMapping" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="to" use="required" type="{www.ParserConfig.com}CategoryNameType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PaginationSign" type="{www.ParserConfig.com}SignType"/>
 *         &lt;element name="CategoryListSign" type="{www.ParserConfig.com}SignType"/>
 *         &lt;element name="CourseListSign" type="{www.ParserConfig.com}SignType"/>
 *         &lt;element name="CourseDetailSign" type="{www.ParserConfig.com}SignType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainType", namespace = "www.ParserConfig.com", propOrder = {
		"threadLimit",
		"domainUrl",
		"categoryMappingList",
		"paginationSign",
		"categoryListSign",
		"courseListSign",
		"courseDetailSign"
})
public class DomainType {
	@XmlElement(name = "ThreadLimit", namespace = "www.ParserConfig.com", required = true)
	protected int threadLimit;

	@XmlElement(name = "DomainUrl", namespace = "www.ParserConfig.com", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	protected String domainUrl;
	@XmlElement(name = "CategoryMappingList", namespace = "www.ParserConfig.com", required = true)
	protected DomainType.CategoryMappingList categoryMappingList;
	@XmlElement(name = "PaginationSign", namespace = "www.ParserConfig.com", required = true)
	protected SignType paginationSign;
	@XmlElement(name = "CategoryListSign", namespace = "www.ParserConfig.com", required = true)
	protected SignType categoryListSign;
	@XmlElement(name = "CourseListSign", namespace = "www.ParserConfig.com", required = true)
	protected SignType courseListSign;
	@XmlElement(name = "CourseDetailSign", namespace = "www.ParserConfig.com", required = true)
	protected SignType courseDetailSign;


	public int getThreadLimit() {
		return threadLimit;
	}

	public void setThreadLimit(int threadLimit) {
		this.threadLimit = threadLimit;
	}

	/**
	 * Gets the value of the domainUrl property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getDomainUrl() {
		return domainUrl;
	}

	/**
	 * Sets the value of the domainUrl property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setDomainUrl(String value) {
		this.domainUrl = value;
	}

	/**
	 * Gets the value of the categoryMappingList property.
	 *
	 * @return possible object is
	 * {@link DomainType.CategoryMappingList }
	 */
	public DomainType.CategoryMappingList getCategoryMappingList() {
		return categoryMappingList;
	}

	/**
	 * Sets the value of the categoryMappingList property.
	 *
	 * @param value allowed object is
	 *              {@link DomainType.CategoryMappingList }
	 */
	public void setCategoryMappingList(DomainType.CategoryMappingList value) {
		this.categoryMappingList = value;
	}

	/**
	 * Gets the value of the paginationSign property.
	 *
	 * @return possible object is
	 * {@link SignType }
	 */
	public SignType getPaginationSign() {
		return paginationSign;
	}

	/**
	 * Sets the value of the paginationSign property.
	 *
	 * @param value allowed object is
	 *              {@link SignType }
	 */
	public void setPaginationSign(SignType value) {
		this.paginationSign = value;
	}

	/**
	 * Gets the value of the categoryListSign property.
	 *
	 * @return possible object is
	 * {@link SignType }
	 */
	public SignType getCategoryListSign() {
		return categoryListSign;
	}

	/**
	 * Sets the value of the categoryListSign property.
	 *
	 * @param value allowed object is
	 *              {@link SignType }
	 */
	public void setCategoryListSign(SignType value) {
		this.categoryListSign = value;
	}

	/**
	 * Gets the value of the courseListSign property.
	 *
	 * @return possible object is
	 * {@link SignType }
	 */
	public SignType getCourseListSign() {
		return courseListSign;
	}

	/**
	 * Sets the value of the courseListSign property.
	 *
	 * @param value allowed object is
	 *              {@link SignType }
	 */
	public void setCourseListSign(SignType value) {
		this.courseListSign = value;
	}

	/**
	 * Gets the value of the courseDetailSign property.
	 *
	 * @return possible object is
	 * {@link SignType }
	 */
	public SignType getCourseDetailSign() {
		return courseDetailSign;
	}

	/**
	 * Sets the value of the courseDetailSign property.
	 *
	 * @param value allowed object is
	 *              {@link SignType }
	 */
	public void setCourseDetailSign(SignType value) {
		this.courseDetailSign = value;
	}


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
	 *         &lt;element name="CategoryMapping" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="to" use="required" type="{www.ParserConfig.com}CategoryNameType" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"categoryMapping"
	})
	public static class CategoryMappingList {

		@XmlElement(name = "CategoryMapping", namespace = "www.ParserConfig.com")
		protected List<DomainType.CategoryMappingList.CategoryMapping> categoryMapping;

		/**
		 * Gets the value of the categoryMapping property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list,
		 * not a snapshot. Therefore any modification you make to the
		 * returned list will be present inside the JAXB object.
		 * This is why there is not a <CODE>set</CODE> method for the categoryMapping property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * <pre>
		 *    getCategoryMapping().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link DomainType.CategoryMappingList.CategoryMapping }
		 */
		public List<DomainType.CategoryMappingList.CategoryMapping> getCategoryMapping() {
			if (categoryMapping == null) {
				categoryMapping = new ArrayList<DomainType.CategoryMappingList.CategoryMapping>();
			}
			return this.categoryMapping;
		}


		/**
		 * <p>Java class for anonymous complex type.
		 *
		 * <p>The following schema fragment specifies the expected content contained within this class.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="to" use="required" type="{www.ParserConfig.com}CategoryNameType" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class CategoryMapping {

			@XmlAttribute(name = "source", required = true)
			protected String source;
			@XmlAttribute(name = "to", required = true)
			protected CategoryNameType to;

			/**
			 * Gets the value of the source property.
			 *
			 * @return possible object is
			 * {@link String }
			 */
			public String getSource() {
				return source;
			}

			/**
			 * Sets the value of the source property.
			 *
			 * @param value allowed object is
			 *              {@link String }
			 */
			public void setSource(String value) {
				this.source = value;
			}

			/**
			 * Gets the value of the to property.
			 *
			 * @return possible object is
			 * {@link CategoryNameType }
			 */
			public CategoryNameType getTo() {
				return to;
			}

			/**
			 * Sets the value of the to property.
			 *
			 * @param value allowed object is
			 *              {@link CategoryNameType }
			 */
			public void setTo(CategoryNameType value) {
				this.to = value;
			}

			@Override
			public String toString() {
				return "CategoryMapping{" +
						"source='" + source + '\'' +
						", to=" + to +
						'}';
			}
		}

		@Override
		public String toString() {
			String result = "CategoryMappingList{" +
					"categoryMapping=[";

			for (CategoryMapping mapping : categoryMapping) {
				result += mapping.toString() + "|";
			}
			result += "]}";
			return result;
		}
	}

	@Override
	public String toString() {
		return "DomainType{" +
				"domainUrl='" + domainUrl + '\'' +
				", categoryMappingList=" + categoryMappingList +
				", paginationSign=" + paginationSign +
				", categoryListSign=" + categoryListSign +
				", courseListSign=" + courseListSign +
				", courseDetailSign=" + courseDetailSign +
				'}';
	}
}
