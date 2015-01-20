package gov.tna.discovery.taxonomy.common.repository.lucene;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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

    @Value("${lucene.index.version}")
    private String luceneVersion;

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
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(Version.valueOf(luceneVersion),
		    trainingSetAnalyser));

	    writer.deleteAll();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }

}
