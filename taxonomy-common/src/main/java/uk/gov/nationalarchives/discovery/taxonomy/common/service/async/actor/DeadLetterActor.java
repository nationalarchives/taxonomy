package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;

@Component("DeadLetter")
@Scope("prototype")
public class DeadLetterActor extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterActor.class);

    public DeadLetterActor() {
    }

    @Override
    public void onReceive(Object message) {
	if (message instanceof DeadLetter) {
	    Object innerMessage = ((DeadLetter) message).message();
	    logger.warn(".onReceive: encountered dead letter: {}", innerMessage);
	}
    }

}
