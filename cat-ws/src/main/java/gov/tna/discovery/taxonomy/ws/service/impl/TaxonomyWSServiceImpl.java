package gov.tna.discovery.taxonomy.ws.service.impl;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;
import gov.tna.discovery.taxonomy.common.service.domain.TSetBasedCategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;
import gov.tna.discovery.taxonomy.ws.domain.TestCategoriseSingleRequest;
import gov.tna.discovery.taxonomy.ws.service.TaxonomyWSService;
import gov.tna.discovery.taxonomy.ws.service.async.AsyncTaskManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyWSServiceImpl implements TaxonomyWSService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(TaxonomyWSServiceImpl.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    IAViewRepository iaViewRepository;

    @Autowired
    AsyncTaskManager asyncExecutor;

    @Autowired
    CategoriserService categoriser;

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.service.impl.TaxonomyWSService#
     * publishUpdateOnCategory(java.lang.String, java.lang.Float)
     */
    @Override
    public void publishUpdateOnCategory(String ciaid) {
	Category category = categoryRepository.findByCiaid(ciaid);

	checkLockOnCategory(category);

	iaViewRepository.checkCategoryQueryValidity(category.getQry());

	lockPublicationOnCategory(category);

	asyncExecutor.updateTrainingSetDbAndIndex(category);
    }

    private void checkLockOnCategory(Category category) {
	if (category.getLck() == true) {
	    throw new TaxonomyException(TaxonomyErrorType.LOCKED_CATEGORY);
	}
    }

    private void lockPublicationOnCategory(Category category) {
	category.setLck(true);
	categoryRepository.save(category);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.tna.discovery.taxonomy.service.impl.TaxonomyWSService#performSearch
     * (java.lang.String, java.lang.Float, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit,
	    Integer offset) {
	return iaViewRepository.performSearch(categoryQuery, score, limit, offset);
    }

    @Override
    public List<TSetBasedCategorisationResult> testCategoriseSingle(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = getIAviewFromRequest(testCategoriseSingleRequest);

	return categoriser.testCategoriseSingle(iaView);
    }

    private InformationAssetView getIAviewFromRequest(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCONTEXTDESCRIPTION(testCategoriseSingleRequest.getContextDescription());
	iaView.setDESCRIPTION(testCategoriseSingleRequest.getDescription());
	iaView.setTITLE(testCategoriseSingleRequest.getTitle());
	iaView.setCATDOCREF(testCategoriseSingleRequest.getCatDocRef());
	iaView.setCORPBODYS(testCategoriseSingleRequest.getCorpBodys());
	iaView.setCOVERINGDATES(testCategoriseSingleRequest.getCoveringDates());
	iaView.setDOCREFERENCE(testCategoriseSingleRequest.getDocReference());
	iaView.setPERSON_FULLNAME(testCategoriseSingleRequest.getPerson_FULLNAME());
	iaView.setPLACE_NAME(testCategoriseSingleRequest.getPlace_NAME());
	iaView.setSUBJECTS(testCategoriseSingleRequest.getSubjects());
	return iaView;
    }

}
