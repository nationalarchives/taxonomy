package gov.tna.discovery.taxonomy.common.repository.domain.mongo;

public class CategoryEvaluationResult {
    private String category;
    private Double accuracy;
    private Double recall;
    private int tp;
    private int fp;
    private int fn;

    public CategoryEvaluationResult(String category, int tp, int fp, int fn) {
	super();
	this.category = category;
	this.tp = tp;
	this.fp = fp;
	this.fn = fn;

	if (tp != 0) {
	    this.recall  = 1.0d * tp / (fn + tp);
	    this.accuracy= 1.0d * tp / (fp + tp);
	} else {
	    this.accuracy = 0d;
	    this.recall = 0d;
	}

    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public Double getAccuracy() {
	return accuracy;
    }

    public Double getRecall() {
	return recall;
    }

    public int getTp() {
	return tp;
    }

    public void setTp(int tp) {
	this.tp = tp;
    }

    public int getFp() {
	return fp;
    }

    public void setFp(int fp) {
	this.fp = fp;
    }

    public int getFn() {
	return fn;
    }

    public void setFn(int fn) {
	this.fn = fn;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategoryEvaluationResult [category=");
	builder.append(category);
	builder.append(", accuracy=");
	builder.append(accuracy);
	builder.append(", recall=");
	builder.append(recall);
	builder.append(", tp=");
	builder.append(tp);
	builder.append(", fp=");
	builder.append(fp);
	builder.append(", fn=");
	builder.append(fn);
	builder.append("]");
	return builder.toString();
    }

}
