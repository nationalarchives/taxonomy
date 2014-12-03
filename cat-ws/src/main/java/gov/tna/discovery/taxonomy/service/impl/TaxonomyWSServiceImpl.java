package gov.tna.discovery.taxonomy.service.impl;

import gov.tna.discovery.taxonomy.domain.CategoryRelevancy;
import gov.tna.discovery.taxonomy.domain.TestCategoriseSingleRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.Categoriser;
import gov.tna.discovery.taxonomy.service.TaxonomyWSService;
import gov.tna.discovery.taxonomy.service.TrainingSetService;
import gov.tna.discovery.taxonomy.service.async.AsyncTaskManager;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    Categoriser categoriser;

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
    public List<InformationAssetView> performSearch(String categoryQuery, Float score, Integer limit, Integer offset) {
	return iaViewRepository.performSearch(categoryQuery, score, limit, offset);
    }

    @Override
    public List<CategoryRelevancy> testCategoriseSingle(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = getIAviewFromRequest(testCategoriseSingleRequest);

	Map<String, Float> mapOfCategoriesAndScores = categoriser.testCategoriseSingle(iaView);

	List<CategoryRelevancy> categoryRelevancies = getListOfCatRelevancyFromMapOfCatAndScore(mapOfCategoriesAndScores);
	return categoryRelevancies;
    }

    private List<CategoryRelevancy> getListOfCatRelevancyFromMapOfCatAndScore(
	    Map<String, Float> mapOfCategoriesAndScores) {
	List<CategoryRelevancy> categoryRelevancies = new ArrayList<CategoryRelevancy>();
	for (Entry<String, Float> element : mapOfCategoriesAndScores.entrySet()) {
	    categoryRelevancies.add(new CategoryRelevancy(element.getKey(), element.getValue()));
	}
	return categoryRelevancies;
    }

    private InformationAssetView getIAviewFromRequest(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCONTEXTDESCRIPTION(testCategoriseSingleRequest.getContextDescription());
	iaView.setDESCRIPTION(testCategoriseSingleRequest.getDescription());
	iaView.setTITLE(testCategoriseSingleRequest.getTitle());
	return iaView;
    }

}
