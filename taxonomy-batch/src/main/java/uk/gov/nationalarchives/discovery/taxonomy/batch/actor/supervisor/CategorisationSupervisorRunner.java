package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationSupervisorActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationSupervisorService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationWorkerActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.DeadLetterActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationSupervisorService.CategorisationStatus;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.Props;
import akka.routing.FromConfig;

;

/**
 * A main class to start up the application.
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all", "categorise-all.supervisor" })
public class CategorisationSupervisorRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CategorisationSupervisorRunner.class);

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
	logger.info("START WHOLE CATEGORISATION");

	// runWholeCategorisationInSeparateThread(categorisationSupervisorService);
	actorSystem.actorOf(Props.create(CategorisationSupervisorActor.class), "supervisorActor");

	CategorisationStatus status = null;
	do {
	    Thread.sleep(5000);
	    status = categorisationSupervisorService.getCategorisationStatus();
	    logger.info("PROGRESS OF CATEGORISATION: {}. index of last doc requested: {}", status,
		    categorisationSupervisorService.getCurrentDocIndex());
	} while (status.getProgress() != 100);

	logger.info("CATEGORISATION TERMINATED ON SUPERVISOR SIDE. Wait for slave to complete their tasks", status);
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }

    // private static Thread runWholeCategorisationInSeparateThread(
    // final CategorisationSupervisorService categorisationSupervisor) {
    // Thread thread = new Thread(new Runnable() {
    // public void run() {
    // categorisationSupervisor.startCategorisation();
    // }
    // });
    // thread.start();
    // return thread;
    // }
}
