package gov.tna.discovery.taxonomy.service.impl;

import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.TaxonomyWSService;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyWSServiceImpl implements TaxonomyWSService {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyWSServiceImpl.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    Searcher searcher;

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.service.impl.TaxonomyWSService#
     * publishUpdateOnCategory(java.lang.String, java.lang.Float)
     */
    @Override
    public void publishUpdateOnCategory(String ciaid, Float fixedLimitScore) {
	Category category = categoryRepository.findByCiaid(ciaid);

	checkLockOnCategory(category);

	// Check the category query validity: should not return any exception
	searcher.checkCategoryQueryValidity(category.getQry());

	lockPublicationOnCategory(category);

	try {
	    trainingDocumentRepository.deleteByCategory(category.getTtl());
	    trainingSetService.updateTrainingSetForCategory(category, fixedLimitScore);
	    trainingSetService.deleteAndUpdateTrainingIndexForCategory(category);
	} finally {
	    releasePublicationOnCategory(category);
	}
    }

    private void releasePublicationOnCategory(Category category) {
	category.setLck(false);
	categoryRepository.save(category);
    }

    private void lockPublicationOnCategory(Category category) {
	category.setLck(true);
	categoryRepository.save(category);
    }

    private void checkLockOnCategory(Category category) {
	if (category.getLck() == true) {
	    throw new TaxonomyException(TaxonomyErrorType.LOCKED_CATEGORY);
	}
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
	return searcher.performSearch(categoryQuery, score, limit, offset);
    }

}
