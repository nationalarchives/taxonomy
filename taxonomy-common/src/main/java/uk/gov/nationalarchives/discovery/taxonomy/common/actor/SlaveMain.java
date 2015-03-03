package uk.gov.nationalarchives.discovery.taxonomy.common.actor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.DeadLetterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.DeadLetter;
import akka.actor.Props;

;

/**
 * A main class to start up the application.
 */
public class SlaveMain {

    public static void main(String[] args) throws Exception {
	AnnotationConfigApplicationContext ctx = createActorPOCContext();

	// get hold of the actor system
	ActorSystem system = ctx.getBean("actorSystem", ActorSystem.class);
	ActorSystem deadLettersSystem = ctx.getBean("deadLettersActorSystem", ActorSystem.class);
	final ActorRef actor = deadLettersSystem.actorOf(Props.create(DeadLetterActor.class));
	system.eventStream().subscribe(actor, AllDeadLetters.class);
	deadLettersSystem.eventStream().subscribe(actor, DeadLetter.class);

    }

    private static AnnotationConfigApplicationContext createActorPOCContext() {
	// create a spring context and scan the classes
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	ctx.scan("uk.gov.nationalarchives.discovery.taxonomy.common.actor.common");
	ctx.scan("uk.gov.nationalarchives.discovery.taxonomy.common.actor.slave");
	ctx.refresh();
	return ctx;
    }
}
