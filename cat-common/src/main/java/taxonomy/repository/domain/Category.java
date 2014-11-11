package taxonomy.repository.domain;

public class Category {
	private String _id;
	private String QUERY;
	private String CATEGORY;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getQUERY() {
		return QUERY;
	}

	public void setQUERY(String query) {
		this.QUERY = query;
	}

	public String getCATEGORY() {
		return CATEGORY;
	}

	public void setCategory(String category) {
		this.CATEGORY = category;
	}

	@Override
	public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("Category [_id=");
	    builder.append(_id);
	    builder.append(", QUERY=");
	    builder.append(QUERY);
	    builder.append(", CATEGORY=");
	    builder.append(CATEGORY);
	    builder.append("]");
	    return builder.toString();
	}
	
	
}
