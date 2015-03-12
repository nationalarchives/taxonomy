package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.actor.SpringApplicationContext;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CurrentlyBusy;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Epic;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.GimmeWork;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.RegisterWorker;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.WorkAvailable;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@SuppressWarnings("rawtypes")
public class CategorisationSupervisorActor extends UntypedActor {

    private static final String CATEGORISE_ALL = "CATEGORISE_ALL";

    public CategorisationSupervisorActor() {
	super();
    }

    Set<ActorRef> workers = new HashSet<ActorRef>();

    String currentEpic = null;

    private int categoriseDocsMessageNumber = 0;

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof Epic) {
	    getLogger().debug("received Epic");
	    if (currentEpic != null)
		getSender().tell(new CurrentlyBusy(), getSelf());
	    else if (workers.isEmpty())
		getLogger().error("Got work but there are no workers registered.");
	    else {
		getCategorisationSupervisorService().startCategorisation();
		categoriseDocsMessageNumber = 0;
		currentEpic = CATEGORISE_ALL;
		for (ActorRef actorRef : workers) {
		    actorRef.tell(new WorkAvailable(), getSelf());
		}
	    }
	} else if (message instanceof RegisterWorker) {
	    getLogger().debug("received RegisterWorker");
	    getLogger().info("worker {} registered", getSender());
	    getContext().watch(getSender());
	    workers.add(getSender());

	} else if (message instanceof Terminated) {
	    getLogger().debug("received Terminated");
	    getLogger().info("worker {} died - taking off the set of workers", getSender());
	    workers.remove(getSender());

	} else if (message instanceof GimmeWork) {
	    getLogger().debug("received GimmeWork");
	    if (currentEpic == null) {
		// if no more work:
		getLogger().info("workers asked for work but we've no more work to do");
	    } else {
		if (getCategorisationSupervisorService().hasDocumentsLeftFromDocIndex()) {
		    String[] nextDocReferences = getCategorisationSupervisorService().getNextDocumentsToCategorise();
		    if (nextDocReferences == null || nextDocReferences.length == 0) {
			getLogger().info("done with current epic");
			currentEpic = null;
		    }
		    categoriseDocsMessageNumber++;
		    getSender()
			    .tell(new CategoriseDocuments(nextDocReferences, categoriseDocsMessageNumber), getSelf());
		} else {
		    getLogger().info("done with current epic");
		    currentEpic = null;
		}
	    }
	} else {
	    unhandled(message);
	}
    }

    // FIXME 1 design flaw: how to test and mock the service in the actor? can
    // use
    // Power Mockito to mock static method but
    // i should rather find another solution to inject the dependency
    private CategorisationSupervisorService getCategorisationSupervisorService() {
	return (CategorisationSupervisorService) SpringApplicationContext.getBean("categorisationSupervisorService");
    }

    private Logger getLogger() {
	return LoggerFactory.getLogger(CategorisationSupervisorActor.class);
    }
}
