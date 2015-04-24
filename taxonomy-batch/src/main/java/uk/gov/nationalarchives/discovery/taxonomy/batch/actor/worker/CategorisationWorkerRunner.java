package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.worker;

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
 * Worker Batch that works on categorising all documents<br/>
 * main task is to start the worker (worker) actor
 * 
 * @see CategorisationWorkerActor
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all.worker" })
@SuppressWarnings("rawtypes")
public class CategorisationWorkerRunner implements CommandLineRunner {

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final CategoriserService categoriserService;
    private final LuceneHelperTools luceneHelperTools;
    private final CategoryRepository categoryRepository;

    @Value("${batch.categorise-all.supervisor-hostname}")
    private String supervisorHostname;

    @Value("${batch.categorise-all.supervisor-port}")
    private String supervisorPort;

    @Value("${batch.categorise-all.afterDocNumber}")
    private Integer afterDocNumber;

    @Value("${batch.categorise-all.startEpic}")
    private Boolean startEpic;

    @Autowired
    public CategorisationWorkerRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
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
	String supervisorAddress = "akka.tcp://supervisor@" + supervisorHostname + ":" + supervisorPort
		+ "/user/supervisorActor";
	ActorRef worker = actorSystem.actorOf(Props.create(CategorisationWorkerActor.class, supervisorAddress,
		categoriserService, luceneHelperTools, categoryRepository), "workerActor");

	Thread.sleep(2000);

	if (this.startEpic) {
	    ActorSelection supervisorReference = actorSystem.actorSelection(supervisorAddress);
	    if (afterDocNumber == null) {
		supervisorReference.tell(new CategoriseAllDocumentsEpic(), worker);
	    } else {
		supervisorReference.tell(new CategoriseAllDocumentsEpic(this.afterDocNumber), worker);
	    }
	}
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
