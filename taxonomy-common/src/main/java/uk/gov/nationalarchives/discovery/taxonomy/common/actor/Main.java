package uk.gov.nationalarchives.discovery.taxonomy.common.actor;

import static akka.pattern.Patterns.*;
import static uk.gov.nationalarchives.discovery.taxonomy.common.actor.SpringExtension.*;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample.CountingActor.Count;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.sample.CountingActor.Get;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationSupervisor.CategoriseAllDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationSupervisor.GetCategorisationStatus;
import uk.gov.nationalarchives.discovery.taxonomy.common.actor.poc.CategorisationSupervisor.CategorisationStatus;

;

/**
 * A main class to start up the application.
 */
public class Main {
    public static void main(String[] args) throws Exception {
	AnnotationConfigApplicationContext ctx = createActorPOCContext();

	// get hold of the actor system
	ActorSystem system = ctx.getBean(ActorSystem.class);

	try {
	    testPOC(system);
	    // testActorSample(system);
	} finally {
	    system.shutdown();
	    system.awaitTermination();
	}
    }

    private static void testPOC(ActorSystem system) throws Exception {
	ActorRef supervisor = system.actorOf(SpringExtProvider.get(system).props("CategorisationSupervisor"),
		"supervisor");

	supervisor.tell(new CategoriseAllDocuments(), null);

	// print the result
	askCategorisationStatus(supervisor);
	Thread.sleep(2000);
	askCategorisationStatus(supervisor);

    }

    private static void askCategorisationStatus(ActorRef supervisor) {
	FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
	Future<Object> result = ask(supervisor, new GetCategorisationStatus(), Timeout.durationToTimeout(duration));
	try {
	    System.out.println("Got back " + (CategorisationStatus) Await.result(result, duration));
	} catch (Exception e) {
	    System.err.println("Failed getting result: " + e.getMessage());
	}
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
