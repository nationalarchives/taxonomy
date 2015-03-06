package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.TsetInformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneTaxonomyMapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
public class TrainingSetRepository {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSetRepository.class);

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    /**
     * Create a lucene document from an trainingDocument object and add it to
     * the TrainingIndex index
     * 
     * @param trainingDocument
     * @throws IOException
     */
    public void indexTrainingSetDocument(TrainingDocument trainingDocument, IndexWriter writer) throws IOException {
	// TODO TSETBASED bulk index, this is far too slow to do it unitary!

	try {
	    if (!StringUtils.isEmpty(trainingDocument.getDescription())) {
		trainingDocument.setDescription(LuceneHelperTools.removePunctuation(trainingDocument.getDescription()));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getContextDescription())) {
		trainingDocument.setContextDescription(LuceneHelperTools.removePunctuation(trainingDocument
			.getContextDescription()));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getTitle())) {
		trainingDocument.setTitle(LuceneHelperTools.removePunctuation(trainingDocument.getTitle()));
	    }
	    Document doc = LuceneTaxonomyMapper.getLuceneDocumentFromTrainingDocument(trainingDocument);
	    writer.addDocument(doc);
	} catch (Exception e) {
	    logger.error(".indexTrainingSetDocument: an error occured on document: '{}', message: {}",
		    trainingDocument.getDocReference(), e.getMessage());
	}
    }

    public void indexTrainingDocuments(IndexWriter writer, List<TrainingDocument> trainingDocuments) {
	try {
	    for (TrainingDocument trainingDocument : trainingDocuments) {
		indexTrainingSetDocument(trainingDocument, writer);
	    }
	    writer.commit();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}
    }

    public void indexTrainingDocuments(List<TrainingDocument> trainingDocuments) {
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(Version.parseLeniently(luceneVersion),
		    trainingSetAnalyser));

	    indexTrainingDocuments(writer, trainingDocuments);
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} catch (TaxonomyException e) {
	    throw e;
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }

    public void deleteTrainingDocumentsForCategory(IndexWriter writer, Category category) {
	try {
	    logger.info(".deleteAndUpdateTraingSetIndexForCategory: removed elements for category: {}",
		    category.getTtl());
	    writer.deleteDocuments(new Term(InformationAssetViewFields.TAXONOMY.toString(), category.getTtl()));
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}
    }

    public void deleteTrainingDocumentsForCategory(Category category) {
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(Version.parseLeniently(luceneVersion),
		    trainingSetAnalyser));

	    deleteTrainingDocumentsForCategory(writer, category);
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} catch (TaxonomyException e) {
	    throw e;
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }
}
