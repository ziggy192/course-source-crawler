package model;

public class CourseDetail {
	private int id;
	private float rating;
	private int ratingNumber;
	private float cost;
	private int duration;
	private String certification;
	private String overviewDescription;
	private String syllabus;
	private String previewVideoURL;
	private String sourceURL;
	private String image;

	public CourseDetail(int id, float rating, int ratingNumber, float cost, int duration, String certification, String overviewDescription, String syllabus, String previewVideoURL, String sourceURL, String image) {
		this.id = id;
		this.rating = rating;
		this.ratingNumber = ratingNumber;
		this.cost = cost;
		this.duration = duration;
		this.certification = certification;
		this.overviewDescription = overviewDescription;
		this.syllabus = syllabus;
		this.previewVideoURL = previewVideoURL;
		this.sourceURL = sourceURL;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public float getRating() {
		return rating;
	}

	public int getRatingNumber() {
		return ratingNumber;
	}

	public float getCost() {
		return cost;
	}

	public int getDuration() {
		return duration;
	}

	public String getCertification() {
		return certification;
	}

	public String getOverviewDescription() {
		return overviewDescription;
	}

	public String getSyllabus() {
		return syllabus;
	}

	public String getPreviewVideoURL() {
		return previewVideoURL;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public String getImage() {
		return image;
	}
}
