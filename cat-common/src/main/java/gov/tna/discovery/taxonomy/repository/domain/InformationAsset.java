package gov.tna.discovery.taxonomy.repository.domain;

public class InformationAsset {
	private String _id;
	private String title;
	private String description;
	private String category;
	private String catdocref;

	public String getCatdocref() {
		return catdocref;
	}

	public void setCatdocref(String catdocref) {
		this.catdocref = catdocref;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
