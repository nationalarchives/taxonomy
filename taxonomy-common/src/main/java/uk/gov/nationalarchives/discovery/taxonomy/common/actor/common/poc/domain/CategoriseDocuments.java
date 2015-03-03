package uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.domain;

import java.io.Serializable;
import java.util.List;

public class CategoriseDocuments implements Serializable {
    private String[] docReferences;

    public CategoriseDocuments(String[] docReferences) {
	super();
	this.docReferences = docReferences;
    }

    public String[] getDocReferences() {
	return docReferences;
    }
}
