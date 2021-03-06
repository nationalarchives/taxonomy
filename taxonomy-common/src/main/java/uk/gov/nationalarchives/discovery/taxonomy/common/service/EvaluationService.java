/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.EvaluationReport;

/**
 * Service dedicated to the evaluation of the current categorisation system
 * against the legacy system
 * 
 * @author jcharlet
 *
 */
public interface EvaluationService {

    /**
     * Create test data set from legacy system
     * 
     * @param pMinNbOfElementsPerCat
     *            minimum number of documents to retrieve per category
     */
    public void createEvaluationTestDataset(int pMinNbOfElementsPerCat);

    /**
     * run categorisation on Test Documents and populate their category fields
     * with found categories
     *
     * @param matchNbOfReturnedCategories
     *            if equals to yes, for each document, return at maximum the
     *            number of legacy categories. Used with training set based
     *            categorisation while there is no system to limit the number of
     *            categories returned
     */
    void runCategorisationOnTestDataSet(Boolean matchNbOfReturnedCategories);

    /**
     * Create a report on the accuracy and recall for all categories on the
     * current categorisation system.
     * 
     * @param comments
     *            the comments to save in the database for that report
     *            (configuration used, for example)
     * @return
     */
    EvaluationReport getEvaluationReport(String comments);

}