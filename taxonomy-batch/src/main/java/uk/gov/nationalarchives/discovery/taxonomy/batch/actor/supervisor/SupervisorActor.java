/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.*;

import java.util.HashSet;
import java.util.Set;

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

    protected Set<ActorRef> workers = new HashSet<ActorRef>();

    protected Object currentEpic = null;

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof Epic) {
	    logger.debug("received Epic");
	    if (currentEpic != null)
            getSender().tell(new CurrentlyBusy(), getSelf());
        else if (workers.isEmpty())
            logger.error("Got work but there are no workers registered.");
        else {
            startEpic(message);
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

    protected abstract void startEpic(Object message);

    protected abstract void giveWork();

}
