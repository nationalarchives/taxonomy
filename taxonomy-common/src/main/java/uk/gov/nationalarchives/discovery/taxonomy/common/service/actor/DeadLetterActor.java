package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Pong;
import akka.actor.DeadLetter;
import akka.actor.UntypedActor;

@Component("DeadLetter")
@Scope("prototype")
public class DeadLetterActor extends UntypedActor {

    // LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    // private static final Logger logger =
    // LoggerFactory.getLogger(DeadLetterActor.class);

    public DeadLetterActor() {
    }

    @Override
    public void onReceive(Object message) {
	if (message instanceof DeadLetter) {
	    Object innerMessage = ((DeadLetter) message).message();
	    if (innerMessage instanceof Pong) {
		System.out.println(".onReceive: encountered dead Pong");
		// logger.debug(".onReceive: encountered dead Pong: ", message);
	    } else {
		System.out.println(".onReceive: encountered dead letter: " + message);
		// logger.debug(".onReceive: encountered dead letter: ",
		// message);
	    }
	}
    }

}
