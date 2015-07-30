/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.LuceneConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { LuceneConfigurationTest.class })
public class TrainingSetRepositoryTest {

    @Autowired
    private SearcherManager trainingSetSearcherManager;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private LuceneTestTrainingDataSet luceneTestDataSet;

    @Test
    public void testRetrieveLatestWrittenDocument() throws IOException {
	try {
	    checkNumberOfDocumentsIs0ForTestCategory();
	    luceneTestDataSet.updateTrainingSetForTestCategory();
	    checkNumberOfDocumentsIncrementedForTestCategory();
	} finally {
	    luceneTestDataSet.deleteTrainingSetForTestCategory();
	}
    }

    private void checkNumberOfDocumentsIs0ForTestCategory() throws IOException {
	IndexSearcher searcher = trainingSetSearcherManager.acquire();
	Query query = new TermQuery(new Term(InformationAssetViewFields.TAXONOMY.toString(),
		LuceneTestTrainingDataSet.TEST_CATEGORY));
	TopDocs search = searcher.search(query, 1);
	assertThat(search.totalHits, is(equalTo(0)));
	LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);

    }

    private void checkNumberOfDocumentsIncrementedForTestCategory() throws IOException {
	Boolean wasRefreshed = trainingSetSearcherManager.maybeRefresh();
	assertThat(wasRefreshed, is(true));
	IndexSearcher searcher = trainingSetSearcherManager.acquire();
	Query query = new TermQuery(new Term(InformationAssetViewFields.TAXONOMY.toString(),
		LuceneTestTrainingDataSet.TEST_CATEGORY));
	TopDocs search = searcher.search(query, 1);
	assertThat(search.totalHits, is(equalTo(1)));
	LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);
    }

}
