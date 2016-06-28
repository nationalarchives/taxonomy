/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

import java.io.IOException;
import java.util.Arrays;

@Component
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
public class LuceneTestTrainingDataSet {

    private static final Logger logger = LoggerFactory.getLogger(LuceneTestTrainingDataSet.class);

    public static final String TEST_DOCREF = "TEST_DOCREF";

    public static final String TEST_CATEGORY = "TEST_CATEGORY";
    public static final String TEST_DESC = "TESTDESC";

    @Autowired
    private TrainingSetRepository trainingSetRepository;

    @Autowired
    private Directory trainingSetDirectory;
    @Autowired
    private Analyzer trainingSetAnalyser;

    public Category getTestCategory() {
	Category category = new Category();
	category.setQry("testdesc");
	category.setTtl(TEST_CATEGORY);
	return category;
    }

    public void updateTrainingSetForTestCategory() {
	logger.info(".updateTrainingSetForTestCategory");
	TrainingDocument trainingDocument = new TrainingDocument();
	trainingDocument.setDocReference(TEST_DOCREF);
	trainingDocument.setCategory(TEST_CATEGORY);
	trainingDocument.setDescription(TEST_DESC);
	trainingSetRepository.indexTrainingDocuments(Arrays.asList(trainingDocument));
    }

    public void deleteTrainingSetForTestCategory() {
	logger.info(".deleteTrainingSetForTestCategory");
	trainingSetRepository.deleteTrainingDocumentsForCategory(getTestCategory());
    }

    public void deleteTrainingSetIndex() {
	logger.info(".deleteTrainingSetIndex");
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(trainingSetAnalyser));

	    writer.deleteAll();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeCloseableObjectQuietly(writer);
	}
    }

}
