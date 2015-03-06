package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Pong;
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
	    if (innerMessage instanceof Pong) {
		logger.debug(".onReceive: encountered dead Pong");
	    } else {
		logger.debug(".onReceive: encountered dead letter: {}", innerMessage);
	    }
	}
    }

}
