package gov.tna.discovery.taxonomy.common.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.LuceneConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
public class TrainingSetRepositoryTest {

    @Autowired
    private SearcherManager trainingSetSearcherManager;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private LuceneTestDataSet luceneTestDataSet;

    @Test
    public void testRetrieveLatestWrittenDocument() throws IOException {
	try {
	    checkNumberOfDocumentsIs0ForTestCategory();
	    luceneTestDataSet.updateTrainingSetForTestCategory();
	    checkNumberOfDocumentsIncrementedForTestCategory();
	} finally {
	    luceneTestDataSet.deleteTrainingSetForTestCategory();
	}
    }

    private void checkNumberOfDocumentsIs0ForTestCategory() throws IOException {
	IndexSearcher searcher = trainingSetSearcherManager.acquire();
	Query query = new TermQuery(new Term(InformationAssetViewFields.CATEGORY.toString(),
		LuceneTestDataSet.TEST_CATEGORY));
	TopDocs search = searcher.search(query, 1);
	assertThat(search.totalHits, is(equalTo(0)));
	LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);

    }

    private void checkNumberOfDocumentsIncrementedForTestCategory() throws IOException {
	Boolean wasRefreshed = trainingSetSearcherManager.maybeRefresh();
	assertThat(wasRefreshed, is(true));
	IndexSearcher searcher = trainingSetSearcherManager.acquire();
	Query query = new TermQuery(new Term(InformationAssetViewFields.CATEGORY.toString(),
		LuceneTestDataSet.TEST_CATEGORY));
	TopDocs search = searcher.search(query, 1);
	assertThat(search.totalHits, is(equalTo(1)));
	LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);
    }

}
