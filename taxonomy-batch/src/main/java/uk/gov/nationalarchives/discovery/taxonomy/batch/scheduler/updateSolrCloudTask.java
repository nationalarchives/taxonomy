package uk.gov.nationalarchives.discovery.taxonomy.batch.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrService;

@Component
@SuppressWarnings("rawtypes")
public class updateSolrCloudTask {

    private static final Logger logger = LoggerFactory.getLogger(updateSolrCloudTask.class);

    private final CategoriserService categoriserService;
    private final UpdateSolrService updateSolrService;

    private static Date lastIAViewUpdateProcessedTime = null;

    @Autowired
    public updateSolrCloudTask(CategoriserService categoriserService, UpdateSolrService updateSolrService) {
	super();
	this.categoriserService = categoriserService;
	this.updateSolrService = updateSolrService;
    }

    @SuppressWarnings("unchecked")
    @Scheduled(fixedDelay = 1000)
    public void updateSolrCloudWithLatestUpdatesOnCategories() {
	if (lastIAViewUpdateProcessedTime == null) {
	    initBatch();
	}

	if (categoriserService.hasNewCategorisedDocumentsSinceDate(lastIAViewUpdateProcessedTime)) {
	    logger.info(".updateSolrCloudWithLatestUpdatesOnCategories: processing documents since: {}",
		    lastIAViewUpdateProcessedTime);
	    int pageNumber = 0;
	    boolean hasNext = true;
	    while (hasNext) {
		Page<IAViewUpdate> pageOfIAViewUpdatesToProcess = categoriserService
			.getPageOfNewCategorisedDocumentsSinceDate(pageNumber, lastIAViewUpdateProcessedTime);

		String[] arrayOfDocReferences = retrieveArrayOfDocRefsFromPageOfIAViewUpdates(pageOfIAViewUpdatesToProcess);

		updateSolrService.bulkUpdateCategoriesOnIAViews(arrayOfDocReferences);

		if (hasNext = pageOfIAViewUpdatesToProcess.hasNext()) {
		    pageNumber++;
		} else {
		    // TODO 1 ERROR_HANDLING if an element fails, updates to
		    // Solr are stopped.
		    // that's interesting but there might be something else to
		    // do
		    updateLastIAViewUpdateProcessedTime(pageOfIAViewUpdatesToProcess);
		}
	    }
	}

    }

    private void updateLastIAViewUpdateProcessedTime(Page<IAViewUpdate> pageOfIAViewUpdatesToProcess) {
	List<IAViewUpdate> listOfIAViewUpdates = pageOfIAViewUpdatesToProcess.getContent();
	int indexOfLastElement = listOfIAViewUpdates.size() - 1;
	IAViewUpdate iaViewUpdate = listOfIAViewUpdates.get(indexOfLastElement);
	lastIAViewUpdateProcessedTime = iaViewUpdate.getCreationDate();
    }

    private String[] retrieveArrayOfDocRefsFromPageOfIAViewUpdates(Page<IAViewUpdate> pageOfIAViewUpdatesToProcess) {
	List<String> listOfDocReferences = new ArrayList<String>();
	for (IAViewUpdate iaViewUpdate : pageOfIAViewUpdatesToProcess.getContent()) {
	    listOfDocReferences.add(iaViewUpdate.getDocReference());
	}
	return listOfDocReferences.toArray(new String[0]);
    }

    private void initBatch() {
	IAViewUpdate lastIAViewUpdate = categoriserService.findLastIAViewUpdate();
	if (lastIAViewUpdate == null) {
	    logger.warn(".initBatch: no iaViewUpdate found, the collection is currently empty.");
	    lastIAViewUpdateProcessedTime = generatePastDate();
	    return;
	}
	logger.debug(".initBatch: last document found: {}", lastIAViewUpdate);
	lastIAViewUpdateProcessedTime = lastIAViewUpdate.getCreationDate();
    }

    private Date generatePastDate() {
	Calendar instance = Calendar.getInstance();
	instance.roll(Calendar.YEAR, -1);
	return instance.getTime();
    }
}
