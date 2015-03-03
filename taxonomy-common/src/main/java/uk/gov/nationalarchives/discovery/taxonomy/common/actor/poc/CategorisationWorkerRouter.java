package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorker.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.domain.Ping;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;

@Component("CategorisationWorkerRouter")
@Scope("prototype")
public class CategorisationWorkerRouter extends UntypedActor {

    private Router router;
    {
	List<Routee> routees = new ArrayList<Routee>();
	for (int i = 0; i < 2; i++) {
	    ActorRef r = getContext().actorOf(Props.create(CategorisationWorker.class));
	    getContext().watch(r);
	    routees.add(new ActorRefRoutee(r));
	}
	router = new Router(new SmallestMailboxRoutingLogic(), routees);
    }

    public void onReceive(Object msg) {
	if (msg instanceof CategoriseDocuments || msg instanceof Ping) {
	    router.route(msg, getSender());

	} else if (msg instanceof Terminated) {
	    router = router.removeRoutee(((Terminated) msg).actor());
	    ActorRef r = getContext().actorOf(Props.create(CategorisationWorker.class));
	    getContext().watch(r);
	    router = router.addRoutee(new ActorRefRoutee(r));
	}
    }
}
