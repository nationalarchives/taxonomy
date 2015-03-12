package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.GimmeWork;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.RegisterWorker;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.WorkAvailable;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

@SuppressWarnings("rawtypes")
@Configurable(preConstruction = true)
public class CategorisationWorkerActor extends UntypedActor {

    @Autowired
    private CategoriserService categoriserService;

    private Logger logger;

    public CategorisationWorkerActor() {
	super();

	this.logger = LoggerFactory.getLogger(CategorisationWorkerActor.class);

	ActorSelection actorSelection = getContext().actorSelection(
		"akka.tcp://supervisor@127.0.0.1:2552/user/supervisorActor");
	actorSelection.tell(new RegisterWorker(), getSelf());
	actorSelection.tell(new GimmeWork(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof WorkAvailable) {
	    this.logger.debug("received WorkAvailable");
	    getSender().tell(new GimmeWork(), getSelf());

	} else if (message instanceof CategoriseDocuments) {
	    this.logger.debug("received CategoriseDocuments");
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    this.logger.info(".onReceive> {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    for (String docReference : docReferences) {
		// Thread.sleep(10);
		this.categoriserService.categoriseSingle(docReference);
	    }
	    this.logger.info(".onReceive<  treatment completed");

	    getSender().tell(new GimmeWork(), getSelf());
	} else {
	    unhandled(message);
	}
    }

    public void setCategoriserService(CategoriserService categoriserService) {
	this.categoriserService = categoriserService;
    }

}
