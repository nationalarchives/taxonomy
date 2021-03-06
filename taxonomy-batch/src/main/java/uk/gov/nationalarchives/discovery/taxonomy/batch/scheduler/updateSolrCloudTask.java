/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrCloudService;

@Component
@SuppressWarnings("rawtypes")
@ConditionalOnProperty(prefix = "batch.role.", value = "udpate-solr-cloud")
public class updateSolrCloudTask {

    private static final Logger logger = LoggerFactory.getLogger(updateSolrCloudTask.class);

    private final CategoriserService categoriserService;
    private final UpdateSolrCloudService updateSolrService;

    private IAViewUpdate lastIAViewUpdate = null;

    @Value("${batch.update-solr-cloud.bulk-update-size}")
    Integer bulkUpdateSize;

    @Value("${batch.update-solr-cloud.start-date}")
    String startDateString;

    @Value("${batch.update-solr-cloud.nb-of-seconds-in-past}")
    private int nbOfSecondsInPast;

    @Autowired
    public updateSolrCloudTask(CategoriserService categoriserService, UpdateSolrCloudService updateSolrService) {
	super();
	this.categoriserService = categoriserService;
	this.updateSolrService = updateSolrService;
    }

    @PostConstruct
    private void initBatch() {
	if (StringUtils.isEmpty(startDateString)) {
	    IAViewUpdate lastIAViewUpdate = categoriserService.findLastIAViewUpdate();
	    if (lastIAViewUpdate == null) {
		logger.warn(".initBatch: no iaViewUpdate found, the collection is currently empty.");
		lastIAViewUpdate = null;
		return;
	    }
	    logger.debug(".initBatch: last document found: {}", lastIAViewUpdate.getDocReference());
	    this.lastIAViewUpdate = new IAViewUpdate(lastIAViewUpdate);
	} else {
	    logger.info(".initBatch: updating from date (UTC time): {}.", startDateString);
	}

    }

    @SuppressWarnings("unchecked")
    @Scheduled(fixedRateString = "${batch.update-solr-cloud.rate-between-updates}")
    public void updateSolrCloudWithLatestUpdatesOnCategories() {
	List<IAViewUpdate> listOfIAViewUpdatesToProcess = getNewCategorisedDocuments();

	if (CollectionUtils.isEmpty(listOfIAViewUpdatesToProcess)) {
	    return;
	}

	updateSolrService.bulkUpdateCategoriesOnIAViews(listOfIAViewUpdatesToProcess);

	updateLastIAViewUpdateObjectId(listOfIAViewUpdatesToProcess);

    }

    private List getNewCategorisedDocuments() {
	if (lastIAViewUpdate != null) {
	    return categoriserService.getNewCategorisedDocumentsAfterDocumentAndUpToNSecondsInPast(lastIAViewUpdate,
		    nbOfSecondsInPast, bulkUpdateSize);
	} else if (!StringUtils.isEmpty(startDateString)) {
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	    df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	    Date startDate;
	    try {
		startDate = df.parse(startDateString);
		return categoriserService.getNewCategorisedDocumentsFromDateToNSecondsInPast(startDate,
			nbOfSecondsInPast, bulkUpdateSize);
	    } catch (ParseException e) {
		throw new TaxonomyException(TaxonomyErrorType.INVALID_PARAMETER,
			"the start date provided has wrong format");
	    }
	} else {
	    return categoriserService.getNewCategorisedDocumentsFromDateToNSecondsInPast(null, nbOfSecondsInPast,
		    bulkUpdateSize);
	}
    }

    private void updateLastIAViewUpdateObjectId(List<IAViewUpdate> listOfIAViewUpdates) {
	IAViewUpdate iaViewUpdate = listOfIAViewUpdates.get(listOfIAViewUpdates.size() - 1);
	lastIAViewUpdate = iaViewUpdate;
    }
}
