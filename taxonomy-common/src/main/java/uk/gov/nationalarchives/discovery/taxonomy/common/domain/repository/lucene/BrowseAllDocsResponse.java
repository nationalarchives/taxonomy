package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;

public class BrowseAllDocsResponse {
    private final List<String> listOfDocReferences;
    private final ScoreDoc lastScoreDoc;

    public BrowseAllDocsResponse(List<String> listOfDocReferences, ScoreDoc lastScoreDoc) {
	super();
	this.listOfDocReferences = listOfDocReferences;
	this.lastScoreDoc = lastScoreDoc;
    }

    public List<String> getListOfDocReferences() {
	return listOfDocReferences;
    }

    public ScoreDoc getLastScoreDoc() {
	return lastScoreDoc;
    }

}
