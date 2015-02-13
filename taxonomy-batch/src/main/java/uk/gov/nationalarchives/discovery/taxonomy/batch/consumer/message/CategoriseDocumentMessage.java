package uk.gov.nationalarchives.discovery.taxonomy.batch.consumer.message;

import java.util.List;

public class CategoriseDocumentMessage {
    private String messageId;
    private List<String> listOfDocReferences;
    private boolean hasProcessingErrors = false;

    public CategoriseDocumentMessage(String messageId, List<String> listOfDocReferences) {
	super();
	this.messageId = messageId;
	this.listOfDocReferences = listOfDocReferences;
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

    public boolean hasProcessingErrors() {
	return hasProcessingErrors;
    }

    public void setHasProcessingErrors(boolean hasProcessingErrors) {
	this.hasProcessingErrors = hasProcessingErrors;
    }

}