package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
	iaView.setCATDOCREF("BT 351/1/107278");
	iaView.setCONTEXTDESCRIPTION("Registry of Shipping and Seamen: Index of First World War Mercantile Marine Medals and the British War Medal.");
	iaView.setCOVERINGDATES("1914-1925");
	iaView.setDESCRIPTION("Medal Card of Oosten Dorp or Van Oosten Dorp, B M. Place of Birth: Rothendam. Date of Birth: 1898.");
	iaView.setDOCREFERENCE("D8075845");
	iaView.setPERSON_FULLNAME(new String[] { "B M Oosten Dorp or Van Oosten Dorp" });
	iaView.setPLACE_NAME(new String[] { "Rothendam" });
	iaView.setSUBJECTS(new String[] { "WW1", "Merchant", "Seamen", "Medal", "Cards" });
	iaView.setTITLE("Medal Card of Oosten Dorp or Van Oosten Dorp, B M. Place of Birth: Rothendam");

	List<CategorisationResult> categorisationResults = categoriserService.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Food and drink")));

    }

    @Test
    public void testCategoriseSingle() {
	InformationAssetViewMongoRepository informationAssetViewMongoRepositoryMock = Mockito
		.mock(InformationAssetViewMongoRepository.class);
	IAViewUpdateRepository iaViewUpdateRepositoryMock = Mockito.mock(IAViewUpdateRepository.class);
	categoriserService.setIaViewMongoRepository(informationAssetViewMongoRepositoryMock);
	categoriserService.setIaViewUpdateRepository(iaViewUpdateRepositoryMock);

	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("BT 351/1/107278");
	iaView.setCONTEXTDESCRIPTION("Registry of Shipping and Seamen: Index of First World War Mercantile Marine Medals and the British War Medal.");
	iaView.setCOVERINGDATES("1914-1925");
	iaView.setDESCRIPTION("Medal Card of Oosten Dorp or Van Oosten Dorp, B M. Place of Birth: Rothendam. Date of Birth: 1898.");
	iaView.setDOCREFERENCE("D8075845");
	iaView.setPERSON_FULLNAME(new String[] { "B M Oosten Dorp or Van Oosten Dorp" });
	iaView.setPLACE_NAME(new String[] { "Rothendam" });
	iaView.setSUBJECTS(new String[] { "WW1", "Merchant", "Seamen", "Medal", "Cards" });
	iaView.setTITLE("Medal Card of Oosten Dorp or Van Oosten Dorp, B M. Place of Birth: Rothendam");
	iaView.setSERIES("BT 351");

	List<CategorisationResult> categorisationResults = categoriserService.categoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Food and drink")));

	Mockito.verify(informationAssetViewMongoRepositoryMock, Mockito.times(1)).save(
		Mockito.any(MongoInformationAssetView.class));
	Mockito.verify(iaViewUpdateRepositoryMock, Mockito.times(1)).save(Mockito.any(IAViewUpdate.class));

    }

}