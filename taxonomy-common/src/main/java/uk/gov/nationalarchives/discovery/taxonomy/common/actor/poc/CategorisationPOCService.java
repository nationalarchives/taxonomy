package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class CategorisationPOCService {

    List<String> docReferences;

    @PostConstruct
    private void initPoc() {
	this.docReferences = generateDocRefArray();
    }

    public List<String> getNextXDocuments(int indexOfNextElement, int nbOfElementsToProcess) {
	List<String> subList = docReferences.subList(indexOfNextElement, indexOfNextElement + nbOfElementsToProcess);
	return subList;
    }

    private List<String> generateDocRefArray() {
	List<String> listOfReferences = new ArrayList<String>();
	for (int i = 0; i < 10; i++) {
	    listOfReferences.add("doc_" + i);
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
