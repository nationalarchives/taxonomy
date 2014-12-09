package gov.tna.discovery.taxonomy.repository.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;

import java.io.IOException;

import org.junit.After;
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

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @After
    public void emptyDatabase() {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testMongoDbIsUpAndRunning() {
	mongoTestDataSet.initCategoryCollectionWith1element();

	Iterable<Category> iterable = repository.findAll();
	assertThat(iterable, is(notNullValue()));
	assertThat(iterable.iterator().hasNext(), is(true));

    }

    @Test
    public void testCollectionContainsFullCollection() throws IOException {
	mongoTestDataSet.initCategoryCollection();

	assertThat(repository.count(), is(equalTo(136l)));
    }

}
