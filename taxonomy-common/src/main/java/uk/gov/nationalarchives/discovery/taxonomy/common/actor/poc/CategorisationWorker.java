package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
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
	private List<String> docReferences;

	public CategoriseDocuments(List<String> docReferences) {
	    super();
	    this.docReferences = docReferences;
	}

	public List<String> getDocReferences() {
	    return docReferences;
	}
    }

    // @Autowired
    public CategorisationWorker() {
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CategoriseDocuments) {
	    List<String> docReferences = ((CategoriseDocuments) message).getDocReferences();
	    System.out.println(getSelf().hashCode() + ". categorising those: " + ArrayUtils.toString(docReferences));
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    for (String docReference : docReferences) {
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
