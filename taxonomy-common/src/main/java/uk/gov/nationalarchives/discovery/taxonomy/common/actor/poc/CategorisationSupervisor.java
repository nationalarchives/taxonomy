package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@Component("CategorisationSupervisor")
@Scope("prototype")
public class CategorisationSupervisor extends UntypedActor {

    private static final int NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME = 2;

    int indexOfNextElementToCategorise;
    int totalNbOfDocs;

    private final CategorisationPOCService categorisationPOCService;

    public static class GetCategorisationStatus {

    }

    public static class CategoriseAllDocuments {
    }

    public class CategorisationStatus {
	public int progress;

	public CategorisationStatus(int progress) {
	    super();
	    this.progress = progress;
	}

	@Override
	public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("CategorisationStatus [progress=");
	    builder.append(progress);
	    builder.append("]");
	    return builder.toString();
	}

    }

    @Autowired
    public CategorisationSupervisor(CategorisationPOCService categorisationPOCService) {
	this.categorisationPOCService = categorisationPOCService;
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseAllDocuments) {
	    this.indexOfNextElementToCategorise = 0;
	    this.totalNbOfDocs = categorisationPOCService.getTotalNbOfDocs();

	    System.out.println("CategoriseAllDocuments request");
	    while (categorisationPOCService.hasNextXDocuments(indexOfNextElementToCategorise)) {
		int indexOfNextElement = getIndexOfNextElementThenIncrement(NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);
		System.out.println("nxt element: " + indexOfNextElement);

		categoriseXNextDocuments(indexOfNextElement);
	    }

	} else if (message instanceof GetCategorisationStatus) {
	    System.out.println("GetCategorisationStatus request");
	    getSender().tell(getCategorisationStatus(), getSelf());
	} else {
	    unhandled(message);
	}
    }

    private Object getCategorisationStatus() {
	int progress = indexOfNextElementToCategorise / totalNbOfDocs;
	return new CategorisationStatus(progress);
    }

    private void categoriseXNextDocuments(int indexOfNextElement) {
	List<String> docReferences = categorisationPOCService.getNextXDocuments(indexOfNextElement,
		NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);

	// ask worker to categorise those docs.
	System.out.println(getSelf().hashCode() + ". categorising those: " + ArrayUtils.toString(docReferences));
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    synchronized private int getIndexOfNextElementThenIncrement(int nbOfElementsToProcess) {
	Integer indexToReturn = new Integer(this.indexOfNextElementToCategorise);
	this.indexOfNextElementToCategorise = indexOfNextElementToCategorise + nbOfElementsToProcess;
	return indexToReturn;
    }
}
