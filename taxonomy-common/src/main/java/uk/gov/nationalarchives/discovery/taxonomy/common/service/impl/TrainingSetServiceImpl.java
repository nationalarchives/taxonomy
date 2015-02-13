package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.TrainingSetRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncTSetBasedTaskManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "lucene.", value = "loadTSetServiceLayer")
public class TrainingSetServiceImpl implements TrainingSetService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSetServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private TrainingSetRepository trainingSetRepository;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.trainingSetCollectionPath}")
    private String trainingSetCollectionPath;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Value("${lucene.index.maxTrainingDocsPerCategory}")
    private Integer maxTrainingDocsPerCategory;

    @Autowired
    AsyncTSetBasedTaskManager asyncExecutor;

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * TrainingSetService# updateTrainingSetForCategory (java.lang.String),
     * java.lang.Float)
     */
    @Override
    public void updateTrainingSetForCategory(String categoryCiaid, Integer fixedLimitScore, Integer fixedLimitSize) {
	Category category = categoryRepository.findByCiaid(categoryCiaid);
	this.updateTrainingSetForCategory(category, null, fixedLimitSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * TrainingSetService# updateTrainingSetForCategory
     * (uk.gov.nationalarchives.
     * discovery.taxonomy.common.repository.domain.mongo.Category,
     * java.lang.Float)
     */
    @Override
    public void updateTrainingSetForCategory(Category category, Float fixedLimitScore, Integer fixedLimitSize) {

	if (fixedLimitScore != null && fixedLimitSize != null) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_PARAMETER);
	}

	PaginatedList<InformationAssetView> IAViewResults;
	try {
	    // FIXME JCT Iterate instead of taking only
	    // maxTrainingDocsPerCategory (could be 5000) elements
	    if (fixedLimitSize != null) {
		IAViewResults = iaViewRepository.performSearch(category.getQry(), null, fixedLimitSize, 0);
	    } else {
		IAViewResults = iaViewRepository.performSearch(category.getQry(),
			(fixedLimitScore != null ? fixedLimitScore : category.getSc()), maxTrainingDocsPerCategory, 0);
	    }
	    logger.info(".updateTrainingSetForCategory: Category=" + category.getTtl() + ", found "
		    + IAViewResults.size() + " result(s). Updating Mongo DB");
	    if (IAViewResults.size() > 0) {

		for (InformationAssetView iaView : IAViewResults.getResults()) {
		    TrainingDocument trainingDocument = new TrainingDocument();
		    trainingDocument.setCategory(category.getTtl());
		    trainingDocument.setDescription(iaView.getDESCRIPTION());
		    trainingDocument.setContextDescription(iaView.getCONTEXTDESCRIPTION());
		    trainingDocument.setTitle(iaView.getTITLE());
		    trainingDocument.setDocReference(iaView.getDOCREFERENCE());
		    trainingDocument.setCatDocRef(iaView.getCATDOCREF());
		    trainingDocument.setCorpBodys(iaView.getCORPBODYS());
		    trainingDocument.setPersonFullName(iaView.getPERSON_FULLNAME());
		    trainingDocument.setPlaceName(iaView.getPLACE_NAME());
		    trainingDocument.setSubjects(iaView.getSUBJECTS());
		    trainingDocumentRepository.save(trainingDocument);
		    logger.debug(trainingDocument.getCategory() + ":" + iaView.getDOCREFERENCE() + " - "
			    + trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
		}
	    }
	} catch (TaxonomyException e) {
	    // TODO 1 several errors occur while creating the training set,
	    // to investigate
	    // some queries are not valid: paul takes care of them.
	    // Some queries have wildcards and lucene doesnt accept them: to
	    // enable.
	    logger.error(".updateTrainingSetForCategory< An error occured for category: " + category.toString());
	    logger.error(".updateTrainingSetForCategory< Error message: " + e.getMessage());
	    throw e;
	}
	logger.debug(".updateTrainingSetForCategory: Process completed for category {}", category.getTtl());
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * TrainingSetService# createTrainingSet (java.lang.Float)
     */
    @Override
    public void createTrainingSet(Float fixedLimitScore, Integer fixedLimitSize) throws IOException, ParseException {
	logger.debug(".createTrainingSet : START");

	Iterator<Category> categoryIterator = categoryRepository.findAll().iterator();

	// empty collection
	trainingDocumentRepository.deleteAll();

	while (categoryIterator.hasNext()) {
	    Category category = categoryIterator.next();
	    try {
		updateTrainingSetForCategory(category, fixedLimitScore, fixedLimitSize);
	    } catch (TaxonomyException e) {
		logger.error(".createTrainingSet: error while parsing Category '{}': {}", category.getTtl(),
			e.toString());
		continue;
	    }

	}
	logger.debug(".createTrainingSet : END");
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * TrainingSetService# deleteAndUpdateTraingSetIndexForCategory
     * (uk.gov.nationalarchives
     * .discovery.taxonomy.common.repository.domain.mongo.Category)
     */
    @Override
    public void deleteAndUpdateTraingSetIndexForCategory(Category category) {
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(Version.parseLeniently(luceneVersion),
		    trainingSetAnalyser));

	    trainingSetRepository.deleteTrainingDocumentsForCategory(writer, category);

	    List<TrainingDocument> trainingDocuments = trainingDocumentRepository.findByCategory(category.getTtl());
	    logger.info(".deleteAndUpdateTraingSetIndexForCategory: indexing {} elements", trainingDocuments.size());

	    trainingSetRepository.indexTrainingDocuments(writer, trainingDocuments);
	} catch (IOException e) {
	    logger.error(".deleteAndUpdateTraingSetIndexForCategory: an exception occured {}", e.getMessage());
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} catch (java.text.ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
	logger.info(".deleteAndUpdateTraingSetIndexForCategory: operation completed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.
     * TrainingSetService# indexTrainingSet ()
     */
    @Override
    public void indexTrainingSet() {
	logger.info("index training set");
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(Version.parseLeniently(luceneVersion),
		    trainingSetAnalyser));

	    writer.deleteAll();

	    Iterator<TrainingDocument> trainingDocumentIterator = trainingDocumentRepository.findAll().iterator();

	    while (trainingDocumentIterator.hasNext()) {
		TrainingDocument trainingDocument = trainingDocumentIterator.next();
		trainingSetRepository.indexTrainingSetDocument(trainingDocument, writer);

	    }
	    writer.commit();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} catch (java.text.ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
	logger.info("index training set ended");

    }

    @Override
    public void deleteMongoTrainingDocumentByCategory(String category) {
	int numberOfRemovedElements = trainingDocumentRepository.deleteByCategory(category);
	logger.info(".deleteMongoTrainingDocumentByCategory < removed {} elements", numberOfRemovedElements);
    }

    @Override
    public void updateCategoriesScores(int minNumber, int maxNumber) {
	logger.info(".updateCategoriesScores> START");
	int maxHitsForAllCategories = getMaximumNumberOfResultsForACategory();
	logger.info(".updateCategoriesScores: maxHitsForAllCategories: {} ", maxHitsForAllCategories);

	for (Category category : categoryRepository.findAll()) {
	    PaginatedList<InformationAssetView> searchResponse;
	    try {
		searchResponse = iaViewRepository.performSearch(category.getQry(), null, 1, 0);
	    } catch (TaxonomyException e) {
		logger.error(".updateCategoriesScores: an error occured", e);
		category.setSc(0d);
		categoryRepository.save(category);
		continue;
	    }
	    Integer numberOfResults = searchResponse.getNumberOfResults();

	    if (numberOfResults == 0) {
		logger.warn(".updateCategoriesScores: category '{}' has no result", category.getTtl());
		category.setSc(0d);
		categoryRepository.save(category);
		continue;
	    }

	    int numberToIncrement = (int) getScaledNumberWithLogFunction(numberOfResults,
		    1.0d * (maxNumber - minNumber), 1.0d * maxHitsForAllCategories);

	    int lastElementToRetrieveOffset;
	    if (numberToIncrement + minNumber < numberOfResults) {
		lastElementToRetrieveOffset = numberToIncrement + minNumber;
	    } else {
		lastElementToRetrieveOffset = numberOfResults;
	    }

	    searchResponse = iaViewRepository
		    .performSearch(category.getQry(), null, 1, lastElementToRetrieveOffset - 1);

	    category.setSc(getUpperDoubleValue(1.0d * searchResponse.getResults().get(0).getScore()));
	    categoryRepository.save(category);
	    logger.info(".updateCategoriesScores : score {} was set for category {}", category.getSc(),
		    category.getTtl());
	}

	logger.info(".updateCategoriesScores> END");
    }

    /**
     * to avoid retrieving too many records on the score boundary
     * 
     * @param number
     * @return
     */
    private Double getUpperDoubleValue(Double number) {
	return Math.nextUp(number);
    }

    private int getMaximumNumberOfResultsForACategory() {
	int maxHits = 0;
	for (Category category : categoryRepository.findAll()) {

	    PaginatedList<InformationAssetView> searchResponse;
	    try {
		searchResponse = iaViewRepository.performSearch(category.getQry(), null, 1, 0);
	    } catch (TaxonomyException e) {
		logger.error(".getMaximumNumberOfResultsForACategory: an error occured", e);
		continue;
	    }
	    Integer numberOfResults = searchResponse.getNumberOfResults();
	    if (numberOfResults > maxHits) {
		maxHits = numberOfResults;
	    }
	}
	logger.info(".getMaximumNumberOfResultsForACategory < {}", maxHits);
	return maxHits;
    }

    private long getScaledNumberWithLogFunction(Integer valueToScale, Double maximumValueExpected,
	    Double maximumValueObserved) {
	// return long to round down to lower element
	return (long) (maximumValueExpected * Math.log(1.0d * (valueToScale + 1)) / Math.log(maximumValueObserved));
    }

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

}
