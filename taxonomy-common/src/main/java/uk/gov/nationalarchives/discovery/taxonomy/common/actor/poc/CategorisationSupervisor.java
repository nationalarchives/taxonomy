package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import static akka.pattern.Patterns.*;
import static uk.gov.nationalarchives.discovery.taxonomy.common.actor.SpringExtension.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorker.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.domain.Ping;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;

//import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorkerRouter.GetNbOfAvailableActors;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@Service
public class CategorisationSupervisor {

    private static final int LIMIT_OF_RETRIES = 100;

    private static final int NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME = 2;

    int indexOfNextElementToCategorise;
    int totalNbOfDocs;

    private final CategorisationPOCService categorisationPOCService;

    private ActorRef categorisationWorkerRouter;

    @Autowired
    public CategorisationSupervisor(CategorisationPOCService categorisationPOCService, ActorSystem actorSystem) {
	this.categorisationPOCService = categorisationPOCService;

	this.categorisationWorkerRouter = actorSystem.actorOf(
		SpringExtProvider.get(actorSystem).props("CategorisationWorkerRouter"), "categorisationWorkerRouter");
    }

    public class CategorisationStatus {
	public int progress;

	public CategorisationStatus(int progress) {
	    super();
	    this.progress = progress;
	}

	public int getProgress() {
	    return progress;
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

    public void categoriseAllDocuments() {
	this.indexOfNextElementToCategorise = 0;
	this.totalNbOfDocs = categorisationPOCService.getTotalNbOfDocs();

	System.out.println("CategoriseAllDocuments request");
	while (categorisationPOCService.hasNextXDocuments(indexOfNextElementToCategorise)) {
	    waitForAvailableWorker();

	    int indexOfNextElement = getIndexOfNextElementThenIncrement(NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);
	    System.out.println("nxt element: " + indexOfNextElement);

	    categoriseXNextDocuments(indexOfNextElement);
	}
    }

    public CategorisationStatus getCategorisationStatus() {
	if (totalNbOfDocs == 0) {
	    return null;
	}
	int progress = 100 * indexOfNextElementToCategorise / totalNbOfDocs;
	return new CategorisationStatus(progress);
    }

    private void waitForAvailableWorker() {
	int nbOfRetries = 0;
	while (!askForAvailableWorker() && nbOfRetries < LIMIT_OF_RETRIES) {
	    nbOfRetries++;
	}

    }

    private boolean askForAvailableWorker() {
	FiniteDuration duration = FiniteDuration.create(500, TimeUnit.MILLISECONDS);
	Future<Object> result = ask(categorisationWorkerRouter, new Ping(), Timeout.durationToTimeout(duration));
	try {
	    Await.result(result, duration);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    private void categoriseXNextDocuments(int indexOfNextElement) {
	List<String> docReferences = categorisationPOCService.getNextXDocuments(indexOfNextElement,
		NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);

	// ask worker to categorise those docs.
	categorisationWorkerRouter.tell(new CategoriseDocuments(docReferences), null);
    }

    synchronized private int getIndexOfNextElementThenIncrement(int nbOfElementsToProcess) {
	Integer indexToReturn = new Integer(this.indexOfNextElementToCategorise);
	this.indexOfNextElementToCategorise = indexOfNextElementToCategorise + nbOfElementsToProcess;
	return indexToReturn;
    }
}
