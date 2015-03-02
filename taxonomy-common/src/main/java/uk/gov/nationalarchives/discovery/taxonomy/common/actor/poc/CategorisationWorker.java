package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    public static class CategoriseDocuments {
	private String[] docReferences;

	public CategoriseDocuments(String[] docReferences) {
	    super();
	    this.docReferences = docReferences;
	}

	public String[] getDocReferences() {
	    return docReferences;
	}
    }

    // @Autowired
    public CategorisationWorker() {
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    for (String docReference : ((CategoriseDocuments) message).getDocReferences()) {
		categoriseDocument(docReference);
	    }
	} else {
	    unhandled(message);
	}
    }

    private void categoriseDocument(String docReference) {
	System.out.println(".categoriseDocument: " + docReference);
    }
}
