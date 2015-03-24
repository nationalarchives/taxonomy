package uk.gov.nationalarchives.discovery.taxonomy.batch.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrCloudService;

@Component
@SuppressWarnings("rawtypes")
public class updateSolrCloudTask {

    private static final Logger logger = LoggerFactory.getLogger(updateSolrCloudTask.class);

    private final CategoriserService categoriserService;
    private final UpdateSolrCloudService updateSolrService;

    private static ObjectId lastIAViewUpdateObjectId = null;

    @Value("${batch.update-solr-cloud.page-size}")
    Integer pageSize;

    @Autowired
    public updateSolrCloudTask(CategoriserService categoriserService, UpdateSolrCloudService updateSolrService) {
	super();
	this.categoriserService = categoriserService;
	this.updateSolrService = updateSolrService;
    }

    @SuppressWarnings("unchecked")
    @Scheduled(fixedDelayString = "${batch.update-solr-cloud.delay-between-tasks}")
    public void updateSolrCloudWithLatestUpdatesOnCategories() {
	if (lastIAViewUpdateObjectId == null) {
	    initBatch();
	}

	if (categoriserService.hasNewCategorisedDocumentsSinceObjectId(lastIAViewUpdateObjectId)) {
	    logger.info(".updateSolrCloudWithLatestUpdatesOnCategories: processing documents since: {}",
		    lastIAViewUpdateObjectId);
	    int pageNumber = 0;
	    boolean hasNext = true;
	    while (hasNext) {
		Page<IAViewUpdate> pageOfIAViewUpdatesToProcess = categoriserService
			.getPageOfNewCategorisedDocumentsSinceObjectId(pageNumber, this.pageSize,
				lastIAViewUpdateObjectId);

		String[] arrayOfDocReferences = retrieveArrayOfDocRefsFromPageOfIAViewUpdates(pageOfIAViewUpdatesToProcess);

		updateSolrService.bulkUpdateCategoriesOnIAViews(arrayOfDocReferences);

		if (hasNext = pageOfIAViewUpdatesToProcess.hasNext()) {
		    pageNumber++;
		} else {
		    // TODO 1 ERROR_HANDLING if an element fails, updates to
		    // Solr are stopped.
		    // that's interesting but there might be something else to
		    // do
		    updateLastIAViewUpdateObjectId(pageOfIAViewUpdatesToProcess);
		}
	    }
	}

    }

    private void updateLastIAViewUpdateObjectId(Page<IAViewUpdate> pageOfIAViewUpdatesToProcess) {
	List<IAViewUpdate> listOfIAViewUpdates = pageOfIAViewUpdatesToProcess.getContent();
	int indexOfLastElement = listOfIAViewUpdates.size() - 1;
	IAViewUpdate iaViewUpdate = listOfIAViewUpdates.get(indexOfLastElement);
	lastIAViewUpdateObjectId = iaViewUpdate.getId();
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
	    lastIAViewUpdateObjectId = null;
	    return;
	}
	logger.debug(".initBatch: last document found: {}", lastIAViewUpdate);
	lastIAViewUpdateObjectId = lastIAViewUpdate.getId();
    }
}
