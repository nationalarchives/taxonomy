package gov.tna.discovery.taxonomy.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.ConfigurationTest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearcherTest {

    private static final String QUERY_WITHOUT_WILDCARD = "\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"";

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    Searcher searcher;

    private static final String QUERY_WITH_LEADING_WILDCARD = "\"renewable energy\" OR \"renewable energies\" OR \"renewable electricity\" OR \"alternative energy\" OR \"alternative energies\" OR \"renewable fuel\" OR \"renewable fuels\" OR \"biogas\" OR \"biomass\" OR \"biofuel\" OR \"hydroelectric\" OR \"hydroelectricity\" OR \"hydropower\" OR (\"wind energy\"~5) OR \"wind farm\" OR \"wind farms\" OR \"wind power\" OR \"wind turbine\" OR \"wind turbines\" OR (\"solar power\"~5) OR (\"solar energy\"~5) OR \"solar panel\" OR \"solar panels\" OR \"landfill gas\" OR \"landfill gases\" OR \"geothermal\" OR \"photovoltaic\" OR \"tidal energy\" OR \"tidal energies\" OR \"tidal power\" OR \"wave farm\" OR \"wave farms\" OR (\"ocean energy\"~5) OR (\"kinetic energy\"~5) OR (\"kinetic energies\"~5) OR (*thermal AND energy) OR (*thermal AND energies) OR \"Renewables Advisory Board\" OR \"Renewable Energy Agency\" OR \"Geothermal Association\" OR \"Energy Saving Trust\" OR \"Non-Fossil Fuel Obligation\" OR \"Renewables Obligation\" OR \"Renewables Directive\" OR \"green energy\" OR \"green energies\" OR (\"energy conservation\"~2)";

    @Test
    public void testPerformSearchWithLeadingWildcard() {
	List<InformationAssetView> results = searcher.performSearch(QUERY_WITH_LEADING_WILDCARD, null, 100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results, is(not(empty())));
	logger.debug(".testPerformSearchWithLeadingWildcard: Found {} results", results.size());
    }

    @Test
    public void testPerformSearchWithQueryWithoutWildCard() {
	List<InformationAssetView> results = searcher.performSearch(QUERY_WITHOUT_WILDCARD, null, 100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results, is(not(empty())));
	logger.debug(".testPerformSearchWithSimpleQuery: Found {} results", results.size());
    }
}
