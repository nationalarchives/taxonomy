package uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.LegacySystemRepository;

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
    public LegacySystemRepository legacySystemService;

    /**
     * To keep ignored not to contact external service
     */
    @Test
    @Ignore
    public void testGetLegacyCategoriesForCatDocRef() {

	String[] legacyCategoriesForCatDocRef = legacySystemService.getLegacyCategoriesForCatDocRef("HLG 102/182");

	assertThat(legacyCategoriesForCatDocRef, is(notNullValue()));
	assertThat(legacyCategoriesForCatDocRef, is(not(emptyArray())));
	assertThat(legacyCategoriesForCatDocRef, is(equalTo(new String[] { "Construction industries", "Labour" })));
    }

    /**
     * To keep ignored not to contact external service
     */
    @Test
    @Ignore
    public void testFindLegacyDocumentsByQuery() {

	Map<String, String[]> mapOfDocumentIaidsAndCategories = legacySystemService.findLegacyDocumentsByCategory(
		"Labour", 1);

	assertThat(mapOfDocumentIaidsAndCategories, is(notNullValue()));
	assertThat(mapOfDocumentIaidsAndCategories.entrySet(), is(not(emptyIterable())));
    }
}
