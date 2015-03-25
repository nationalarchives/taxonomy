package uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer.message;

import java.util.ArrayList;
import java.util.List;

public class CategoriseDocumentMessage {
    private String messageId;
    private List<String> listOfDocReferences;
    private List<String> listOfDocReferencesInError;

    public CategoriseDocumentMessage(String messageId, List<String> listOfDocReferences) {
	super();
	this.messageId = messageId;
	this.listOfDocReferences = listOfDocReferences;
	this.listOfDocReferencesInError = new ArrayList<String>();
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

    public List<String> getListOfDocReferencesInError() {
	return listOfDocReferencesInError;
    }

    public void setListOfDocReferencesInError(List<String> listOfDocReferencesInError) {
	this.listOfDocReferencesInError = listOfDocReferencesInError;
    }

    public void addDocReferenceInError(String docReferenceInError) {
	this.listOfDocReferencesInError.add(docReferenceInError);
    }

    public boolean hasProcessingErrors() {
	return !listOfDocReferencesInError.isEmpty();
    }

}