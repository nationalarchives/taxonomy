package gov.tna.discovery.taxonomy.ws.domain;

public class SearchIAViewRequest {
    private String categoryQuery;

    private Double score;

    private Integer offset;

    private Integer limit;

    public String getCategoryQuery() {
	return categoryQuery;
    }

    public void setCategoryQuery(String categoryQuery) {
	this.categoryQuery = categoryQuery;
    }

    public Double getScore() {
	return score;
    }

    public void setScore(Double score) {
	this.score = score;
    }

    public Integer getOffset() {
	return offset;
    }

    public void setOffset(Integer offset) {
	this.offset = offset;
    }

    public Integer getLimit() {
	return limit;
    }

    public void setLimit(Integer limit) {
	this.limit = limit;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("SearchIAViewRequest [categoryQuery=");
	builder.append(categoryQuery);
	builder.append(", score=");
	builder.append(score);
	builder.append(", offset=");
	builder.append(offset);
	builder.append(", limit=");
	builder.append(limit);
	builder.append("]");
	return builder.toString();
    }

}
