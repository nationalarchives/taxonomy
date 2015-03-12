package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import akka.actor.ActorSystem;

@Service("categorisationSupervisorService")
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all", "categorise-all.supervisor" })
@SuppressWarnings("rawtypes")
public class CategorisationSupervisorService {

    private static final Logger logger = LoggerFactory.getLogger(CategorisationSupervisorService.class);

    private static final int NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME = 100;

    private Integer totalNbOfDocs;
    private ScoreDoc lastElementRetrieved = null;
    private Status status = Status.NOT_STARTED;

    private enum Status {
	NOT_STARTED, INITIATED, ONGOING, TERMINATED
    }

    private final IAViewService iaViewService;
    private final CategoriserService categoriserService;

    // private ActorRef categorisationWorkerRouter;

    @Autowired
    public CategorisationSupervisorService(IAViewService iaViewService, ActorSystem actorSystem,
	    CategoriserService categoriserService) {
	this.iaViewService = iaViewService;
	this.categoriserService = categoriserService;
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

    public void startCategorisation() {
	categoriserService.refreshTaxonomyIndex();

	this.totalNbOfDocs = iaViewService.getTotalNbOfDocs();

	if (totalNbOfDocs == 0) {
	    throw new TaxonomyException(TaxonomyErrorType.DOC_NOT_FOUND, "The index is empty");
	}

	logger.info(".categoriseAllDocuments: categorizing {} documents", this.totalNbOfDocs);
	status = Status.INITIATED;
	lastElementRetrieved = null;
    }

    public boolean hasDocumentsLeftFromDocIndex() {
	switch (status) {
	case NOT_STARTED:
	case INITIATED:
	case ONGOING:
	    return true;
	case TERMINATED:
	default:
	    return false;
	}
    }

    public CategorisationStatus getCategorisationStatus() {
	switch (status) {
	case NOT_STARTED:
	case INITIATED:
	    return new CategorisationStatus(0);
	case ONGOING:
	    int progress = 100 * this.lastElementRetrieved.doc / this.totalNbOfDocs;
	    return new CategorisationStatus(progress);
	case TERMINATED:
	    return new CategorisationStatus(100);
	default:
	    return null;
	}
    }

    public Integer getCurrentDocIndex() {
	if (lastElementRetrieved != null) {
	    return lastElementRetrieved.doc;
	}
	return null;
    }

    /**
     * This method must be synchronized because lastElementRetrieved must be
     * updated before the next bunch of documents can be processed
     */
    public synchronized String[] getNextDocumentsToCategorise() {
	switch (status) {
	case INITIATED:
	case ONGOING:
	    status = Status.ONGOING;
	    BrowseAllDocsResponse browseAllDocs = iaViewService.browseAllDocs(this.lastElementRetrieved,
		    NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME);

	    this.lastElementRetrieved = browseAllDocs.getLastScoreDoc();

	    List<String> listOfDocReferences = browseAllDocs.getListOfDocReferences();
	    if (!CollectionUtils.isEmpty(listOfDocReferences)) {
		logger.debug(
			".getNextDocumentsToCategorise: returning new set of docs: lastScoreDoc={}, size={}, docs={}",
			this.lastElementRetrieved, NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME,
			ArrayUtils.toString(listOfDocReferences));
		return listOfDocReferences.toArray(new String[0]);
	    }
	    status = Status.TERMINATED;
	    return null;
	case NOT_STARTED:
	case TERMINATED:
	default:
	    return null;
	}

    }
}