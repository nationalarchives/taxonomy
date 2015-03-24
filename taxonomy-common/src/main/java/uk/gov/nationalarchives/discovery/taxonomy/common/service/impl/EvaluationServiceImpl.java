package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryEvaluationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.EvaluationReport;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.TestDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.LegacySystemRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneTaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.EvaluationReportRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.EvaluationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final IAViewRepository iaviewRepository;

    private final TestDocumentRepository testDocumentRepository;

    private final EvaluationReportRepository evaluationReportRepository;

    private final LegacySystemRepository legacySystemService;

    private final CategoriserService categoriserService;

    @Autowired
    public EvaluationServiceImpl(CategoryRepository categoryRepository, IAViewRepository iaviewRepository,
	    TestDocumentRepository testDocumentRepository, EvaluationReportRepository evaluationReportRepository,
	    LegacySystemRepository legacySystemService, CategoriserService categoriserService) {
	super();
	this.categoryRepository = categoryRepository;
	this.iaviewRepository = iaviewRepository;
	this.testDocumentRepository = testDocumentRepository;
	this.evaluationReportRepository = evaluationReportRepository;
	this.legacySystemService = legacySystemService;
	this.categoriserService = categoriserService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * EvaluationService# createEvaluationTestDataset()
     */
    @Override
    public void createEvaluationTestDataset(Integer pMinNbOfElementsPerCat) {
	logger.info(".createEvaluationTestDataset> empty testDocument collection");
	testDocumentRepository.deleteAll();

	logger.info(".createEvaluationTestDataset> going to retrieve {} documents per category at least",
		pMinNbOfElementsPerCat);

	for (Category category : categoryRepository.findAll()) {
	    logger.info(".createEvaluationTestDataset: processing category: {}", category.getTtl());

	    Integer nbOfMatchedElementsWithLegacySystem = 0;
	    Integer page = 1;
	    while (nbOfMatchedElementsWithLegacySystem < pMinNbOfElementsPerCat) {
		Map<String, String[]> mapOfLegacyDocuments = legacySystemService.findLegacyDocumentsByCategory(
			category.getTtl(), page);
		int currentSuccessfulAttempts = 0;

		if (mapOfLegacyDocuments == null || mapOfLegacyDocuments.size() == 0) {
		    logger.warn(
			    ".createTestDataset: giving up, no results found on legacy system or no document found in IAView repo for category: {}",
			    category.getTtl());
		    break;
		}

		for (String iaid : mapOfLegacyDocuments.keySet()) {
		    TopDocs topDocs = iaviewRepository.searchIAViewIndexByFieldAndPhrase(
			    InformationAssetViewFields.DOCREFERENCE.toString(), iaid, 1);

		    if (topDocs.totalHits != 0) {
			Document doc = iaviewRepository.getDoc(topDocs.scoreDocs[0]);
			TestDocument testDocument = new TestDocument();
			testDocument = LuceneTaxonomyMapper.getTestDocumentFromLuceneDocument(doc);
			testDocument.setLegacyCategories(mapOfLegacyDocuments.get(iaid));
			testDocumentRepository.save(testDocument);
			nbOfMatchedElementsWithLegacySystem++;
			currentSuccessfulAttempts++;
		    }

		    if (nbOfMatchedElementsWithLegacySystem == pMinNbOfElementsPerCat) {
			break;
		    }
		}
		page += 1;
		if (currentSuccessfulAttempts == 0) {
		    logger.warn(
			    ".createTestDataset: giving up, no results found on legacy system or no document found in IAView repo among 15 last previous attempts for category: {}",
			    category.getTtl());
		    break;
		}
	    }

	}
	logger.info(".createEvaluationTestDataset < END");
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * EvaluationService# evaluateCategorisation()
     */
    @Override
    public void runCategorisationOnTestDataSet(Boolean matchNbOfReturnedCategories) {
	logger.info(".runCategorisationOnTestDataSet> START");
	logger.info(".runCategorisationOnTestDataSet: processing {} documents",
		String.valueOf(testDocumentRepository.count()));
	for (TestDocument testDocument : testDocumentRepository.findAll()) {
	    List<CategorisationResult> categorisationResults;
	    try {
		categorisationResults = categoriserService.testCategoriseSingle(testDocument.getDocReference());
	    } catch (TaxonomyException e) {
		logger.error(".runCategorisationOnTestDataSet: an error occured while getting categories for doc: {}",
			testDocument.getDocReference(), e);
		continue;
	    }
	    List<String> categories = new ArrayList<String>();
	    for (int i = 0; i < categorisationResults.size(); i++) {
		if (matchNbOfReturnedCategories && i >= testDocument.getLegacyCategories().length) {
		    break;
		}

		CategorisationResult categorisationResult = categorisationResults.get(i);
		categories.add(categorisationResult.getName());

	    }
	    testDocument.setCategories(categories.toArray(new String[0]));
	    testDocumentRepository.save(testDocument);
	}
	logger.info(".runCategorisationOnTestDataSet < END");
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * EvaluationService# getEvaluationReport()
     */
    @Override
    public EvaluationReport getEvaluationReport(String comments) {
	logger.info(".getEvaluationReport > START");
	Map<String, Integer> mapOfTruePositivesPerCat = new HashMap<String, Integer>();
	Map<String, Integer> mapOfFalsePositivesPerCat = new HashMap<String, Integer>();
	Map<String, Integer> mapOfFalseNegativesPerCat = new HashMap<String, Integer>();
	long numberOfDocuments = testDocumentRepository.count();
	for (TestDocument testDocument : testDocumentRepository.findAll()) {
	    List<String> categories = CollectionUtils.arrayToList(testDocument.getCategories());
	    List<String> legacyCategories = CollectionUtils.arrayToList(testDocument.getLegacyCategories());

	    for (String category : categories) {
		if (legacyCategories.contains(category)) {
		    incrementMapValueForCategory(mapOfTruePositivesPerCat, category);
		} else {
		    incrementMapValueForCategory(mapOfFalsePositivesPerCat, category);
		}
	    }

	    for (String legacyCategory : legacyCategories) {
		if (!categories.contains(legacyCategory)) {
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
		    fp != null ? fp : 0, fn != null ? fn : 0, categoryRepository.findByTtl(category) != null ? true
			    : false));
	}
	for (Category category : categoryRepository.findAll()) {
	    String categoryName = category.getTtl();
	    if (!setOfCategories.contains(categoryName)) {
		listOfEvaluationResults.add(new CategoryEvaluationResult(categoryName, true, false, false));
	    }
	}

	EvaluationReport report = new EvaluationReport(comments, listOfEvaluationResults, (int) numberOfDocuments);
	evaluationReportRepository.save(report);
	logger.info(".getEvaluationReport < END");
	return report;
    }

    private void incrementMapValueForCategory(Map<String, Integer> mapOfOccurencesPerCat, String category) {
	Integer existingCounter = mapOfOccurencesPerCat.get(category);
	mapOfOccurencesPerCat.put(category, (existingCounter != null) ? (existingCounter + 1) : 1);
    }

}
