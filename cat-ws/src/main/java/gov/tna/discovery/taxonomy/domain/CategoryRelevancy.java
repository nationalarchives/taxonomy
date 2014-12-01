package gov.tna.discovery.taxonomy.domain;

public class CategoryRelevancy {
    private String name;
    private Float score;

    public CategoryRelevancy(String name, Float score) {
	super();
	this.name = name;
	this.score = score;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Float getScore() {
	return score;
    }

    public void setScore(Float score) {
	this.score = score;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategoryRelevancy [name=");
	builder.append(name);
	builder.append(", score=");
	builder.append(score);
	builder.append("]");
	return builder.toString();
    }

}