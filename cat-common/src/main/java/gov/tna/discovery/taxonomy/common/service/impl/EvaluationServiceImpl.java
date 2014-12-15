package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.EvaluationService;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private LegacySystemService legacySystemService;

    private Integer minNbOfElementsPerCat = 10;

    /* (non-Javadoc)
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#createEvaluationTestDataset()
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
			testDocument = getTestDocumentFromIAView(iaview);
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

    private TestDocument getTestDocumentFromIAView(InformationAssetView iaView) {
	TestDocument testDocument = new TestDocument();
	testDocument.setDescription(iaView.getDESCRIPTION());
	testDocument.setContextDescription(iaView.getCONTEXTDESCRIPTION());
	testDocument.setTitle(iaView.getTITLE());
	testDocument.setDocReference(iaView.getDOCREFERENCE());
	testDocument.setCatDocRef(iaView.getCATDOCREF());
	testDocument.setCorpBodys(iaView.getCORPBODYS());
	testDocument.setPersonFullName(iaView.getPERSON_FULLNAME());
	testDocument.setPlaceName(iaView.getPLACE_NAME());
	testDocument.setSubjects(iaView.getSUBJECTS());
	return testDocument;
    }

    /* (non-Javadoc)
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#evaluateCategorisation()
     */
    @Override
    public void evaluateCategorisation() {

    }

    /* (non-Javadoc)
     * @see gov.tna.discovery.taxonomy.common.service.impl.EvaluationService#getEvaluationReport()
     */
    @Override
    public String getEvaluationReport() {
	return null;
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

}
