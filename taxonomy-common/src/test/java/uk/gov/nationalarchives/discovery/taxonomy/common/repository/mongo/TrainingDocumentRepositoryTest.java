/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MongoConfigurationTest.class)
public class TrainingDocumentRepositoryTest {

    @Autowired
    TrainingDocumentRepository repository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initTrainingSetCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testCollectionCount() {

	Iterable<TrainingDocument> iterable = repository.findAll();
	assertThat(iterable, is(notNullValue()));
	assertThat(iterable.iterator().hasNext(), is(true));
    }

}
