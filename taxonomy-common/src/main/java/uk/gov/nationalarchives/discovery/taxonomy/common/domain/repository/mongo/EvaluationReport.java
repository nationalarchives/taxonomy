package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "evaluationReports")
public class EvaluationReport {
    @Id
    private int timestamp;
    private String comments;
    private Double avgAccuracy;
    private Double avgRecall;
    private int numberOfDocuments;
    List<CategoryEvaluationResult> results;

    public EvaluationReport(List<CategoryEvaluationResult> results, int numberOfDocuments) {
	super();
	this.results = results;
	this.numberOfDocuments = numberOfDocuments;
	this.timestamp = setTimeStamp();
	this.avgAccuracy = calculateAvgAccuracy();
	this.avgRecall = calculateAvgRecall();
    }

    public EvaluationReport(String comments, List<CategoryEvaluationResult> results, int numberOfDocuments) {
	super();
	this.comments = comments;
	this.results = results;
	this.numberOfDocuments = numberOfDocuments;
	this.timestamp = setTimeStamp();
	this.avgAccuracy = calculateAvgAccuracy();
	this.avgRecall = calculateAvgRecall();
    }

    public EvaluationReport() {
	super();
	this.timestamp = setTimeStamp();
    }

    private Double calculateAvgRecall() {
	double sum = 0d;
	for (CategoryEvaluationResult result : this.results) {
	    sum += result.getRecall();
	}
	return sum / results.size();
    }

    private Double calculateAvgAccuracy() {
	double sum = 0d;
	for (CategoryEvaluationResult result : this.results) {
	    sum += result.getAccuracy();
	}
	return sum / results.size();
    }

    private int setTimeStamp() {
	return (int) System.currentTimeMillis();
    }

    public int getTimestamp() {
	return timestamp;
    }

    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    public Double getAvgAccuracy() {
	return avgAccuracy;
    }

    public Double getAvgRecall() {
	return avgRecall;
    }

    public List<CategoryEvaluationResult> getResults() {
	return results;
    }

    public void setResults(List<CategoryEvaluationResult> results) {
	this.results = results;
	this.avgAccuracy = calculateAvgAccuracy();
	this.avgRecall = calculateAvgRecall();
    }

    public int getNumberOfDocuments() {
	return numberOfDocuments;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("EvaluationReport [timestamp=");
	builder.append(timestamp);
	builder.append(", comments=");
	builder.append(comments);
	builder.append(", avgAccuracy=");
	builder.append(avgAccuracy);
	builder.append(", avgRecall=");
	builder.append(avgRecall);
	builder.append(", numberOfDocuments=");
	builder.append(numberOfDocuments);
	builder.append("]");
	return builder.toString();
    }

}
