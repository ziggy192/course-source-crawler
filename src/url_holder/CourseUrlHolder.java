package url_holder;

public class CourseUrlHolder {
	private String courseName;
	private String courseThumbnailUrl;
	private String courseUrl;


	public CourseUrlHolder(String courseName, String courseThumbnailUrl, String courseUrl) {
		this.courseName = courseName;
		this.courseThumbnailUrl = courseThumbnailUrl;
		this.courseUrl = courseUrl;
	}

	public CourseUrlHolder() {
	}

	public String getCourseThumbnailUrl() {
		return courseThumbnailUrl;
	}

	public void setCourseThumbnailUrl(String courseThumbnailUrl) {
		this.courseThumbnailUrl = courseThumbnailUrl;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseUrl() {
		return courseUrl;
	}

	public void setCourseUrl(String courseUrl) {
		this.courseUrl = courseUrl;
	}

	@Override
	public String toString() {
		return "CourseUrlHolder{" +
				"courseName='" + courseName + '\'' +
				", courseThumbnailUrl='" + courseThumbnailUrl + '\'' +
				", courseUrl='" + courseUrl + '\'' +
				'}';
	}
}
