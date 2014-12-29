package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class LegacySystemServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(LegacySystemServiceTest.class);

    @Autowired
    public LegacySystemService legacySystemService;

    /**
     * To keep ignored not to contact external service: test connection to
     * legacy system service
     */
    @Test
    public void testGetLegacyCategoriesForCatDocRef() {

	String[] legacyCategoriesForCatDocRef = legacySystemService.getLegacyCategoriesForCatDocRef("HLG 102/182");

	assertThat(legacyCategoriesForCatDocRef, is(notNullValue()));
	assertThat(legacyCategoriesForCatDocRef, is(not(emptyArray())));
	assertThat(legacyCategoriesForCatDocRef, is(equalTo(new String[] { "Construction industries", "Labour" })));
    }
}
