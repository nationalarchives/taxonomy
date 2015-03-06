package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
@Component("CategorisationWorker")
@Scope("prototype")
@SuppressWarnings("rawtypes")
public class CategorisationWorker extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(CategorisationWorker.class);

    final CategoriserService categoriserService;

    @Autowired
    public CategorisationWorker(CategoriserService categoriserService) {
	this.categoriserService = categoriserService;
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    logger.info(".onReceive: {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    for (String docReference : docReferences) {
		categoriserService.categoriseSingle(docReference);
	    }
	} else if (message instanceof Ping) {
	    getSender().tell(new Pong(), getSelf());
	} else {
	    unhandled(message);
	}
    }
}
