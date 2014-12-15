package gov.tna.discovery.taxonomy.common.service;

/**
 * Service dedicated to the evaluation of the current categorisation system against the legacy system
 * @author jcharlet
 *
 */
public interface EvaluationService {

    /**
     * Create test data set from legacy system
     */
    public void createEvaluationTestDataset();

    public void runCategorisationOnTestDataSet();

    public String getEvaluationReport();

}