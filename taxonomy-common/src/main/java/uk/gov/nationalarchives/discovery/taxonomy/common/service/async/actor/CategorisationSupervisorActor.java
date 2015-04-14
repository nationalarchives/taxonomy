package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.util.CollectionUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseAllDocumentsEpic;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseDocuments;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;

@SuppressWarnings("rawtypes")
public class CategorisationSupervisorActor extends SupervisorActor {

    private final int nbOfDocsToCategoriseAtATime;

    private Integer totalNbOfDocs;
    private ScoreDoc lastElementRetrieved = null;
    private CategorisationStatusEnum status = CategorisationStatusEnum.NOT_STARTED;

    private final IAViewService iaViewService;

    private final CategoriserService categoriserService;

    public CategorisationSupervisorActor(int nbOfDocsToCategoriseAtATime, IAViewService iaViewService,
	    CategoriserService categoriserService) {
	super();
	this.nbOfDocsToCategoriseAtATime = nbOfDocsToCategoriseAtATime;
	this.iaViewService = iaViewService;
	this.categoriserService = categoriserService;
	this.logger.info("Initiated Categorisation Supervisor Actor");
    }

    private int categoriseDocsMessageNumber = 0;

    @Override
    public void startEpic(Object message) {
	if (message instanceof CategoriseAllDocumentsEpic) {
	    logger.info("START WHOLE CATEGORISATION");

	    categoriserService.refreshTaxonomyIndex();

	    this.totalNbOfDocs = iaViewService.getTotalNbOfDocs();

	    if (totalNbOfDocs == 0) {
		throw new TaxonomyException(TaxonomyErrorType.DOC_NOT_FOUND, "The index is empty");
	    }

	    status = CategorisationStatusEnum.INITIATED;

	    Integer afterDocNumber = ((CategoriseAllDocumentsEpic) message).getAfterDocNumber();
	    if (afterDocNumber != null) {
		lastElementRetrieved = new FieldDoc(afterDocNumber, Float.NaN, new Object[] { afterDocNumber });
		logger.info(".categoriseAllDocuments: categorizing {} documents from doc numbered: {}",
			this.totalNbOfDocs, afterDocNumber);
	    } else {
		lastElementRetrieved = null;
		logger.info(".categoriseAllDocuments: categorizing {} documents", this.totalNbOfDocs);
	    }

	    categoriseDocsMessageNumber = 0;
	    currentEpic = message;
	}
    }

    @Override
    public void giveWork() {
	if (currentEpic instanceof CategoriseAllDocumentsEpic) {
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

    public String[] getNextDocumentsToCategorise() {
	switch (status) {
	case INITIATED:
	case ONGOING:
	    status = CategorisationStatusEnum.ONGOING;
	    BrowseAllDocsResponse browseAllDocs = iaViewService.browseAllDocs(this.lastElementRetrieved,
		    nbOfDocsToCategoriseAtATime);

	    this.lastElementRetrieved = browseAllDocs.getLastScoreDoc();

	    List<String> listOfDocReferences = browseAllDocs.getListOfDocReferences();
	    if (!CollectionUtils.isEmpty(listOfDocReferences)) {
		logger.debug(
			".getNextDocumentsToCategorise: returning new set of docs: lastScoreDoc={}, size={}, docs={}",
			this.lastElementRetrieved, nbOfDocsToCategoriseAtATime,
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

}
