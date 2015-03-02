package uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.UntypedActor;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 *       instance for use of this bean.
 */
@Component("CountingActor")
@Scope("prototype")
public class CountingActor extends UntypedActor {

    public static class Count {
    }

    public static class Get {
    }

    // the service that will be automatically injected
    final CountingService countingService;

    @Autowired
    public CountingActor(CountingService countingService) {
	this.countingService = countingService;
    }

    private int count = 0;

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof Count) {
	    count = countingService.increment(count);
	} else if (message instanceof Get) {
	    getSender().tell(count, getSelf());
	} else {
	    unhandled(message);
	}
    }
}
