package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor.CategorisationSupervisorService.CategorisationStatus;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.DeadLetterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.Props;

;

/**
 * A main class to start up the application.
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all", "categorise-all.supervisor" })
public class CategorisationSupervisorRunner implements CommandLineRunner {

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final CategorisationSupervisorService categorisationSupervisorService;

    @Autowired
    public CategorisationSupervisorRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
	    CategorisationSupervisorService categorisationSupervisor) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
	this.categorisationSupervisorService = categorisationSupervisor;
    }

    @Override
    public void run(String... args) throws Exception {
	trackDeadLetters();

	testPOC(categorisationSupervisorService);
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }

    private static void testPOC(final CategorisationSupervisorService categorisationSupervisor) throws Exception {

	runWholeCategorisationInSeparateThread(categorisationSupervisor);

	CategorisationStatus status = null;
	while (status == null || status.getProgress() != 100) {
	    System.out.println("PROGRESS OF CATEGORISATION: " + status);
	    Thread.sleep(600);
	    status = categorisationSupervisor.getCategorisationStatus();
	}
	System.out.println("PROGRESS OF CATEGORISATION: " + status);

    }

    private static Thread runWholeCategorisationInSeparateThread(
	    final CategorisationSupervisorService categorisationSupervisor) {
	Thread thread = new Thread(new Runnable() {
	    public void run() {
		categorisationSupervisor.categoriseAllDocuments();
	    }
	});
	thread.start();
	return thread;
    }
}
