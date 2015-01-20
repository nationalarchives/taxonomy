package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("queryBased")
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class QueryBasedCategoriserServiceTest {

    @Autowired
    QueryBasedCategoriserServiceImpl categoriserService;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("WO 195/11733");
	iaView.setTITLE("Crop Committee: experiments at Camp Detrick with plant inhibitors on sugar beet crops");
	iaView.setDESCRIPTION("Crop Committee: experiments at Camp Detrick with plant inhibitors on sugar beet crops");
	List<CategorisationResult> categorisationResults = categoriserService.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Farming")));

    }

}
