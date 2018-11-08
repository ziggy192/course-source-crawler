package url_holder;

public class UnicaCourseUrlHolder {
	private String courseThumbnailUrl;
	private String courseUrl;

	private double cost;

	public UnicaCourseUrlHolder() {
	}

	public UnicaCourseUrlHolder(String courseThumbnailUrl, String courseUrl, double cost) {
		this.courseThumbnailUrl = courseThumbnailUrl;
		this.courseUrl = courseUrl;
		this.cost = cost;
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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "UnicaCourseUrlHolder{" +
				"courseThumbnailUrl='" + courseThumbnailUrl + '\'' +
				", courseUrl='" + courseUrl + '\'' +
				", cost=" + cost +
				'}';
	}
}
