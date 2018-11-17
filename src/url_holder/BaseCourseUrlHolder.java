package url_holder;

public class BaseCourseUrlHolder {
	private String courseThumbnailUrl;
	private String courseUrl;

	public BaseCourseUrlHolder(String courseThumbnailUrl, String courseUrl) {
		this.courseThumbnailUrl = courseThumbnailUrl;
		this.courseUrl = courseUrl;
	}

	public BaseCourseUrlHolder() {
	}

	public String getCourseThumbnailUrl() {
		return courseThumbnailUrl;
	}

	public void setCourseThumbnailUrl(String courseThumbnailUrl) {
		this.courseThumbnailUrl = courseThumbnailUrl;
	}

	public String getCourseUrl() {
		return courseUrl;
	}

	public void setCourseUrl(String courseUrl) {
		this.courseUrl = courseUrl;
	}

	@Override
	public String toString() {
		return "BaseCourseUrlHolder{" +
				"courseThumbnailUrl='" + courseThumbnailUrl + '\'' +
				", courseUrl='" + courseUrl + '\'' +
				'}';
	}
}
