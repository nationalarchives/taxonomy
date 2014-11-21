package gov.tna.discovery.taxonomy.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.MongoConfigurationTest;
import gov.tna.discovery.taxonomy.MongoTestDataSet;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MongoConfigurationTest.class)
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    // @Autowired
    // MongoTestDataSet mongoTestDataSet;

    @Test
    public void testCollectionCount() {
	// mongoTestDataSet.createCategory();

	Iterable<Category> iterable = repository.findAll();
	assertThat(iterable, is(notNullValue()));
	assertThat(iterable.iterator().hasNext(), is(true));
    }

}
