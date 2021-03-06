/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.TaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.*;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.InMemoryIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncQueryBasedServiceTaskManager;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service("categoriserService")
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class QueryBasedCategoriserServiceImpl implements CategoriserService<CategorisationResult> {

    private static final Logger logger = LoggerFactory.getLogger(QueryBasedCategoriserServiceImpl.class);

    private final CategoryRepository categoryRepository;

    private final IAViewRepository iaViewRepository;

    private final InMemoryIAViewRepository inMemoryiaViewRepository;

    private final InformationAssetViewMongoRepository informationAssetViewMongoRepository;

    private final IAViewUpdateRepository iaViewUpdateRepository;

    private final AsyncQueryBasedServiceTaskManager asyncTaskManager;

    private final SolrTaxonomyIAViewRepository solrTaxonomyIAViewRepository;

    @Autowired
    public QueryBasedCategoriserServiceImpl(CategoryRepository categoryRepository, IAViewRepository iaViewRepository,
	    InMemoryIAViewRepository inMemoryiaViewRepository,
	    InformationAssetViewMongoRepository informationAssetViewMongoRepository,
	    IAViewUpdateRepository iaViewUpdateRepository,
	    AsyncQueryBasedServiceTaskManager asyncQueryBasedServiceTaskManager,
	    SolrTaxonomyIAViewRepository solrTaxonomyIAViewRepository) {
	super();
	this.categoryRepository = categoryRepository;
	this.iaViewRepository = iaViewRepository;
	this.inMemoryiaViewRepository = inMemoryiaViewRepository;
	this.informationAssetViewMongoRepository = informationAssetViewMongoRepository;
	this.iaViewUpdateRepository = iaViewUpdateRepository;
	this.asyncTaskManager = asyncQueryBasedServiceTaskManager;
	this.solrTaxonomyIAViewRepository = solrTaxonomyIAViewRepository;
    }

    @Override
    @Loggable
    public List<CategorisationResult> testCategoriseSingle(String docReference) {
	logger.info(".testCategoriseSingle: docreference:{} ", docReference);
	return testCategoriseSingle((iaViewRepository.searchDocByDocReference(docReference)), true, null);
    }

    /**
     * Low end API to test the categorisation of a document<br/>
     * the use of retrieveScoreForAllRelevantCategories parameter has a huge
     * impact on performances:<br/>
     * should be true when displaying the result of categorisation to a user or
     * used to run a process where performances are not critical<br/>
     * should be false when performances are critical: typically, in batch
     * processes.
     * 
     * @param iaView
     * @param retrieveScoreForAllRelevantCategories
     * @param cachedCategories
     *            on batch processes, to avoid retrieving and parsing all
     *            category queries, provide cached categories
     * @return
     */
    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView,
	    boolean retrieveScoreForAllRelevantCategories, List<CategoryWithLuceneQuery> cachedCategories) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();

	List<Category> listOfRelevantCategories;
	if (cachedCategories == null) {
	    listOfRelevantCategories = inMemoryiaViewRepository.findRelevantCategoriesForDocument(iaView,
		    categoryRepository.findAll());
	} else {
	    listOfRelevantCategories = inMemoryiaViewRepository.findRelevantCategoriesForDocument(iaView,
		    cachedCategories);
	}

	if (retrieveScoreForAllRelevantCategories) {
	    listOfCategoryResults = runCategorisationWithFSDirectory(iaView, listOfRelevantCategories);
	    sortCategorisationResultsByScoreDesc(listOfCategoryResults);
	} else {
	    logger.debug(".testCategoriseSingle: runCategorisationWithFSDirectory only on categories with threshold");
	    List<CategorisationResult> generatedResults = getListOfGeneratedResultsForCategoriesWithoutThreshold(listOfRelevantCategories);
	    listOfCategoryResults.addAll(generatedResults);

	    List<Category> listOfMatchingCategoriesWithThreshold = getListOfMatchingCategoriesWithThreshold(listOfRelevantCategories);
	    List<CategorisationResult> listOfResultsForCategoriesWithThreshold = runCategorisationWithFSDirectory(
		    iaView, listOfMatchingCategoriesWithThreshold);
	    listOfCategoryResults.addAll(listOfResultsForCategoriesWithThreshold);
	}

	return listOfCategoryResults;
    }

    private List<Category> getListOfMatchingCategoriesWithThreshold(List<Category> listOfRelevantCategories) {
	List<Category> listOfMatchingCategoriesWithThreshold = new ArrayList<Category>();
	for (Category category : listOfRelevantCategories) {
	    if (category.getSc() != 0d) {
		listOfMatchingCategoriesWithThreshold.add(category);
	    }
	}
	return listOfMatchingCategoriesWithThreshold;
    }

    private List<CategorisationResult> getListOfGeneratedResultsForCategoriesWithoutThreshold(
	    List<Category> listOfRelevantCategories) {
	List<CategorisationResult> listOfGeneratedResultsForCategoriesWithoutThreshold = new ArrayList<CategorisationResult>();
	for (Category category : listOfRelevantCategories) {
	    if (category.getSc() == 0d) {
		CategorisationResult generatedResultForCategoryWithoutThreshold = new CategorisationResult(
			category.getTtl(), category.getCiaid(), null);
		listOfGeneratedResultsForCategoriesWithoutThreshold.add(generatedResultForCategoryWithoutThreshold);
	    }
	}
	return listOfGeneratedResultsForCategoriesWithoutThreshold;
    }

    @Override
    @Loggable
    public List<CategorisationResult> categoriseSingle(String docReference) {
	logger.info(".categoriseSingle: docreference:{} ", docReference);
	return categoriseSingle((iaViewRepository.searchDocByDocReference(docReference)));
    }

    @Override
    public List<CategorisationResult> categoriseSingle(String docReference,
	    List<CategoryWithLuceneQuery> cachedCategories) {
	logger.info(".categoriseSingle: docreference:{} ", docReference);
	return categoriseSingle((iaViewRepository.searchDocByDocReference(docReference)), cachedCategories);
    }

    public List<CategorisationResult> categoriseSingle(InformationAssetView iaView) {
	return categoriseSingle(iaView, null);
    }

    public List<CategorisationResult> categoriseSingle(InformationAssetView iaView,
	    List<CategoryWithLuceneQuery> cachedCategories) {
	List<CategorisationResult> listOfCategorisationResults = testCategoriseSingle(iaView, false, cachedCategories);

	List<CategoryLight> categories = getListOfCategoryLightFromListOfCatResult(listOfCategorisationResults);

	Date creationDate = Calendar.getInstance().getTime();

	saveRecordInMongoIAViewTable(iaView, categories, creationDate);

	saveNewRecordInIAViewUpdateTable(iaView, categories, creationDate);

	return listOfCategorisationResults;
    }

    private void saveNewRecordInIAViewUpdateTable(InformationAssetView iaView, List<CategoryLight> categories,
	    Date creationDate) {
	IAViewUpdate iaViewUpdateFromLuceneIAView = TaxonomyMapper
		.getIAViewUpdateFromLuceneIAView(iaView, creationDate);
	iaViewUpdateFromLuceneIAView.setCategories(categories);
	iaViewUpdateRepository.save(iaViewUpdateFromLuceneIAView);
    }

    private void saveRecordInMongoIAViewTable(InformationAssetView iaView, List<CategoryLight> categories,
	    Date creationDate) {
	MongoInformationAssetView mongoIAViewFromLuceneIAView = TaxonomyMapper.getMongoIAViewFromLuceneIAView(iaView,
		creationDate);
	mongoIAViewFromLuceneIAView.setCategories(categories);
	informationAssetViewMongoRepository.save(mongoIAViewFromLuceneIAView);
    }

    private List<CategoryLight> getListOfCategoryLightFromListOfCatResult(
	    List<CategorisationResult> listOfCategorisationResults) {
	List<CategoryLight> categories = new ArrayList<CategoryLight>();
	for (CategorisationResult categorisationResult : listOfCategorisationResults) {
	    categories.add(new CategoryLight(categorisationResult.getCiaid(), categorisationResult.getName()));
	}
	return categories;
    }

    private List<CategorisationResult> runCategorisationWithFSDirectory(InformationAssetView iaView,
	    List<Category> listOfRelevantCategories) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();
	List<Future<CategorisationResult>> listOfFutureCategoryResults = new ArrayList<Future<CategorisationResult>>();

	// TODO PERF cache filter on current document
	// Filter filter = new CachingWrapperFilter(new QueryWrapperFilter(new
	// TermQuery(new Term(
	// InformationAssetViewFields.DOCREFERENCE.toString(),
	// iaView.getDOCREFERENCE()))));
	Query filter = new TermQuery(new Term(InformationAssetViewFields.DOCREFERENCE.toString(),
		iaView.getDOCREFERENCE()));
	for (Category category : listOfRelevantCategories) {
	    listOfFutureCategoryResults.add(asyncTaskManager.runUnitFSCategoryQuery(filter, category));
	}

	for (Future<CategorisationResult> futureCatResult : listOfFutureCategoryResults) {
	    try {
		CategorisationResult categorisationResult = futureCatResult.get();
		if (categorisationResult != null) {
		    listOfCategoryResults.add(categorisationResult);
		}
	    } catch (InterruptedException | ExecutionException e) {
		logger.error(
			".runCategorisationWithFSDirectory: an exception occured while retreiving the categorisation result: , exception: {}",
			futureCatResult.toString(), e);
	    }
	}

	return listOfCategoryResults;
    }

    private void sortCategorisationResultsByScoreDesc(List<CategorisationResult> categorisationResults) {
	// Sort results by Score in descending Order
	Collections.sort(categorisationResults, new Comparator<CategorisationResult>() {
	    public int compare(CategorisationResult a, CategorisationResult b) {
		return b.getScore().compareTo(a.getScore());
	    }
	});
    }

    @Override
    public List<IAViewUpdate> getNewCategorisedDocumentsAfterDocumentAndUpToNSecondsInPast(
	    IAViewUpdate afterIAViewUpdate, int nbOfSecondsInPast, int limit) {
	Date nowMinusNseconds = getDateWithNSecondsInPast(nbOfSecondsInPast);

	List<IAViewUpdate> listOfIAViewUpdatesToProcess = iaViewUpdateRepository
		.findDocumentsCreatedAfterDocumentAndCreatedBeforeDate(afterIAViewUpdate, nowMinusNseconds, limit);

	return listOfIAViewUpdatesToProcess;
    }

    @Override
    public List<IAViewUpdate> getNewCategorisedDocumentsFromDateToNSecondsInPast(Date date, int nbOfSecondsInPast,
	    int limit) {
	Date pastDate = getDateWithNSecondsInPast(nbOfSecondsInPast);

	List<IAViewUpdate> listOfIAViewUpdatesToProcess = iaViewUpdateRepository
		.findDocumentsCreatedFromDateAndCreatedBeforeDate(date, pastDate, limit);

	return listOfIAViewUpdatesToProcess;
    }

    private Date getDateWithNSecondsInPast(int nbOfSeconds) {
	Calendar instance = Calendar.getInstance();
	instance.add(Calendar.SECOND, -nbOfSeconds);
	Date nowMinusNseconds = instance.getTime();
	return nowMinusNseconds;
    }

    @Override
    public IAViewUpdate findLastIAViewUpdate() {
	return iaViewUpdateRepository.findLastIAViewUpdate();
    }

    @Override
    public void refreshTaxonomyIndex() {
	solrTaxonomyIAViewRepository.commit();
	iaViewRepository.refreshIndexUsedForCategorisation();
    }

}
