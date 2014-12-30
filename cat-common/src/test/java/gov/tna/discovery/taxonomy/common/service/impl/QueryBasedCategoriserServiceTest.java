package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.QueryBasedServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.lucene.TrainingSetRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.TSetBasedCategorisationResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = QueryBasedServiceConfigurationTest.class)
public class QueryBasedCategoriserServiceTest {

    @Autowired
    QueryBasedCategoriserServiceImpl categoriserService;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private TrainingSetRepository trainingSetRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

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

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    @Ignore
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("CO 847");
	iaView.setTITLE("colonial office: Africa: Original Correspondence");
	iaView.setDESCRIPTION("This series relates to British policy in Africa in general and includes a proportion of correspondence with the Foreign Office on the international implications outside Africa. The very varied subject matter includes: constitutional development, cultural questions, education, finance, game and national parks, health, labour, matters relating to migration and citizenship, missionaries, personnel, political organization, religion, scientific research, trade and tariffs, transport and various wartime arrangements.");
	List<CategorisationResult> categorisationResults = categoriserService.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("International")));

    }

}
