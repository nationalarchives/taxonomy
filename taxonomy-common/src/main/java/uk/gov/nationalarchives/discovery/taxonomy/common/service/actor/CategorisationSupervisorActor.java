package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.CollectionUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import akka.actor.ActorSystem;

@SuppressWarnings("rawtypes")
@Configurable(preConstruction = true)
public class CategorisationSupervisorActor extends SupervisorActor {

    private static final int NB_OF_DOCS_TO_CATEGORISE_AT_A_TIME = 100;

    private Integer totalNbOfDocs;
    private ScoreDoc lastElementRetrieved = null;
    private CategorisationStatusEnum status = CategorisationStatusEnum.NOT_STARTED;

    @Autowired
    private IAViewService iaViewService;

    @Autowired
    private CategoriserService categoriserService;

    @Autowired
    private ActorSystem actorSystem;

    public CategorisationSupervisorActor() {
	super();
	this.logger.info("Initiated Categorisation Supervisor Actor");
    }

    private int categoriseDocsMessageNumber = 0;
    private static final String CATEGORISE_ALL = "CATEGORISE_ALL";

    @Override
    public void startEpic() {
	logger.info("START WHOLE CATEGORISATION");

	categoriserService.refreshTaxonomyIndex();

	this.totalNbOfDocs = iaViewService.getTotalNbOfDocs();

	if (totalNbOfDocs == 0) {
	    throw new TaxonomyException(TaxonomyErrorType.DOC_NOT_FOUND, "The index is empty");
	}

	logger.info(".categoriseAllDocuments: categorizing {} documents", this.totalNbOfDocs);
	status = CategorisationStatusEnum.INITIATED;
	lastElementRetrieved = null;

	categoriseDocsMessageNumber = 0;
	currentEpic = CATEGORISE_ALL;
    }

    @Override
    public void giveWork() {
	if (hasDocumentsLeftFromDocIndex()) {
	    String[] nextDocReferences = getNextDocumentsToCategorise();
	    if (nextDocReferences == null || nextDocReferences.length == 0) {
		logger.info("done with current epic");
		currentEpic = null;

		logger.info("CATEGORISATION TERMINATED ON SUPERVISOR SIDE. Wait for slave to complete their tasks",
			status);
		return;
	    }
	    categoriseDocsMessageNumber++;
	    getSender().tell(new CategoriseDocuments(nextDocReferences, categoriseDocsMessageNumber), getSelf());

	    logger.info("PROGRESS OF CATEGORISATION: {}. message number: {}, index of last doc requested: {}",
		    getCategorisationStatus(), categoriseDocsMessageNumber, getCurrentDocIndex());
	} else {
	    logger.info("done with current epic");
	    currentEpic = null;
	}
    }

    private enum CategorisationStatusEnum {
	NOT_STARTED, INITIATED, ONGOING, TERMINATED
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
	    status = CategorisationStatusEnum.ONGOING;
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
	    status = CategorisationStatusEnum.TERMINATED;
	    return null;
	case NOT_STARTED:
	case TERMINATED:
	default:
	    return null;
	}

    }

    public void setIaViewService(IAViewService iaViewService) {
	this.iaViewService = iaViewService;
    }

    public void setCategoriserService(CategoriserService categoriserService) {
	this.categoriserService = categoriserService;
    }

}
