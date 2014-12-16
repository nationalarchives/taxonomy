package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.EvaluationReport;

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
     */
    public void createEvaluationTestDataset();

    /**
     * run categorisation on Test Documents and populate their category fields
     * with found categories
     */
    public void runCategorisationOnTestDataSet();

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