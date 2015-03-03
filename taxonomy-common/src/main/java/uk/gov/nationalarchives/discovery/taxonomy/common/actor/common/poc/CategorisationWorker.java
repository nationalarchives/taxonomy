package uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.domain.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.domain.Ping;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.domain.Pong;
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

    // @Autowired
    public CategorisationWorker() {
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    System.out.println(getSelf().hashCode() + ". categorising those: " + ArrayUtils.toString(docReferences));
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
	System.out.println(".categoriseDocument: " + docReference);
    }
}
