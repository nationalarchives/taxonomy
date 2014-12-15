package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class LegacySystemServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    public LegacySystemService legacySystemService;

    /**
     * To keep ignored not to contact external service: test connection to
     * legacy system service
     */
    @Test
    @Ignore
    public void testGetLegacyCategoriesForCatDocRef() {

	String[] legacyCategoriesForCatDocRef = legacySystemService.getLegacyCategoriesForCatDocRef("HLG 102/182");

	assertThat(legacyCategoriesForCatDocRef, is(notNullValue()));
	assertThat(legacyCategoriesForCatDocRef, is(not(emptyArray())));
	assertThat(legacyCategoriesForCatDocRef, is(equalTo(new String[] { "Construction industries", "Labour" })));
    }
}
