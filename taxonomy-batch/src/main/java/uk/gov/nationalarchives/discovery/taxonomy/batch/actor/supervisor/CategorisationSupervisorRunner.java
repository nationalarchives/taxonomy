package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationSupervisorActor;
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
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all.supervisor" })
public class CategorisationSupervisorRunner implements CommandLineRunner {

    // private static final Logger logger =
    // LoggerFactory.getLogger(CategorisationSupervisorRunner.class);

    @Value("${batch.categorise-all.message-size}")
    private int nbOfDocsToCategoriseAtATime;

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;

    @Autowired
    public CategorisationSupervisorRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
    }

    @Override
    public void run(String... args) throws Exception {
	trackDeadLetters();

	actorSystem.actorOf(Props.create(CategorisationSupervisorActor.class, nbOfDocsToCategoriseAtATime),
		"supervisorActor");
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
