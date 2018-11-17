package config.model;

import config.ConfigManager;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Java class for CategoryNameType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CategoryNameType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TestPrep"/>
 *     &lt;enumeration value="Multimedia"/>
 *     &lt;enumeration value="Design"/>
 *     &lt;enumeration value="PersonalDevelopment"/>
 *     &lt;enumeration value="OfficeProductivity"/>
 *     &lt;enumeration value="Music"/>
 *     &lt;enumeration value="Marketing"/>
 *     &lt;enumeration value="LifeStyle"/>
 *     &lt;enumeration value="Language"/>
 *     &lt;enumeration value="IT"/>
 *     &lt;enumeration value="HealthAndFitness"/>
 *     &lt;enumeration value="Business"/>
 *     &lt;enumeration value="Academics"/>
 *     &lt;enumeration value="Kids"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "CategoryNameType", namespace = "www.ParserConfig.com")
@XmlEnum
public enum CategoryNameType {

	@XmlEnumValue("TestPrep")
	TEST_PREP("Luyện thi"),
	@XmlEnumValue("Multimedia")
	MULTIMEDIA("Multimedia"),
	@XmlEnumValue("Design")
	DESIGN("Thiết kế"),
	@XmlEnumValue("PersonalDevelopment")
	PERSONAL_DEVELOPMENT("Phát triển bản thân"),
	@XmlEnumValue("OfficeProductivity")
	OFFICE_PRODUCTIVITY("Tin học văn phòng"),
	@XmlEnumValue("Âm nhạc")
	MUSIC("Music"),
	@XmlEnumValue("Marketing")
	MARKETING("Marketing"),
	@XmlEnumValue("LifeStyle")
	LIFE_STYLE("Phong cách sống"),
	@XmlEnumValue("Language")
	LANGUAGE("Ngoại ngữ"),
	IT("Công nghệ thông tin"),
	@XmlEnumValue("HealthAndFitness")
	HEALTH_AND_FITNESS("Thể thao và sức khoẻ"),
	@XmlEnumValue("Business")
	BUSINESS("Kinh doanh"),
	@XmlEnumValue("Academics")
	ACADEMICS("Học thuật"),
	@XmlEnumValue("Kids")
	KIDS("Trẻ con"),
	@XmlEnumValue("Other")
	OTHER("Khác");
	private final String value;


	/*ex:
	* CategoryNameType category = CategoryNameType.MUSIC
	* System.out.println(category.getValue()); -> "Âm nhạc"
	*
	*  */
	private CategoryNameType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CategoryNameType fromValue(String v) {
		for (CategoryNameType c: CategoryNameType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	@Override
	public String toString() {
		return "CategoryNameType{" +
				"value='" + value + '\'' +
				'}';
	}



//for future reference to category names

//	public static CategoryNameType mapEdumall(String edumallCategoryName) {
//
//		CategoryNameType result = CategoryNameType.OTHER;
//
//		switch (edumallCategoryName) {
//			case "Luyện thi":
//				result = TEST_PREP;
//				break;
//			case "Multimedia":
//				result = MULTIMEDIA;
//				break;
//			case "Phát triển cá nhân":
//				result = PERSONAL_DEVELOPMENT;
//				break;
//			case "Tin học văn phòng":
//				result = OFFICE_PRODUCTIVITY;
//				break;
//			case "Âm nhạc":
//				result = MUSIC;
//				break;
//			case "Marketing":
//				result = MARKETING;
//				break;
//			case "Life style":
//				result = LIFE_STYLE;
//				break;
//			case "Ngoại ngữ":
//				result = LANGUAGE;
//				break;
//			case "Công nghệ thông tin":
//				result = IT;
//				break;
//			case "Thể thao và sức khỏe":
//				result = HEALTH_AND_FITNESS;
//				break;
//			case "Thiết kế":
//				result = DESIGN;
//				break;
//			case "Kinh doanh khởi nghiệp":
//				result = BUSINESS;
//				break;
//			case "Học thuật":
//				result = ACADEMICS;
//				break;
//			case "Nuôi dạy con":
//				result = KIDS;
//				break;
//			case "Phong thủy/Nhân tướng học":
//				result = OTHER;
//				break;
//			default:
//				result = OTHER;
//				break;
//
//		}
//		return result;
//	}
//
//	public static CategoryNameType mapUnica(String unicaCategoryName) {
//		CategoryNameType result = CategoryNameType.OTHER;
//		switch (unicaCategoryName) {
//			case "Nhiếp ảnh, dựng phim":
//				result = MULTIMEDIA;
//				break;
//			case "Học Thiết kế":
//				result = DESIGN;
//				break;
//			case "Khóa học Phát triển cá nhân":
//			result = PERSONAL_DEVELOPMENT;
//			break;
//			case "Tin học văn phòng":
//			result = OFFICE_PRODUCTIVITY;
//			break;
//			case "Âm nhạc":
//			result = MUSIC;
//			break;
//			case "Khóa học  Marketing":
//			result = MARKETING;
//			break;
//			case "Phong cách sống":
//			result = LIFE_STYLE;
//			break;
//			case "Khóa học Tiếng Anh, Tiếng Nhật, Tiếng Hàn, Tiếng Trung Giao Tiếp":
//			result = LANGUAGE;
//			break;
//			case "Khóa học Công nghệ thông tin":
//			result = IT;
//			break;
//			case "Khóa học Sức khỏe - Giới tính":
//			result = HEALTH_AND_FITNESS;
//			break;
//			case "Sale, bán hàng":
//			result = BUSINESS;
//			break;
//			case "Khóa học Kinh doanh & Khởi nghiệp":
//				result = BUSINESS;
//				break;
//			case "Khóa học Nuôi dạy con":
//				result = KIDS;
//				break;
//			default:
//				result = OTHER;
//				break;
//
//		}
//		return result;
//	}


}
