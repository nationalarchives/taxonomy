package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneHelperTools;
import gov.tna.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextRefAnalyser;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
    private Analyzer iaViewSearchAnalyser;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private WordDelimiterFilterFactory wordDelimiterFilterFactory;

    @Override
    public void testCategoriseIAViewSolrIndex() throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView) {
	logger.info(".testCategoriseSingle: catdocref:{}, docreference:{} ", iaView.getCATDOCREF(),
		iaView.getDOCREFERENCE());
	List<CategorisationResult> listOfCategoryResults = new ArrayList<CategorisationResult>();
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

		    if (topDocs.totalHits != 0 && topDocs.scoreDocs[0].score > category.getSc()) {
			listOfCategoryResults.add(new CategorisationResult(category.getTtl(),
				topDocs.scoreDocs[0].score));
			logger.debug(".testCategoriseSingle: found category {} with score {}", category.getTtl(),
				topDocs.scoreDocs[0].score);
		    }
		} catch (TaxonomyException e) {
		    logger.debug(
			    ".testCategoriseSingle: an exception occured while parsing category query for category: {}, title: ",
			    category.getTtl(), e.getMessage());
		}
	    }

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(searcherManager, searcher);
	}

	sortCategorisationResultsByScoreDesc(listOfCategoryResults);
	return listOfCategoryResults;
    }

    @Override
    public List<CategorisationResult> testCategoriseSingle(String docReference) {
	return testCategoriseSingle(LuceneTaxonomyMapper.getIAViewFromLuceneDocument(iaViewRepository
		.searchDocByDocReference(docReference)));

    }

    private RAMDirectory createRamDirectoryForDocument(InformationAssetView iaView) throws IOException {
	RAMDirectory ramDirectory = new RAMDirectory();

	// Make an writer to create the index

	PerFieldAnalyzerWrapper iaViewIndexAnalyser = createIAViewIndexAnalyser();

	IndexWriter writer = new IndexWriter(ramDirectory, new IndexWriterConfig(Version.valueOf(luceneVersion),
		iaViewIndexAnalyser));

	// Add some Document objects containing quotes
	writer.addDocument(getLuceneDocumentFromIaVIew(iaView));

	// Optimize and close the writer to finish building the index
	writer.close();
	return ramDirectory;

    }

    private PerFieldAnalyzerWrapper createIAViewIndexAnalyser() {
	Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
	IAViewTextRefAnalyser textRefAnalyser = new IAViewTextRefAnalyser(Version.valueOf(luceneVersion),
		this.wordDelimiterFilterFactory);
	textRefAnalyser.setPositionIncrementGap(100);
	analyzerPerField.put("CATDOCREF", textRefAnalyser);
	PerFieldAnalyzerWrapper iaViewIndexAnalyser = new PerFieldAnalyzerWrapper(this.iaViewSearchAnalyser,
		analyzerPerField);
	return iaViewIndexAnalyser;
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

	doc.add(new TextField(InformationAssetViewFields.texttax.toString(), iaView.getDESCRIPTION(), Field.Store.YES));
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

}
