package uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc;

import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationWorker.CategoriseDocuments;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

@Component("CategorisationWorkerRouter")
@Scope("prototype")
public class CategorisationWorkerRouter extends UntypedActor {

    // public static class GetNbOfAvailableActors {
    //
    // }

    private Router router;
    {
	List<Routee> routees = new ArrayList<Routee>();
	for (int i = 0; i < 2; i++) {
	    ActorRef r = getContext().actorOf(Props.create(CategorisationWorker.class));
	    getContext().watch(r);
	    routees.add(new ActorRefRoutee(r));
	}
	router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    public void onReceive(Object msg) {
	if (msg instanceof CategoriseDocuments) {
	    router.route(msg, getSender());
	    // } else if (msg instanceof GetNbOfAvailableActors) {
	    // for (Routee routee :
	    // scala.collection.JavaConversions.asJavaCollection(router.routees()))
	    // {
	    // }

	    // getSender().tell(router.routees().size(), getSelf());

	} else if (msg instanceof Terminated) {
	    router = router.removeRoutee(((Terminated) msg).actor());
	    ActorRef r = getContext().actorOf(Props.create(CategorisationWorker.class));
	    getContext().watch(r);
	    router = router.addRoutee(new ActorRefRoutee(r));
	}
    }
}
