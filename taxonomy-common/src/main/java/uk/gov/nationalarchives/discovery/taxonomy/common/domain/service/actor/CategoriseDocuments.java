package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor;

import java.io.Serializable;

public class CategoriseDocuments implements Serializable {
    private static final long serialVersionUID = -2111185858468095541L;

    final private String[] docReferences;

    final private int messageNumber;

    public CategoriseDocuments(String[] docReferences, int messageNumber) {
	super();
	this.docReferences = docReferences;
	this.messageNumber = messageNumber;
    }

    public String[] getDocReferences() {
	return docReferences;
    }

    public int getMessageNumber() {
	return messageNumber;
    }

}
