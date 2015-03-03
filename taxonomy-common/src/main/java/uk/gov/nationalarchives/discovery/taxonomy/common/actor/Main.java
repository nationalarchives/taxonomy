package uk.gov.nationalarchives.discovery.taxonomy.common.actor;

import static akka.pattern.Patterns.*;
import static uk.gov.nationalarchives.discovery.taxonomy.common.actor.SpringExtension.*;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationSupervisor;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationSupervisor.CategorisationStatus;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.DeadLetterActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample.CountingActor.Count;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample.CountingActor.Get;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.util.Timeout;

;

/**
 * A main class to start up the application.
 */
public class Main {

    public static void main(String[] args) throws Exception {
	AnnotationConfigApplicationContext ctx = createActorPOCContext();

	// get hold of the actor system
	ActorSystem system = ctx.getBean("actorSystem", ActorSystem.class);
	ActorSystem deadLettersSystem = ctx.getBean("deadLettersActorSystem", ActorSystem.class);
	final ActorRef actor = deadLettersSystem.actorOf(Props.create(DeadLetterActor.class));
	system.eventStream().subscribe(actor, AllDeadLetters.class);
	// actorSystem.actorOf(
	// SpringExtProvider.get(actorSystem).props("CategorisationWorkerRouter"),
	// "categorisationWorkerRouter");

	deadLettersSystem.eventStream().subscribe(actor, DeadLetter.class);

	final CategorisationSupervisor categorisationSupervisor = ctx.getBean(CategorisationSupervisor.class);

	try {
	    testPOC(categorisationSupervisor);
	    // testActorSample(system);
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

    private static void testActorSample(ActorSystem system) throws Exception {
	// use the Spring Extension to create props for a named actor bean
	ActorRef counter = system.actorOf(SpringExtProvider.get(system).props("CountingActor"), "counter");

	// tell it to count three times
	counter.tell(new Count(), null);
	counter.tell(new Count(), null);
	counter.tell(new Count(), null);

	// print the result
	FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
	Future<Object> result = ask(counter, new Get(), Timeout.durationToTimeout(duration));
	try {
	    System.out.println("Got back " + Await.result(result, duration));
	} catch (Exception e) {
	    System.err.println("Failed getting result: " + e.getMessage());
	    throw e;
	}
    }

    private static AnnotationConfigApplicationContext createActorPOCContext() {
	// create a spring context and scan the classes
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	ctx.scan("uk.gov.nationalarchives.discovery.taxonomy.common.actor");
	ctx.refresh();
	return ctx;
    }
}
