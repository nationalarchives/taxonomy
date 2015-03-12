package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.actor.SpringApplicationContext;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.GimmeWork;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.RegisterWorker;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.WorkAvailable;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@SuppressWarnings("rawtypes")
public class CategorisationWorkerActor extends UntypedActor {

    public CategorisationWorkerActor() {
	super();
	ActorSelection actorSelection = getContext().actorSelection(
		"akka.tcp://supervisor@127.0.0.1:2552/user/supervisorActor");
	actorSelection.tell(new RegisterWorker(), getSelf());
	actorSelection.tell(new GimmeWork(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof WorkAvailable) {
	    getLogger().debug("received WorkAvailable");
	    getSender().tell(new GimmeWork(), getSelf());

	} else if (message instanceof CategoriseDocuments) {
	    getLogger().debug("received CategoriseDocuments");
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    getLogger().info(".onReceive> {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    for (String docReference : docReferences) {
		// Thread.sleep(10);
		getCategoriserService().categoriseSingle(docReference);
	    }
	    getLogger().info(".onReceive<  treatment completed");

	    getSender().tell(new GimmeWork(), getSelf());
	} else {
	    unhandled(message);
	}
    }

    private CategoriserService getCategoriserService() {
	return (CategoriserService) SpringApplicationContext.getBean("categoriserService");
    }

    private Logger getLogger() {
	return LoggerFactory.getLogger(CategorisationWorkerActor.class);
    }
}
