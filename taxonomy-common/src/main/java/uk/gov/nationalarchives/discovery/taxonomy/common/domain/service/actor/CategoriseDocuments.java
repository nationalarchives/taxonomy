package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor;

import java.io.Serializable;

public class CategoriseDocuments implements Serializable {
    private static final long serialVersionUID = -2111185858468095541L;

    private String[] docReferences;

    public CategoriseDocuments(String[] docReferences) {
	super();
	this.docReferences = docReferences;
    }

    public String[] getDocReferences() {
	return docReferences;
    }
}
