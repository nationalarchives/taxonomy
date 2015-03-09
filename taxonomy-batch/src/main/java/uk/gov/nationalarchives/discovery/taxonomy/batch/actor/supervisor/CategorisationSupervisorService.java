package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import static akka.pattern.Patterns.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.Ping;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.actor.CategorisationWorker;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.util.Timeout;

@Service
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all", "categorise-all.supervisor" })
@SuppressWarnings("rawtypes")
public class CategorisationSupervisorService {

    private static final Logger logger = LoggerFactory.getLogger(CategorisationSupervisorService.class);
    private static final int LIMIT_OF_RETRIES = 100;

    private static final int NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME = 100;

    int totalNbOfDocs;
    private ScoreDoc lastElementRetrieved;

    private final IAViewService iaViewService;
    private final CategoriserService categoriserService;

    private ActorRef categorisationWorkerRouter;

    @Autowired
    public CategorisationSupervisorService(IAViewService iaViewService, ActorSystem actorSystem,
	    CategoriserService categoriserService) {
	this.iaViewService = iaViewService;
	this.categoriserService = categoriserService;

	this.categorisationWorkerRouter = actorSystem.actorOf(
		FromConfig.getInstance().props(Props.create(CategorisationWorker.class)), "categorisation-router");

	lastElementRetrieved = null;
    }

    public class CategorisationStatus {
	public int progress;

	public CategorisationStatus(int progress) {
	    super();
	    this.progress = progress;
	}

	public int getProgress() {
	    return progress;
	}

	@Override
	public String toString() {
	    StringBuilder builder = new StringBuilder();
	    builder.append("CategorisationStatus [progress=");
	    builder.append(progress);
	    builder.append("]");
	    return builder.toString();
	}

    }

    public void categoriseAllDocuments() {
	categoriserService.refreshTaxonomyIndex();

	this.totalNbOfDocs = iaViewService.getTotalNbOfDocs();

	if (totalNbOfDocs == 0) {
	    throw new TaxonomyException(TaxonomyErrorType.DOC_NOT_FOUND, "The index is empty");
	}

	logger.info(".categoriseAllDocuments: categorizing {} documents", this.totalNbOfDocs);
	do {
	    waitForAvailableWorker();

	    categoriseNextDocuments();
	} while (hasDocumentsLeftFromDocIndex());

    }

    private boolean hasDocumentsLeftFromDocIndex() {
	if (this.lastElementRetrieved == null) {
	    return false;
	}
	return this.lastElementRetrieved.doc < this.totalNbOfDocs;
    }

    public CategorisationStatus getCategorisationStatus() {
	if (this.lastElementRetrieved == null) {
	    return new CategorisationStatus(100);
	}
	int progress = 100 * this.lastElementRetrieved.doc / this.totalNbOfDocs;
	return new CategorisationStatus(progress);
    }

    public int getCurrentDocIndex() {
	return lastElementRetrieved.doc;
    }

    private void waitForAvailableWorker() {
	int nbOfRetries = 0;
	while (!askForAvailableWorker() && nbOfRetries < LIMIT_OF_RETRIES) {
	    nbOfRetries++;
	}

    }

    private boolean askForAvailableWorker() {
	FiniteDuration duration = FiniteDuration.create(500, TimeUnit.MILLISECONDS);
	Future<Object> result = ask(categorisationWorkerRouter, new Ping(), Timeout.durationToTimeout(duration));
	try {
	    Await.result(result, duration);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    /**
     * This method must be synchronized because lastElementRetrieved must be
     * updated before the next bunch of documents can be processed
     */
    private synchronized void categoriseNextDocuments() {
	BrowseAllDocsResponse browseAllDocs = iaViewService.browseAllDocs(this.lastElementRetrieved,
		NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);

	this.lastElementRetrieved = browseAllDocs.getLastScoreDoc();

	List<String> listOfDocReferences = browseAllDocs.getListOfDocReferences();
	if (!CollectionUtils.isEmpty(listOfDocReferences)) {
	    logger.info(
		    ".categoriseAllDocuments: submitting request to categorise documents: lastScoreDoc={}, size={}, docs={}",
		    this.lastElementRetrieved, NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME,
		    ArrayUtils.toString(listOfDocReferences));

	    // ask worker to categorise those docs.
	    categorisationWorkerRouter.tell(new CategoriseDocuments(listOfDocReferences.toArray(new String[0])), null);
	}
    }
}
