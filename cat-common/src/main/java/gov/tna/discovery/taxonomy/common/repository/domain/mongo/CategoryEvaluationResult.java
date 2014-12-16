package gov.tna.discovery.taxonomy.common.repository.domain.mongo;

public class CategoryEvaluationResult {
    private String category;
    private Double accuracy;
    private Double recall;
    private int tp;
    private int fp;
    private int fn;
    private boolean foundInCatRepo = false;
    private boolean foundInTDocCat = false;
    private boolean foundInTDocLegacyCat = false;

    public CategoryEvaluationResult(String category, int tp, int fp, int fn) {
	super();
	this.category = category;
	this.tp = tp;
	this.fp = fp;
	this.fn = fn;

	if (tp != 0) {
	    this.recall = 1.0d * tp / (fn + tp);
	    this.accuracy = 1.0d * tp / (fp + tp);
	} else {
	    this.accuracy = 0d;
	    this.recall = 0d;
	}

	if (tp > 0) {
	    this.foundInCatRepo = true;
	    this.foundInTDocCat = true;
	    this.foundInTDocLegacyCat = true;
	    return;
	}
	if (fp > 0) {
	    this.foundInCatRepo = true;
	    this.foundInTDocCat = true;
	}
	if (fn > 0) {
	    this.foundInTDocLegacyCat = true;
	}

    }

    public CategoryEvaluationResult(String category, boolean foundInCatRepo, boolean foundInTDocCat,
	    boolean foundInTDocLegacyCat) {
	this.category = category;
	this.tp = 0;
	this.fp = 0;
	this.fn = 0;
	this.accuracy = 0d;
	this.recall = 0d;
	this.foundInCatRepo = foundInCatRepo;
	this.foundInTDocCat = foundInTDocCat;
	this.foundInTDocLegacyCat = foundInTDocLegacyCat;
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

    public boolean isFoundInCatRepo() {
	return foundInCatRepo;
    }

    public boolean isFoundInTDocCat() {
	return foundInTDocCat;
    }

    public boolean isFoundInTDocLegacyCat() {
	return foundInTDocLegacyCat;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategoryEvaluationResult [category=");
	builder.append(category);
	if (foundInTDocLegacyCat || foundInTDocCat) {
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
	}
	builder.append(", foundInCatRepo=");
	builder.append(foundInCatRepo);
	builder.append(", foundInTDocCat=");
	builder.append(foundInTDocCat);
	builder.append(", foundInTDocLegacyCat=");
	builder.append(foundInTDocLegacyCat);
	builder.append("]");
	return builder.toString();
    }

}
