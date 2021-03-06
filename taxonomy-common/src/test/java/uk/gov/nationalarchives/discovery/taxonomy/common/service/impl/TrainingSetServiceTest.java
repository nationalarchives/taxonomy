/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.LuceneTestTrainingDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class TrainingSetServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TrainingSetServiceTest.class);

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    ReaderManager trainingSetReaderManager;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    IAViewRepository iaViewRepository;

    @Autowired
    LuceneTestTrainingDataSet luceneTestDataSet;

    @After
    public void dropDbAndIndex() {

	mongoTestDataSet.dropDatabase();
	luceneTestDataSet.deleteTrainingSetIndex();
    }

    @Test
    public void testCreateTrainingSetWithLimitScore() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(0.001f, null);
	assertThat(trainingDocumentRepository.count(), equalTo(10l));
    }

    @Test
    public void testCreateTrainingSetWithLimitSize() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(null, 1);
	assertThat(trainingDocumentRepository.count(), equalTo(9l));
    }

    @Test
    public void testIndexTrainingSet() throws IOException, InterruptedException, ParseException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.indexTrainingSet();
	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	Thread.sleep(1000);
	assertThat(trainingSetIndexReader.numDocs(), equalTo(200));
	trainingSetReaderManager.release(trainingSetIndexReader);

    }

    @Test
    public void testDeleteAndUpdateTraingSetIndexForCategory() throws IOException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.deleteAndUpdateTraingSetIndexForCategory(categoryRepository.findByCiaid("C10052"));

	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	assertThat(trainingSetIndexReader.numDocs(), is(not(equalTo(0))));
    }

    @Test
    public void testUpdateCategoriesScoresAndTrainingSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
	for (Category category : categoryRepository.findAll()) {
	    logger.debug("{} : {}", category.getTtl(), category.getSc());
	}

	int maxNumber = 20;
	int minNumber = 10;
	trainingSetService.updateCategoriesScores(minNumber, maxNumber);
	for (Category category : categoryRepository.findAll()) {
	    PaginatedList<InformationAssetView> response = iaViewRepository.performSearch(category.getQry(),
		    category.getSc(), 100, 0);
	    logger.info("{} : {} results, for min score {}", category.getTtl(), response.getNumberOfResults(),
		    category.getSc());
	    assertThat(response.getNumberOfResults(), is(equalTo(response.getResults().size())));
	    assertThat(response.getNumberOfResults(), is(lessThanOrEqualTo(maxNumber)));
	    assertThat(response.getNumberOfResults(), is(greaterThanOrEqualTo(0)));
	}

	// for (Category category : categoryRepository.findAll()) {
	// List<TrainingDocument> trainingDocuments =
	// trainingDocumentRepository.findByCategory(category.getTtl());
	// logger.info("found {} documents for category {}",
	// trainingDocuments.size(), category.getTtl());
	// assertThat(trainingDocuments.size(), is(lessThanOrEqualTo(10)));
	// if (trainingDocuments.size() != 0) {
	// assertThat(trainingDocuments.size(), is(greaterThanOrEqualTo(2)));
	// }
	// }
    }

}
