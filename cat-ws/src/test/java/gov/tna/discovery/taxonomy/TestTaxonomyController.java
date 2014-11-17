package gov.tna.discovery.taxonomy;

import static org.junit.Assert.fail;
import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WSApplication.class)
@WebAppConfiguration
@IntegrationTest
public class TestTaxonomyController {

    // @Autowired
    // CityRepository repository;

    RestTemplate restTemplate = new TestRestTemplate();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testHelloWorld() {
	String responseBody = restTemplate.getForEntity("http://localhost:8080/taxonomy/hello", String.class).getBody();
	assertThat(responseBody, Matchers.equalTo("Hello World"));
    }

    @Test
    public final void testSearchIaView() {
	SearchIAViewRequest request = new SearchIAViewRequest();
	request.setCategoryQuery("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	request.setLimit(10);
	List<InformationAssetView> iaViews = restTemplate.postForEntity("http://localhost:8080/taxonomy/search",
		request, List.class).getBody();
	assertThat(iaViews, is(notNullValue()));
	assertThat(iaViews, is(not(empty())));
    }

    @Test
    @Ignore
    public final void testPublish() {
	fail("Not yet implemented"); // TODO
    }

}
