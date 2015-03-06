package uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer.message;

import java.util.List;

public class CategoriseDocumentMessage {
    private String messageId;
    private List<String> listOfDocReferences;
    private int nbOfProcessingErrors;

    public CategoriseDocumentMessage(String messageId, List<String> listOfDocReferences) {
	super();
	this.messageId = messageId;
	this.listOfDocReferences = listOfDocReferences;
	this.nbOfProcessingErrors = 0;
    }

    public String getMessageId() {
	return messageId;
    }

    public void setMessageId(String messageId) {
	this.messageId = messageId;
    }

    public List<String> getListOfDocReferences() {
	return listOfDocReferences;
    }

    public void setListOfDocReferences(List<String> listOfDocReferences) {
	this.listOfDocReferences = listOfDocReferences;
    }

    public int getNbOfProcessingErrors() {
	return nbOfProcessingErrors;
    }

    public void incrementNbOfProcessingErrors() {
	this.nbOfProcessingErrors++;
    }

    public boolean hasProcessingErrors() {
	return this.nbOfProcessingErrors != 0;
    }

}