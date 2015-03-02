package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import static uk.gov.nationalarchives.discovery.taxonomy.common.actor.SpringExtension.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorker.CategoriseDocuments;
//import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorkerRouter.GetNbOfAvailableActors;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample.CountingActor.Get;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.*;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.util.Timeout;

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
    // int nbOfAvailableActors;

    private final CategorisationPOCService categorisationPOCService;

    private ActorRef categorisationWorkerRouter;

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

	this.categorisationWorkerRouter = getContext().actorOf(
		SpringExtProvider.get(getContext().system()).props("CategorisationWorkerRouter"),
		"categorisationWorkerRouter");
	// this.nbOfAvailableActors = askRouterItsNbOfAvailableActors();
    }

    // private int askRouterItsNbOfAvailableActors() {
    // FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
    // Future<Object> result = ask(categorisationWorkerRouter, new
    // GetNbOfAvailableActors(),
    // Timeout.durationToTimeout(duration));
    // int nbOfAvailableActors = 0;
    // try {
    // nbOfAvailableActors = (int) Await.result(result, duration);
    // System.out.println("there are " + nbOfAvailableActors +
    // " actors available");
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return nbOfAvailableActors;
    // }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseAllDocuments) {
	    this.indexOfNextElementToCategorise = 0;
	    this.totalNbOfDocs = categorisationPOCService.getTotalNbOfDocs();

	    System.out.println("CategoriseAllDocuments request");
	    while (categorisationPOCService.hasNextXDocuments(indexOfNextElementToCategorise)) {
		// while (nbOfAvailableActors == 0) {
		// Thread.sleep(1000);
		// this.nbOfAvailableActors = askRouterItsNbOfAvailableActors();
		// }
		int indexOfNextElement = getIndexOfNextElementThenIncrement(NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);
		System.out.println("nxt element: " + indexOfNextElement);

		categoriseXNextDocuments(indexOfNextElement);
		// this.nbOfAvailableActors--;
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
	categorisationWorkerRouter.tell(new CategoriseDocuments(docReferences), getSelf());
    }

    synchronized private int getIndexOfNextElementThenIncrement(int nbOfElementsToProcess) {
	Integer indexToReturn = new Integer(this.indexOfNextElementToCategorise);
	this.indexOfNextElementToCategorise = indexOfNextElementToCategorise + nbOfElementsToProcess;
	return indexToReturn;
    }
}
