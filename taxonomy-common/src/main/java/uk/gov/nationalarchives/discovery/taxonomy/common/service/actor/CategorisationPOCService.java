package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class CategorisationPOCService {

    private static final int NB_OF_FAKE_DOCS = 20;
    List<String> docReferences;

    @PostConstruct
    private void initPoc() {
	this.docReferences = generateDocRefArray();
    }

    public List<String> getNextXDocuments(int indexOfNextElement, int nbOfElementsToProcess) {
	int toIndex;
	if (docReferences.size() > indexOfNextElement + nbOfElementsToProcess) {
	    toIndex = indexOfNextElement + nbOfElementsToProcess;
	} else {
	    toIndex = docReferences.size();
	}
	List<String> subList = docReferences.subList(indexOfNextElement, toIndex);
	return subList;
    }

    private List<String> generateDocRefArray() {
	List<String> listOfReferences = new ArrayList<String>();
	for (int i = 0; i < NB_OF_FAKE_DOCS; i++) {
	    listOfReferences.add("doc_" + (i + 1));
	}
	return listOfReferences;
    }

    public boolean hasNextXDocuments(int indexOfNextElementToCategorise) {
	return indexOfNextElementToCategorise < docReferences.size();
    }

    public int getTotalNbOfDocs() {
	return docReferences.size();
    }
}
