package gov.tna.discovery.taxonomy.common.repository.lucene;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;
import gov.tna.discovery.taxonomy.common.service.impl.CategoriserServiceImpl;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LuceneTestDataSet {

    private static final Logger logger = LoggerFactory.getLogger(LuceneTestDataSet.class);

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
	trainingSetRepository.indexTrainingDocuments(null, Arrays.asList(trainingDocument));
    }

    public void deleteTrainingSetForTestCategory() {
	logger.info(".deleteTrainingSetForTestCategory");
	trainingSetRepository.deleteTrainingDocumentsForCategory(null, getTestCategory());
    }

}
