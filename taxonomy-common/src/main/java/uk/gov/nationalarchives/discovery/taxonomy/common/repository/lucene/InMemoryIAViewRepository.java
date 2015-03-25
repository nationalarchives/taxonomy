package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncQueryBasedTaskManager;

@Repository
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class InMemoryIAViewRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryIAViewRepository.class);

    private final Analyzer iaViewIndexAnalyser;

    private final LuceneHelperTools luceneHelperTools;

    private final AsyncQueryBasedTaskManager asyncTaskManager;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    public InMemoryIAViewRepository(Analyzer iaViewIndexAnalyser, LuceneHelperTools luceneHelperTools,
	    AsyncQueryBasedTaskManager asyncTaskManager) {
	super();
	this.iaViewIndexAnalyser = iaViewIndexAnalyser;
	this.luceneHelperTools = luceneHelperTools;
	this.asyncTaskManager = asyncTaskManager;
    }

    public List<Category> findRelevantCategoriesForDocument(InformationAssetView iaView,
	    Iterable<Category> categoriesToCheck) {

	List<CategoryWithLuceneQuery> categoriesWithQuery = getCategoriesWithParsedQueries(categoriesToCheck);

	return findRelevantCategoriesForDocument(iaView, categoriesWithQuery);
    }

    public List<Category> findRelevantCategoriesForDocument(InformationAssetView iaView,
	    List<CategoryWithLuceneQuery> categoriesWithQuery) {

	List<Category> listOfRelevantCategories = new ArrayList<Category>();

	SearcherManager searcherManager = null;
	IndexSearcher searcher = null;
	RAMDirectory ramDirectory = null;
	try {
	    ramDirectory = createRamDirectoryForDocument(iaView);
	    searcherManager = new SearcherManager(ramDirectory, null);
	    searcher = searcherManager.acquire();

	    List<Future<CategoryWithLuceneQuery>> listOfFutureFoundCategories = new ArrayList<Future<CategoryWithLuceneQuery>>();
	    for (CategoryWithLuceneQuery category : categoriesWithQuery) {
		Future<CategoryWithLuceneQuery> futureSearchResults = asyncTaskManager.runUnitInMemoryCategoryQuery(
			searcher, category);
		listOfFutureFoundCategories.add(futureSearchResults);
	    }

	    for (Future<CategoryWithLuceneQuery> futureFoundCategory : listOfFutureFoundCategories) {
		Category category;
		try {
		    category = futureFoundCategory.get();
		    if (category != null) {
			listOfRelevantCategories.add(category);
		    }
		} catch (InterruptedException | ExecutionException e) {
		    logger.error(
			    ".findRelevantCategories: an exception occured while retrieving the categorisation result: , exception: {}",
			    futureFoundCategory.toString(), e);
		}
	    }
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(searcherManager, searcher);
	    LuceneHelperTools.closeCloseableObjectQuietly(ramDirectory);
	}
	return listOfRelevantCategories;

    }

    private List<CategoryWithLuceneQuery> getCategoriesWithParsedQueries(Iterable<Category> categoriesToCheck) {
	List<CategoryWithLuceneQuery> categoriesWithQuery = new ArrayList<CategoryWithLuceneQuery>();
	for (Category category : categoriesToCheck) {
	    CategoryWithLuceneQuery cachedCategory = new CategoryWithLuceneQuery(category,
		    luceneHelperTools.buildSearchQuery(category.getQry()));
	    categoriesWithQuery.add(cachedCategory);
	}
	return categoriesWithQuery;
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

    /**
     * Create a lucene document from an iaView object
     * 
     * @param iaView
     * @throws IOException
     */
    public Document getLuceneDocumentFromIaVIew(InformationAssetView iaView) throws IOException {

	Document document = new Document();
	List<Field> listOfFields = new ArrayList<Field>();

	listOfFields.addAll(getListOfUnmodifiedFieldsFromIAView(iaView));

	listOfFields.addAll(getCopyIAViewFieldsToTaxonomyField(iaView, InformationAssetViewFields.textcaspunc));
	listOfFields.addAll(getCopyIAViewFieldsToTaxonomyField(iaView, InformationAssetViewFields.textcasnopunc));
	listOfFields.addAll(getCopyIAViewFieldsToTaxonomyField(iaView, InformationAssetViewFields.textnocasnopunc));

	addFieldsToLuceneDocument(document, listOfFields);

	return document;
    }

    private void addFieldsToLuceneDocument(Document document, List<Field> listOfFields) {
	for (Field field : listOfFields) {
	    document.add(field);
	}
    }

    private List<Field> getListOfUnmodifiedFieldsFromIAView(InformationAssetView iaView) {
	List<Field> listOfUnmodifiedFields = new ArrayList<Field>();
	if (iaView.getCATDOCREF() != null) {
	    listOfUnmodifiedFields.add(new TextField(InformationAssetViewFields.CATDOCREF.toString(), iaView
		    .getCATDOCREF(), Field.Store.NO));
	}
	if (iaView.getDESCRIPTION() != null) {
	    listOfUnmodifiedFields.add(new TextField(InformationAssetViewFields.DESCRIPTION.toString(), iaView
		    .getDESCRIPTION(), Field.Store.NO));
	}
	if (iaView.getTITLE() != null) {
	    listOfUnmodifiedFields.add(new TextField(InformationAssetViewFields.TITLE.toString(), iaView.getTITLE(),
		    Field.Store.NO));
	}
	if (iaView.getSOURCE() != null) {
	    listOfUnmodifiedFields.add(new IntField(InformationAssetViewFields.SOURCE.toString(), Integer
		    .parseInt(iaView.getSOURCE()), Field.Store.NO));
	}
	return listOfUnmodifiedFields;
    }

    private List<Field> getCopyIAViewFieldsToTaxonomyField(InformationAssetView iaView,
	    InformationAssetViewFields texttax) {
	List<Field> listOfFields = new ArrayList<Field>();

	listOfFields.add(new TextField(texttax.toString(), iaView.getDESCRIPTION(), Field.Store.NO));
	if (!StringUtils.isEmpty(iaView.getTITLE())) {
	    listOfFields.add(new TextField(texttax.toString(), iaView.getTITLE(), Field.Store.NO));
	}
	if (!StringUtils.isEmpty(iaView.getCONTEXTDESCRIPTION())) {
	    listOfFields.add(new TextField(texttax.toString(), iaView.getCONTEXTDESCRIPTION(), Field.Store.NO));
	}
	if (iaView.getCORPBODYS() != null) {
	    for (String corpBody : iaView.getCORPBODYS()) {
		listOfFields.add(new TextField(texttax.toString(), corpBody, Field.Store.NO));
	    }
	}
	if (iaView.getSUBJECTS() != null) {
	    for (String subject : iaView.getSUBJECTS()) {
		listOfFields.add(new TextField(texttax.toString(), subject, Field.Store.NO));
	    }
	}

	if (iaView.getPERSON_FULLNAME() != null) {
	    for (String person : iaView.getPERSON_FULLNAME()) {
		listOfFields.add(new TextField(texttax.toString(), person, Field.Store.NO));
	    }
	}
	if (iaView.getPLACE_NAME() != null) {
	    for (String place : iaView.getPLACE_NAME()) {
		listOfFields.add(new TextField(texttax.toString(), place, Field.Store.NO));
	    }
	}
	if (iaView.getCATDOCREF() != null) {
	    listOfFields.add(new TextField(texttax.toString(), iaView.getCATDOCREF(), Field.Store.NO));
	}
	return listOfFields;
    }

}
