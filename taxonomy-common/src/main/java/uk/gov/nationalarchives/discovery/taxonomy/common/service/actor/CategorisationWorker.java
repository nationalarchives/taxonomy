package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.actor.SpringApplicationContext;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Ping;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Pong;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@SuppressWarnings("rawtypes")
public class CategorisationWorker extends UntypedActor {

    public CategorisationWorker() {
	super();
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    getLogger().info(".onReceive> {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    for (String docReference : docReferences) {
		getCategoriserService().categoriseSingle(docReference);
	    }
	    getLogger().info(".onReceive<  treatment completed");
	} else if (message instanceof Ping) {
	    getSender().tell(new Pong(), getSelf());
	} else {
	    unhandled(message);
	}
    }

    private CategoriserService getCategoriserService() {
	return (CategoriserService) SpringApplicationContext.getBean("categoriserService");
    }

    private Logger getLogger() {
	return LoggerFactory.getLogger(CategorisationWorker.class);
    }
}
