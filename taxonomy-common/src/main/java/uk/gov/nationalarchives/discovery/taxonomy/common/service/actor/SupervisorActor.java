package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CurrentlyBusy;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Epic;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.GimmeWork;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.RegisterWorker;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.WorkAvailable;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * reusing the code from scala class on that brilliant article
 * http://www.michaelpollmeier.com/akka-work-pulling-pattern/
 * 
 * @author jcharlet
 *
 */
public abstract class SupervisorActor extends UntypedActor {

    protected Logger logger;

    public SupervisorActor() {
	super();
	logger = LoggerFactory.getLogger(getClass());
    }

    private Set<ActorRef> workers = new HashSet<ActorRef>();

    protected String currentEpic = null;

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof Epic) {
	    logger.debug("received Epic");
	    if (currentEpic != null)
		getSender().tell(new CurrentlyBusy(), getSelf());
	    else if (workers.isEmpty())
		logger.error("Got work but there are no workers registered.");
	    else {
		startEpic();
		for (ActorRef actorRef : workers) {
		    actorRef.tell(new WorkAvailable(), getSelf());
		}
	    }
	} else if (message instanceof RegisterWorker) {
	    logger.debug("received RegisterWorker");
	    logger.info("worker {} registered", getSender());
	    getContext().watch(getSender());
	    workers.add(getSender());

	} else if (message instanceof Terminated) {
	    logger.debug("received Terminated");
	    logger.info("worker {} died - taking off the set of workers", getSender());
	    workers.remove(getSender());

	} else if (message instanceof GimmeWork) {
	    logger.debug("received GimmeWork");
	    if (currentEpic == null) {
		logger.info("workers asked for work but we've no more work to do");
	    } else {
		giveWork();
	    }
	} else {
	    unhandled(message);
	}
    }

    protected abstract void startEpic();

    protected abstract void giveWork();

}
