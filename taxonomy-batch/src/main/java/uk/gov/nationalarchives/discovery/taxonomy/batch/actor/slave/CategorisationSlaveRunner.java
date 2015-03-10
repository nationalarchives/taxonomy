package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.slave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationWorker;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.DeadLetterActor;
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
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all", "categorise-all.slave" })
public class CategorisationSlaveRunner implements CommandLineRunner {

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;

    @Autowired
    public CategorisationSlaveRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
    }

    @Override
    public void run(String... args) throws Exception {
	trackDeadLetters();
	// actorSystem.actorOf(FromConfig.getInstance().props(Props.create(CategorisationWorker.class)),
	// "worker");
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
