package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.slave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseAllDocumentsEpic;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.CategorisationWorkerActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.DeadLetterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.Props;

;

/**
 * Slave Batch that works on categorising all documents<br/>
 * main task is to start the worker (slave) actor
 * 
 * @see CategorisationWorkerActor
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all.slave" })
@SuppressWarnings("rawtypes")
public class CategorisationSlaveRunner implements CommandLineRunner {

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final CategoriserService categoriserService;
    private final LuceneHelperTools luceneHelperTools;
    private final CategoryRepository categoryRepository;

    @Value("${batch.categorise-all.supervisor-address}")
    private String supervisorAddress;

    @Value("${batch.categorise-all.afterDocNumber}")
    private Integer afterDocNumber;

    @Value("${batch.categorise-all.startEpic}")
    private Boolean startEpic;

    @Autowired
    public CategorisationSlaveRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
	    CategoriserService categoriserService, LuceneHelperTools luceneHelperTools,
	    CategoryRepository categoryRepository) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
	this.categoriserService = categoriserService;
	this.luceneHelperTools = luceneHelperTools;
	this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
	// Integer afterDocNumber = getAfterDocNumberFromArgs(args);

	trackDeadLetters();
	actorSystem.actorOf(Props.create(CategorisationWorkerActor.class, supervisorAddress, categoriserService,
		luceneHelperTools, categoryRepository), "workerActor");

	Thread.sleep(2000);

	if (this.startEpic) {
	    ActorSelection actorSelection = actorSystem.actorSelection(supervisorAddress);
	    if (afterDocNumber == null) {
		actorSelection.tell(new CategoriseAllDocumentsEpic(), null);
	    } else {
		actorSelection.tell(new CategoriseAllDocumentsEpic(this.afterDocNumber), null);
	    }
	}
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
