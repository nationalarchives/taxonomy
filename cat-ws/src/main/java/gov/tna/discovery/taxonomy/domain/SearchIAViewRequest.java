package gov.tna.discovery.taxonomy.domain;

import org.apache.lucene.search.ScoreDoc;

public class SearchIAViewRequest {
    private String categoryQuery;
    
    private Float score;
    
    private Integer number;
    
    private SearchScoreDoc afterDoc;

    public SearchIAViewRequest() {
	super();
	afterDoc = new SearchScoreDoc();
    }

    public SearchIAViewRequest(String categoryQuery, Float score) {
	super();
	this.categoryQuery = categoryQuery;
	this.score = score;
    }

    public String getCategoryQuery() {
	return categoryQuery;
    }

    public void setCategoryQuery(String categoryQuery) {
	this.categoryQuery = categoryQuery;
    }

    public Float getScore() {
	return score;
    }

    public void setScore(Float score) {
	this.score = score;
    }

    public Integer getNumber() {
	return number;
    }

    public void setNumber(Integer number) {
	this.number = number;
    }

    public SearchScoreDoc getAfterDoc() {
	return afterDoc;
    }

    public void setAfterDoc(SearchScoreDoc afterDoc) {
	this.afterDoc = afterDoc;
    }

    public ScoreDoc getLuceneAfterScoreDoc() {
	if (afterDoc.getDoc() != null && afterDoc.getScore() != null) {
	    if (afterDoc.getShardIndex() != null) {
		return new ScoreDoc(afterDoc.getDoc(), afterDoc.getScore(), afterDoc.getShardIndex());
	    }
	    return new ScoreDoc(afterDoc.getDoc(), afterDoc.getScore());
	}
	return null;

    }

}
