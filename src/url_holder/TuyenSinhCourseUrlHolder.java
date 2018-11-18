package url_holder;

public class TuyenSinhCourseUrlHolder extends BaseCourseUrlHolder {
	private String authorName;
	private String authorImageUrl;

	public TuyenSinhCourseUrlHolder(String courseThumbnailUrl, String courseUrl, String authorName, String authorImageUrl) {
		super(courseThumbnailUrl, courseUrl);
		this.authorName = authorName;
		this.authorImageUrl = authorImageUrl;
	}

	public TuyenSinhCourseUrlHolder() {
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}


	public String getAuthorImageUrl() {
		return authorImageUrl;
	}

	public void setAuthorImageUrl(String authorImageUrl) {
		this.authorImageUrl = authorImageUrl;
	}

	@Override
	public String toString() {
		return "TuyenSinhCourseUrlHolder{" +
				"courseThumbnailUrl='" + getCourseThumbnailUrl() + '\'' +
				", courseUrl='" + getCourseUrl() + '\'' +
				"authorName='" + authorName + '\'' +
				", authorImageUrl='" + authorImageUrl + '\'' +
				'}';
	}
}
