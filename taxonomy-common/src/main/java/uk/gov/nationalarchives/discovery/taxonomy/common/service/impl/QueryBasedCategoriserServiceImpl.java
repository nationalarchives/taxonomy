package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.aop.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.mapper.TaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncQueryBasedTaskManager;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class QueryBasedCategoriserServiceImpl implements CategoriserService<CategorisationResult> {

    private static final Logger logger = LoggerFactory.getLogger(QueryBasedCategoriserServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private Analyzer iaViewIndexAnalyser;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private InformationAssetViewMongoRepository iaViewMongoRepository;

    @Autowired
    private IAViewUpdateRepository iaViewUpdateRepository;

    @Autowired
    private SearcherManager iaviewSearcherManager;

    @Autowired
    private AsyncQueryBasedTaskManager asyncTaskManager;

    @Override
    public void testCategoriseIAViewSolrIndex() throws IOException {
	// TODO Auto-generated method stub
	// iaViewRepository.performSearch(queryString, category.getSc(), 1, 0);

    }

    @Override
    @Loggable
    public List<CategorisationResult> testCategoriseSingle(String docReference) {
	logger.info(".testCategoriseSingle: docreference:{} ", docReference);
	return testCategoriseSingle(LuceneTaxonomyMapper.getIAViewFromLuceneDocument(iaViewRepository
		.searchDocByDocReference(docReference)));
    }

    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();

	List<Category> listOfRelevantCategories = findRelevantCategories(iaView);

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

	List<String> categories = new ArrayList<String>();
	for (CategorisationResult categorisationResult : listOfCategorisationResults) {
	    categories.add(categorisationResult.getName());
	}
	iaView.setCATEGORIES(categories.toArray(new String[] {}));

	long timestamp = Calendar.getInstance().getTime().getTime();
	iaViewMongoRepository.save(TaxonomyMapper.getMongoIAViewFromLuceneIAView(iaView, timestamp));
	iaViewUpdateRepository.save(TaxonomyMapper.getIAViewUpdateFromLuceneIAView(iaView, timestamp));
	return listOfCategorisationResults;
    }

    // TODO JCT manage timeout on the search to lucene and NOT on the task:
    // impossible anyway to interrupt it this way
    private List<CategorisationResult> runCategorisationWithFSDirectory(InformationAssetView iaView,
	    List<Category> listOfRelevantCategories) {
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();
	List<Future<CategorisationResult>> listOfFutureCategoryResults = new ArrayList<Future<CategorisationResult>>();

	// TODO JCT cache filter on current document
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
			".runCategorisationWithFSDirectory: an exception occured while retreiving the categorisation result: {}, exception: {}",
			futureCatResult.toString(), e.getMessage());
	    }
	}

	return listOfCategoryResults;
    }

    private List<Category> findRelevantCategories(InformationAssetView iaView) {
	List<Category> listOfRelevantCategories = new ArrayList<Category>();

	SearcherManager searcherManager = null;
	IndexSearcher searcher = null;
	try {
	    RAMDirectory ramDirectory = createRamDirectoryForDocument(iaView);
	    searcherManager = new SearcherManager(ramDirectory, null);

	    searcher = searcherManager.acquire();
	    for (Category category : categoryRepository.findAll()) {
		String queryString = category.getQry();
		try {
		    Query query = iaViewRepository.buildSearchQuery(queryString);
		    TopDocs topDocs = searcher.search(query, 1);

		    if (topDocs.totalHits != 0) {
			listOfRelevantCategories.add(category);
			logger.debug(".findRelevantCategories: found category {}", category.getTtl());
		    }
		} catch (TaxonomyException e) {
		    logger.debug(
			    ".findRelevantCategories: an exception occured while parsing category query for category: {}, title: ",
			    category.getTtl(), e.getMessage());
		}
	    }
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(searcherManager, searcher);
	}
	return listOfRelevantCategories;

    }

    private RAMDirectory createRamDirectoryForDocument(InformationAssetView iaView) throws IOException {
	RAMDirectory ramDirectory = new RAMDirectory();

	// Make an writer to create the index

	IndexWriter writer;
	try {
	    writer = new IndexWriter(ramDirectory, new IndexWriterConfig(Version.parseLeniently(luceneVersion),
		    this.iaViewIndexAnalyser));
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, e);
	}

	// Add some Document objects containing quotes
	writer.addDocument(getLuceneDocumentFromIaVIew(iaView));

	// Optimize and close the writer to finish building the index
	writer.close();
	return ramDirectory;

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
     * Create a lucene document from an iaView object and add it to the
     * TrainingIndex index
     * 
     * @param iaView
     * @throws IOException
     */
    public Document getLuceneDocumentFromIaVIew(InformationAssetView iaView) throws IOException {

	Document doc = new Document();

	doc.add(new TextField(InformationAssetViewFields.texttax.toString(), iaView.getDESCRIPTION(), Field.Store.NO));
	if (!StringUtils.isEmpty(iaView.getTITLE())) {
	    doc.add(new TextField(InformationAssetViewFields.texttax.toString(), iaView.getTITLE(), Field.Store.NO));
	}
	if (!StringUtils.isEmpty(iaView.getCONTEXTDESCRIPTION())) {
	    doc.add(new TextField(InformationAssetViewFields.texttax.toString(), iaView.getCONTEXTDESCRIPTION(),
		    Field.Store.NO));
	}
	if (iaView.getCORPBODYS() != null) {
	    for (String corpBody : iaView.getCORPBODYS()) {
		doc.add(new TextField(InformationAssetViewFields.texttax.toString(), corpBody, Field.Store.NO));
	    }
	}
	if (iaView.getSUBJECTS() != null) {
	    for (String subject : iaView.getSUBJECTS()) {
		doc.add(new TextField(InformationAssetViewFields.texttax.toString(), subject, Field.Store.NO));
	    }
	}

	if (iaView.getPERSON_FULLNAME() != null) {
	    for (String person : iaView.getPERSON_FULLNAME()) {
		doc.add(new TextField(InformationAssetViewFields.texttax.toString(), person, Field.Store.NO));
	    }
	}
	if (iaView.getPLACE_NAME() != null) {
	    for (String place : iaView.getPLACE_NAME()) {
		doc.add(new TextField(InformationAssetViewFields.texttax.toString(), place, Field.Store.NO));
	    }
	}
	if (iaView.getCATDOCREF() != null) {
	    doc.add(new TextField(InformationAssetViewFields.texttax.toString(), iaView.getCATDOCREF(), Field.Store.NO));
	    doc.add(new TextField(InformationAssetViewFields.CATDOCREF.toString(), iaView.getCATDOCREF(),
		    Field.Store.NO));
	}
	return doc;
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

}
