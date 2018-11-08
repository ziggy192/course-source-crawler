package url_holder;

public class CategoryUrlHolder {
	private String categoryName;
	private String categoryURL;

	public CategoryUrlHolder(String categoryName, String categoryURL) {
		this.categoryName = categoryName;
		this.categoryURL = categoryURL;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryURL() {
		return categoryURL;
	}

	public void setCategoryURL(String categoryURL) {
		this.categoryURL = categoryURL;
	}

	@Override
	public String toString() {
		return "CategoryUrlHolder{" +
				"categoryName='" + categoryName + '\'' +
				", categoryURL='" + categoryURL + '\'' +
				'}';
	}
}
