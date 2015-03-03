package uk.gov.nationalarchives.discovery.taxonomy.common.actor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.poc.DeadLetterActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.supervisor.CategorisationSupervisor;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.supervisor.CategorisationSupervisor.CategorisationStatus;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.DeadLetter;
import akka.actor.Props;

;

/**
 * A main class to start up the application.
 */
public class SupervisorMain {

    public static void main(String[] args) throws Exception {
	AnnotationConfigApplicationContext ctx = createActorPOCContext();

	// get hold of the actor system
	ActorSystem system = ctx.getBean("actorSystem", ActorSystem.class);
	ActorSystem deadLettersSystem = ctx.getBean("deadLettersActorSystem", ActorSystem.class);
	final ActorRef actor = deadLettersSystem.actorOf(Props.create(DeadLetterActor.class));
	system.eventStream().subscribe(actor, AllDeadLetters.class);

	deadLettersSystem.eventStream().subscribe(actor, DeadLetter.class);

	final CategorisationSupervisor categorisationSupervisor = ctx.getBean(CategorisationSupervisor.class);

	try {
	    testPOC(categorisationSupervisor);
	} finally {
	    system.shutdown();
	    system.awaitTermination();
	    deadLettersSystem.shutdown();
	    deadLettersSystem.awaitTermination();
	    ctx.close();
	}
    }

    private static void testPOC(final CategorisationSupervisor categorisationSupervisor) throws Exception {

	Thread thread = runWholeCategorisationInSeparateThread(categorisationSupervisor);

	CategorisationStatus status = null;
	while (status == null || status.getProgress() != 100) {
	    System.out.println("PROGRESS OF CATEGORISATION: " + status);
	    Thread.sleep(600);
	    status = categorisationSupervisor.getCategorisationStatus();
	}
	System.out.println("PROGRESS OF CATEGORISATION: " + status);

    }

    private static Thread runWholeCategorisationInSeparateThread(final CategorisationSupervisor categorisationSupervisor) {
	Thread thread = new Thread(new Runnable() {
	    public void run() {
		categorisationSupervisor.categoriseAllDocuments();
	    }
	});
	thread.start();
	return thread;
    }

    private static AnnotationConfigApplicationContext createActorPOCContext() {
	// create a spring context and scan the classes
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	ctx.scan("uk.gov.nationalarchives.discovery.taxonomy.common.actor.common");
	ctx.scan("uk.gov.nationalarchives.discovery.taxonomy.common.actor.supervisor");
	ctx.refresh();
	return ctx;
    }
}
