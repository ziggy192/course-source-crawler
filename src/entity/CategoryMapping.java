package entity;

public enum CategoryMapping {
	TEST_PREP ("Luyện thi")
	, MULTIMEDIA ("Multimedia")
	, DESIGN ("Thiết kế")
	, PERSONAL_DEVELOPMENT ("Phát triển bản thân")
	, OFFICE_PRODUCTIVITY ("Tin học văn phòng")
	, MUSIC ("Âm nhạc")
	, MARKETING ("Marketing")
	, LIFE_STYLE ("Phong cách sống")
	, LANGUAGE ("Ngoại ngữ")
	, IT ("Công nghệ thông tin")
	, HEALTH_AND_FITNESS ("Thể thao và sức khoẻ")
	, BUSINESS ("Kinh doanh")
	, ACADEMICS("Học thuật")
	, KIDS ("Trẻ con")
	, OTHER("Khác")
	;

	private final String value;


	/*ex:
	* CategoryMapping category = CategoryMapping.MUSIC
	* System.out.println(category.getValue()); -> "Âm nhạc"
	*
	*  */
	private CategoryMapping(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CategoryMapping mapEdumall(String edumallCategoryName) {
		CategoryMapping result = CategoryMapping.OTHER;
		switch (edumallCategoryName) {
			case "Luyện thi":
				result = TEST_PREP;
				break;
			case "Multimedia":
				result = MULTIMEDIA;
				break;
			case "Phát triển cá nhân":
				result = PERSONAL_DEVELOPMENT;
				break;
			case "Tin học văn phòng":
				result = OFFICE_PRODUCTIVITY;
				break;
			case "Âm nhạc":
				result = MUSIC;
				break;
			case "Marketing":
				result = MARKETING;
				break;
			case "Life style":
				result = LIFE_STYLE;
				break;
			case "Ngoại ngữ":
				result = LANGUAGE;
				break;
			case "Công nghệ thông tin":
				result = IT;
				break;
			case "Thể thao và sức khỏe":
				result = HEALTH_AND_FITNESS;
				break;
			case "Thiết kế":
				result = DESIGN;
				break;
			case "Kinh doanh khởi nghiệp":
				result = BUSINESS;
				break;
			case "Học thuật":
				result = ACADEMICS;
				break;
			case "Nuôi dạy con":
				result = KIDS;
				break;
			case "Phong thủy/Nhân tướng học":
				result = OTHER;
				break;
			default:
				result = OTHER;
				break;

		}
		return result;
	}

	public static CategoryMapping mapUnica(String unicaCategoryName) {
		CategoryMapping result = CategoryMapping.OTHER;
		switch (unicaCategoryName) {
			case "Nhiếp ảnh, dựng phim":
				result = MULTIMEDIA;
				break;
			case "Học Thiết kế":
				result = DESIGN;
				break;
			case "Khóa học Phát triển cá nhân":
			result = PERSONAL_DEVELOPMENT;
			break;
			case "Tin học văn phòng":
			result = OFFICE_PRODUCTIVITY;
			break;
			case "Âm nhạc":
			result = MUSIC;
			break;
			case "Khóa học  Marketing":
			result = MARKETING;
			break;
			case "Phong cách sống":
			result = LIFE_STYLE;
			break;
			case "Khóa học Tiếng Anh, Tiếng Nhật, Tiếng Hàn, Tiếng Trung Giao Tiếp":
			result = LANGUAGE;
			break;
			case "Khóa học Công nghệ thông tin":
			result = IT;
			break;
			case "Khóa học Sức khỏe - Giới tính":
			result = HEALTH_AND_FITNESS;
			break;
			case "Sale, bán hàng":
			result = BUSINESS;
			break;
			case "Khóa học Kinh doanh & Khởi nghiệp":
				result = BUSINESS;
				break;
			case "Khóa học Nuôi dạy con":
				result = KIDS;
				break;
			default:
				result = OTHER;
				break;

		}
		return result;
	}


}
