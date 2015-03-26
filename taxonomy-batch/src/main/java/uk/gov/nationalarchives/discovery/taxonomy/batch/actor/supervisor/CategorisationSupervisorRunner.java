package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.CategorisationSupervisorActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.CategorisationWorkerActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.DeadLetterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.Props;

;

/**
 * Supervisor Batch that works on categorising all documents<br/>
 * main task is to start the supervisor actor
 * 
 * @see CategorisationSupervisorActor
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all.supervisor" })
@SuppressWarnings("rawtypes")
public class CategorisationSupervisorRunner implements CommandLineRunner {

    // private static final Logger logger =
    // LoggerFactory.getLogger(CategorisationSupervisorRunner.class);

    @Value("${batch.categorise-all.message-size}")
    private int nbOfDocsToCategoriseAtATime;

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final IAViewService iaViewService;
    private final CategoriserService categoriserService;

    @Autowired
    public CategorisationSupervisorRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
	    IAViewService iaViewService, CategoriserService categoriserService) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
	this.iaViewService = iaViewService;
	this.categoriserService = categoriserService;
    }

    @Override
    public void run(String... args) throws Exception {
	trackDeadLetters();

	actorSystem.actorOf(Props.create(CategorisationSupervisorActor.class, nbOfDocsToCategoriseAtATime,
		iaViewService, categoriserService), "supervisorActor");
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
