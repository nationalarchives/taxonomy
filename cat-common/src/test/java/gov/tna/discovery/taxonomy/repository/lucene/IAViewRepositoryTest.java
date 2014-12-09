package gov.tna.discovery.taxonomy.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.config.AbstractTaxonomyTestCase;
import gov.tna.discovery.taxonomy.config.LuceneConfigurationTest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IAViewRepositoryTest extends AbstractTaxonomyTestCase {

    private static final String QUERY_WITHOUT_WILDCARD = "\"church\"";

    @Autowired
    IAViewRepository iaViewRepository;

    private static final String QUERY_WITH_LEADING_WILDCARD = "*Church";

    @Test
    public void testPerformSearchWithLeadingWildcard() {
	List<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITH_LEADING_WILDCARD, null, 100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results, is(not(empty())));
	assertThat(results.size(), is(equalTo(4)));
	logger.debug(".testPerformSearchWithLeadingWildcard: Found {} results", results.size());
    }

    @Test
    public void testPerformSearchWithQueryWithoutWildCard() {
	List<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITHOUT_WILDCARD, null, 100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results, is(not(empty())));
	assertThat(results.size(), is(equalTo(3)));
	logger.debug(".testPerformSearchWithSimpleQuery: Found {} results", results.size());
    }
}
