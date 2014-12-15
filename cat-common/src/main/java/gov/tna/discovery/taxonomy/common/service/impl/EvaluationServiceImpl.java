package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.mapper.TaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.CategoryEvaluationResult;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.EvaluationReport;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.EvaluationReportRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.EvaluationService;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IAViewRepository iaviewRepository;

    @Autowired
    private TestDocumentRepository testDocumentRepository;

    @Autowired
    private EvaluationReportRepository evaluationReportRepository;

    @Autowired
    private LegacySystemService legacySystemService;

    @Autowired
    private CategoriserService categoriserService;

    private Integer minNbOfElementsPerCat = 10;

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#
     * createEvaluationTestDataset()
     */
    @Override
    public void createEvaluationTestDataset() {
	for (Category category : categoryRepository.findAll()) {
	    Integer nbOfMatchedElementsWithLegacySystem = 0;
	    Integer offset = 0;
	    while (nbOfMatchedElementsWithLegacySystem < minNbOfElementsPerCat) {
		PaginatedList<InformationAssetView> iaviews = iaviewRepository.performSearch(category.getQry(), null,
			10, offset);
		for (InformationAssetView iaview : iaviews.getResults()) {
		    String[] legacyCategories = legacySystemService.getLegacyCategoriesForCatDocRef(iaview
			    .getCATDOCREF());
		    if (legacyCategories != null && Arrays.asList(legacyCategories).contains(category.getTtl())) {
			TestDocument testDocument = new TestDocument();
			testDocument = TaxonomyMapper.getTestDocumentFromIAView(iaview);
			testDocument.setLegacyCategories(legacyCategories);
			testDocumentRepository.save(testDocument);
			nbOfMatchedElementsWithLegacySystem++;
		    }
		    if (nbOfMatchedElementsWithLegacySystem == minNbOfElementsPerCat) {
			break;
		    }
		}
		offset += 10;
		if (nbOfMatchedElementsWithLegacySystem == 0) {
		    logger.warn(".createTestDataset: giving up, no results found for category: {}", category.getTtl());
		    break;
		}
		if (offset >= iaviews.getNumberOfResults()) {
		    break;
		}
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#
     * evaluateCategorisation()
     */
    @Override
    public void runCategorisationOnTestDataSet() {
	for (TestDocument testDocument : testDocumentRepository.findAll()) {
	    InformationAssetView iaView = TaxonomyMapper.getIAViewFromTestDocument(testDocument);
	    List<CategorisationResult> categorisationResults = categoriserService.testCategoriseSingle(iaView);
	    String[] categories = new String[testDocument.getLegacyCategories().length];
	    for (int i = 0; i < categorisationResults.size(); i++) {
		if (i >= testDocument.getLegacyCategories().length) {
		    break;
		}

		CategorisationResult categorisationResult = categorisationResults.get(i);
		categories[i] = categorisationResult.getName();

	    }
	    testDocument.setCategories(categories);
	    testDocumentRepository.save(testDocument);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#
     * getEvaluationReport()
     */
    @SuppressWarnings("unchecked")
    @Override
    public EvaluationReport getEvaluationReport(String comments) {
	Map<String, Integer> mapOfTruePositivesPerCat = new HashMap<String, Integer>();
	Map<String, Integer> mapOfFalsePositivesPerCat = new HashMap<String, Integer>();
	Map<String, Integer> mapOfFalseNegativesPerCat = new HashMap<String, Integer>();
	long numberOfDocuments = testDocumentRepository.count();
	for (TestDocument testDocument : testDocumentRepository.findAll()) {
	    List<String> categories = CollectionUtils.arrayToList(testDocument.getCategories());
	    List<String> legacyCategories = CollectionUtils.arrayToList(testDocument.getLegacyCategories());

	    for (String category : categories) {
		if (CollectionUtils.containsInstance(legacyCategories, category)) {
		    incrementMapValueForCategory(mapOfTruePositivesPerCat, category);
		} else {
		    incrementMapValueForCategory(mapOfFalsePositivesPerCat, category);
		}
	    }

	    for (String legacyCategory : legacyCategories) {
		if (!CollectionUtils.containsInstance(categories, legacyCategory)) {
		    incrementMapValueForCategory(mapOfFalseNegativesPerCat, legacyCategory);
		}
	    }

	}

	Set<String> setOfCategories = new HashSet<String>();
	setOfCategories.addAll(mapOfTruePositivesPerCat.keySet());
	setOfCategories.addAll(mapOfFalsePositivesPerCat.keySet());
	setOfCategories.addAll(mapOfFalseNegativesPerCat.keySet());

	List<CategoryEvaluationResult> listOfEvaluationResults = new ArrayList<CategoryEvaluationResult>();
	for (String category : setOfCategories) {
	    Integer tp = mapOfTruePositivesPerCat.get(category);
	    Integer fp = mapOfFalsePositivesPerCat.get(category);
	    Integer fn = mapOfFalseNegativesPerCat.get(category);
	    listOfEvaluationResults.add(new CategoryEvaluationResult(category, tp != null ? tp : 0,
		    fp != null ? fp : 0, fn != null ? fn : 0));
	}

	EvaluationReport report = new EvaluationReport(comments, listOfEvaluationResults, (int) numberOfDocuments);
	evaluationReportRepository.save(report);
	return report;
    }

    private void incrementMapValueForCategory(Map<String, Integer> mapOfOccurencesPerCat, String category) {
	Integer existingCounter = mapOfOccurencesPerCat.get(category);
	mapOfOccurencesPerCat.put(category, (existingCounter != null) ? (existingCounter + 1) : 1);
    }

    public void setLegacySystemService(LegacySystemService legacySystemService) {
	this.legacySystemService = legacySystemService;
    }

    public void setIaviewRepository(IAViewRepository iaviewRepository) {
	this.iaviewRepository = iaviewRepository;
    }

    public void setTestDocumentRepository(TestDocumentRepository testDocumentRepository) {
	this.testDocumentRepository = testDocumentRepository;
    }

    public void setCategoryRepository(CategoryRepository categoryRepository) {
	this.categoryRepository = categoryRepository;
    }

    public void setCategoriserService(CategoriserService categoriserService) {
	this.categoriserService = categoriserService;
    }

    public void setEvaluationReportRepository(EvaluationReportRepository evaluationReportRepository) {
	this.evaluationReportRepository = evaluationReportRepository;
    }

}
