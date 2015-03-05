package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

@Repository
public class InMemoryIAViewRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryIAViewRepository.class);

    private final Analyzer iaViewIndexAnalyser;

    private final LuceneHelperTools luceneHelperTools;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    public InMemoryIAViewRepository(Analyzer iaViewIndexAnalyser, LuceneHelperTools luceneHelperTools) {
	super();
	this.iaViewIndexAnalyser = iaViewIndexAnalyser;
	this.luceneHelperTools = luceneHelperTools;
    }

    public List<Category> findRelevantCategoriesForDocument(InformationAssetView iaView,
	    Iterable<Category> categoriesToCheck) {
	List<Category> listOfRelevantCategories = new ArrayList<Category>();

	SearcherManager searcherManager = null;
	IndexSearcher searcher = null;
	try {
	    RAMDirectory ramDirectory = createRamDirectoryForDocument(iaView);
	    searcherManager = new SearcherManager(ramDirectory, null);

	    searcher = searcherManager.acquire();
	    for (Category category : categoriesToCheck) {
		String queryString = category.getQry();
		try {
		    Query query = luceneHelperTools.buildSearchQuery(queryString);
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
		    .valueOf(iaView.getSOURCE()), Field.Store.NO));
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
