package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Ping;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Pong;
import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@Component("CategorisationWorker")
@Scope("prototype")
public class CategorisationWorker extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(CategorisationWorker.class);

    // @Autowired
    public CategorisationWorker() {
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    logger.info(".onReceive: {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    for (String docReference : docReferences) {
		categoriseDocument(docReference);
	    }
	} else if (message instanceof Ping) {
	    getSender().tell(new Pong(), getSelf());
	} else {
	    unhandled(message);
	}
    }

    private void categoriseDocument(String docReference) {
	// logger.debug(".categoriseDocument: {}", docReference);
    }
}
