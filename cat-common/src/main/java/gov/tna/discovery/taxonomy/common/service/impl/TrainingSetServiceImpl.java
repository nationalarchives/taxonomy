package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneHelperTools;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

//TODO create Interface for service layer
@Service
public class TrainingSetServiceImpl implements TrainingSetService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSetServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.trainingSetCollectionPath}")
    private String trainingSetCollectionPath;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.TrainingSetService#
     * updateTrainingSetForCategory
     * (gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category,
     * java.lang.Float)
     */
    @Override
    public void updateTrainingSetForCategory(Category category, Float fixedLimitScore) {
	PaginatedList<InformationAssetView> IAViewResults;
	try {
	    // FIXME JCT Iterate instead of taking only 100 elements
	    IAViewResults = iaViewRepository.performSearch(category.getQry(),
		    (fixedLimitScore != null ? fixedLimitScore : category.getSc()), 1000, 0);
	    logger.debug(".updateTrainingSetForCategory: Category=" + category.getTtl() + ", found "
		    + IAViewResults.size() + " result(s)");
	    if (IAViewResults.size() > 0) {

		for (InformationAssetView iaView : IAViewResults.getResults()) {
		    TrainingDocument trainingDocument = new TrainingDocument();
		    trainingDocument.setCategory(category.getTtl());
		    trainingDocument.setDescription(iaView.getDESCRIPTION());
		    trainingDocument.setContextDescription(iaView.getCONTEXTDESCRIPTION());
		    trainingDocument.setTitle(iaView.getTITLE());
		    trainingDocument.setDocReference(iaView.getDOCREFERENCE());
		    trainingDocument.setCatDocRef(iaView.getCATDOCREF());
		    trainingDocument.setCorpBodys(iaView.getCORPBODYS());
		    trainingDocument.setPersonFullName(iaView.getPERSON_FULLNAME());
		    trainingDocument.setPlaceName(iaView.getPLACE_NAME());
		    trainingDocument.setSubjects(iaView.getSUBJECTS());
		    trainingDocumentRepository.save(trainingDocument);
		    logger.debug(trainingDocument.getCategory() + ":" + iaView.getDOCREFERENCE() + " - "
			    + trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
		}
	    }
	} catch (TaxonomyException e) {
	    // TODO 1 several errors occur while creating the training set,
	    // to investigate
	    // some queries are not valid: paul takes care of them.
	    // Some queries have wildcards and lucene doesnt accept them: to
	    // enable.
	    logger.error(".updateTrainingSetForCategory< An error occured for category: " + category.toString());
	    logger.error(".updateTrainingSetForCategory< Error message: " + e.getMessage());
	    throw e;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.TrainingSetService#
     * indexTrainingSetDocument
     * (gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument,
     * org.apache.lucene.index.IndexWriter)
     */
    @Override
    public void indexTrainingSetDocument(TrainingDocument trainingDocument, IndexWriter writer) throws IOException {
	// TODO 4 handle exceptions, do not stop the process unless several
	// errors occur
	// TODO 1 bulk insert, this is far too slow to do it unitary!
	// FIXME why to remove punctuation before indexing? analyser duty

	try {
	    if (!StringUtils.isEmpty(trainingDocument.getDescription())) {
		trainingDocument.setDescription(trainingDocument.getDescription().replaceAll("\\<.*?>", ""));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getContextDescription())) {
		trainingDocument.setContextDescription(trainingDocument.getContextDescription().replaceAll("\\<.*?>",
			""));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getTitle())) {
		trainingDocument.setTitle(trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
	    }
	    Document doc = new Document();

	    doc.add(new StringField(InformationAssetViewFields.DOCREFERENCE.toString(), trainingDocument
		    .getDocReference(), Field.Store.YES));

	    doc.add(new TextField(InformationAssetViewFields.DESCRIPTION.toString(), trainingDocument.getDescription(),
		    Field.Store.YES));

	    if (!StringUtils.isEmpty(trainingDocument.getCatDocRef())) {
		doc.add(new StringField(InformationAssetViewFields.CATDOCREF.toString(), trainingDocument
			.getCatDocRef(), Field.Store.YES));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getCategory())) {
		doc.add(new StringField(InformationAssetViewFields.CATEGORY.toString(), trainingDocument.getCategory(),
			Field.Store.YES));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getTitle())) {
		doc.add(new TextField(InformationAssetViewFields.TITLE.toString(), trainingDocument.getTitle(),
			Field.Store.YES));
	    }
	    if (!StringUtils.isEmpty(trainingDocument.getTitle())) {
		doc.add(new TextField(InformationAssetViewFields.CONTEXTDESCRIPTION.toString(), trainingDocument
			.getContextDescription(), Field.Store.YES));
	    }
	    if (trainingDocument.getCorpBodys() != null) {
		doc.add(new TextField(InformationAssetViewFields.CORPBODYS.toString(), Arrays.toString(trainingDocument
			.getCorpBodys()), Field.Store.YES));
	    }
	    if (trainingDocument.getPersonFullName() != null) {
		doc.add(new TextField(InformationAssetViewFields.PERSON_FULLNAME.toString(), Arrays
			.toString(trainingDocument.getPersonFullName()), Field.Store.YES));
	    }
	    if (trainingDocument.getPlaceName() != null) {
		doc.add(new TextField(InformationAssetViewFields.PLACE_NAME.toString(), Arrays
			.toString(trainingDocument.getPlaceName()), Field.Store.YES));
	    }
	    if (trainingDocument.getSubjects() != null) {
		doc.add(new TextField(InformationAssetViewFields.SUBJECTS.toString(), Arrays.toString(trainingDocument
			.getSubjects()), Field.Store.YES));
	    }
	    writer.addDocument(doc);
	} catch (Exception e) {
	    logger.error(".indexTrainingSetDocument: an error occured on document: '{}', message: {}",
		    trainingDocument.getDocReference(), e.getMessage());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.TrainingSetService#
     * createTrainingSet (java.lang.Float)
     */
    @Override
    public void createTrainingSet(Float fixedLimitScore) throws IOException, ParseException {
	logger.debug(".createTrainingSet : START");

	Iterator<Category> categoryIterator = categoryRepository.findAll().iterator();

	// empty collection
	trainingDocumentRepository.deleteAll();

	while (categoryIterator.hasNext()) {
	    Category category = categoryIterator.next();
	    try {
		updateTrainingSetForCategory(category, fixedLimitScore);
	    } catch (TaxonomyException e) {
		logger.error(".createTrainingSet: error while parsing Category '{}': {}", category.getTtl(),
			e.toString());
		continue;
	    }

	}
	logger.debug(".createTrainingSet : END");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.TrainingSetService#
     * deleteAndUpdateTraingSetIndexForCategory
     * (gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category)
     */
    @Override
    public void deleteAndUpdateTraingSetIndexForCategory(Category category) {
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(getLuceneVersion(),
		    trainingSetAnalyser));
	    writer.deleteDocuments(new Term(InformationAssetViewFields.CATEGORY.toString(), category.getTtl()));

	    List<TrainingDocument> trainingDocuments = trainingDocumentRepository.findByCategory(category.getTtl());
	    logger.info(".deleteAndUpdateTraingSetIndexForCategory: indexing {} elements", trainingDocuments.size());
	    for (TrainingDocument trainingDocument : trainingDocuments) {
		indexTrainingSetDocument(trainingDocument, writer);
	    }
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }

    private Version getLuceneVersion() {
	return Version.valueOf(luceneVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.TrainingSetService#
     * indexTrainingSet ()
     */
    @Override
    public void indexTrainingSet() {
	IndexWriter writer = null;
	try {
	    writer = new IndexWriter(trainingSetDirectory, new IndexWriterConfig(getLuceneVersion(),
		    trainingSetAnalyser));

	    writer.deleteAll();

	    Iterator<TrainingDocument> trainingDocumentIterator = trainingDocumentRepository.findAll().iterator();

	    while (trainingDocumentIterator.hasNext()) {
		TrainingDocument trainingDocument = trainingDocumentIterator.next();
		indexTrainingSetDocument(trainingDocument, writer);

	    }
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}

    }

    @Override
    public void deleteMongoTrainingDocumentByCategory(String category) {
	int numberOfRemovedElements = trainingDocumentRepository.deleteByCategory(category);
	logger.info(".deleteMongoTrainingDocumentByCategory < removed {} elements", numberOfRemovedElements);
    }

}
