package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryLight;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.mapper.TaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.InMemoryIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneTaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncQueryBasedTaskManager;

@Service("categoriserService")
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class QueryBasedCategoriserServiceImpl implements CategoriserService<CategorisationResult> {

    private static final Logger logger = LoggerFactory.getLogger(QueryBasedCategoriserServiceImpl.class);

    // TODO 6 put all autowired fields in constructors
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private InMemoryIAViewRepository inMemoryiaViewRepository;

    @Autowired
    private InformationAssetViewMongoRepository iaViewMongoRepository;

    @Autowired
    private IAViewUpdateRepository iaViewUpdateRepository;

    @Autowired
    private AsyncQueryBasedTaskManager asyncTaskManager;

    @Autowired
    private SolrTaxonomyIAViewRepository solrTaxonomyIAViewRepository;

    private static final int PAGE_SIZE = 10;

    @Override
    @Loggable
    public List<CategorisationResult> testCategoriseSingle(String docReference) {
	logger.info(".testCategoriseSingle: docreference:{} ", docReference);
	return testCategoriseSingle(LuceneTaxonomyMapper.getIAViewFromLuceneDocument(iaViewRepository
		.searchDocByDocReference(docReference)));
    }

    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();

	List<Category> listOfRelevantCategories = inMemoryiaViewRepository.findRelevantCategoriesForDocument(iaView,
		categoryRepository.findAll());

	listOfCategoryResults = runCategorisationWithFSDirectory(iaView, listOfRelevantCategories);

	sortCategorisationResultsByScoreDesc(listOfCategoryResults);
	return listOfCategoryResults;
    }

    @Override
    @Loggable
    public List<CategorisationResult> categoriseSingle(String docReference) {
	logger.info(".categoriseSingle: docreference:{} ", docReference);
	return categoriseSingle(LuceneTaxonomyMapper.getIAViewFromLuceneDocument(iaViewRepository
		.searchDocByDocReference(docReference)));
    }

    public List<CategorisationResult> categoriseSingle(InformationAssetView iaView) {
	List<CategorisationResult> listOfCategorisationResults = testCategoriseSingle(iaView);

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
	iaViewMongoRepository.save(mongoIAViewFromLuceneIAView);
    }

    private List<CategoryLight> getListOfCategoryLightFromListOfCatResult(
	    List<CategorisationResult> listOfCategorisationResults) {
	List<CategoryLight> categories = new ArrayList<CategoryLight>();
	for (CategorisationResult categorisationResult : listOfCategorisationResults) {
	    categories.add(new CategoryLight(categorisationResult.getCiaid(), categorisationResult.getName()));
	}
	return categories;
    }

    // TODO 4 manage timeout? on the search to lucene and NOT on the task:
    // impossible anyway to interrupt it this way
    private List<CategorisationResult> runCategorisationWithFSDirectory(InformationAssetView iaView,
	    List<Category> listOfRelevantCategories) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();
	List<Future<CategorisationResult>> listOfFutureCategoryResults = new ArrayList<Future<CategorisationResult>>();

	// TODO 2 PERF cache filter on current document
	// Filter filter = new CachingWrapperFilter(new QueryWrapperFilter(new
	// TermQuery(new Term(
	// InformationAssetViewFields.DOCREFERENCE.toString(),
	// iaView.getDOCREFERENCE()))));
	Filter filter = new TermFilter(new Term(InformationAssetViewFields.DOCREFERENCE.toString(),
		iaView.getDOCREFERENCE()));
	for (Category category : listOfRelevantCategories) {
	    listOfFutureCategoryResults.add(asyncTaskManager.runUnitCategoryQuery(filter, category));
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

    /**
     * set repository for testing purpose
     * 
     * @param iaViewMongoRepository
     */
    public void setIaViewMongoRepository(InformationAssetViewMongoRepository iaViewMongoRepository) {
	this.iaViewMongoRepository = iaViewMongoRepository;
    }

    /**
     * set repository for testing purpose
     * 
     * @param iaViewUpdateRepository
     */
    public void setIaViewUpdateRepository(IAViewUpdateRepository iaViewUpdateRepository) {
	this.iaViewUpdateRepository = iaViewUpdateRepository;
    }

    @Override
    public boolean hasNewCategorisedDocumentsSinceDate(Date date) {
	PageRequest pageRequest = new PageRequest(0, 1);
	Page<IAViewUpdate> pageOfIAViewUpdatesToProcess = iaViewUpdateRepository.findByCreationDateGreaterThan(date,
		pageRequest);
	return pageOfIAViewUpdatesToProcess.hasContent();
    }

    @Override
    public Page<IAViewUpdate> getPageOfNewCategorisedDocumentsSinceDate(int pageNumber, Date date) {
	Sort sort = new Sort(Direction.ASC, IAViewUpdate.CREATIONDATE);
	PageRequest pageRequest = new PageRequest(0, PAGE_SIZE, sort);
	return iaViewUpdateRepository.findByCreationDateGreaterThan(date, pageRequest);
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
