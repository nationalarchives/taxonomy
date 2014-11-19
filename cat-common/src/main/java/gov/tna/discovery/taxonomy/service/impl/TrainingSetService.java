package gov.tna.discovery.taxonomy.service.impl;

import gov.tna.discovery.taxonomy.config.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO create Interface for service layer
@Service
public class TrainingSetService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSetService.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    Indexer indexer;

    @Autowired
    Searcher searcher;

    public void updateTrainingSetForCategory(Category category, Float fixedLimitScore) {
	List<InformationAssetView> IAViewResults;
	try {
	    // FIXME JCT Iterate instead of taking only 100 elements
	    IAViewResults = searcher.performSearch(category.getQry(), (fixedLimitScore != null ? fixedLimitScore
		    : category.getSc()), 1000, 0);
	    logger.debug(".updateTrainingSetForCategory: Category=" + category.getTtl() + ", found "
		    + IAViewResults.size() + " result(s)");
	    if (IAViewResults.size() > 0) {

		for (InformationAssetView iaView : IAViewResults) {
		    TrainingDocument trainingDocument = new TrainingDocument();
		    trainingDocument.setCategory(category.getTtl());
		    trainingDocument.setDescription(iaView.getDESCRIPTION());
		    trainingDocument.setTitle(iaView.getTITLE());
		    trainingDocumentRepository.save(trainingDocument);
		    logger.debug(trainingDocument.getCategory() + ":" + iaView.getCATDOCREF() + " - "
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

    public void deleteAndUpdateTrainingIndexForCategory(Category category) {

	try {
	    IndexWriter writer = indexer.getIndexWriter(false, CatConstants.TRAINING_INDEX);
	    writer.deleteDocuments(new Term(InformationAssetViewFields.CATEGORY.toString(), category.getTtl()));

	    for (TrainingDocument trainingDocument : trainingDocumentRepository.findByCategory(category.getTtl())) {
		trainingDocument.setDescription(trainingDocument.getDescription().replaceAll("\\<.*?>", ""));
		trainingDocument.setTitle(trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
		indexTrainingSetDocument(trainingDocument, writer);

	    }
	    writer.close();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}

    }

    /**
     * build index of trainingDocument from mongo db trainingset<br/>
     * remove punctuation from Description, title
     * 
     * @throws IOException
     */
    public void indexTrainingSet() throws IOException {

	IndexWriter writer = indexer.getIndexWriter(false, CatConstants.TRAINING_INDEX);

	try {
	    writer.deleteAll();

	    Iterator<TrainingDocument> trainingDocumentIterator = trainingDocumentRepository.findAll().iterator();

	    while (trainingDocumentIterator.hasNext()) {
		TrainingDocument trainingDocument = trainingDocumentIterator.next();
		trainingDocument.setDescription(trainingDocument.getDescription().replaceAll("\\<.*?>", ""));
		trainingDocument.setTitle(trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
		indexTrainingSetDocument(trainingDocument, writer);

	    }
	} finally {
	    writer.close();
	}

    }

    /**
     * Create a lucene document from an trainingDocument object and add it to
     * the TrainingIndex index
     * 
     * @param trainingDocument
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
    public void indexTrainingSetDocument(TrainingDocument trainingDocument, IndexWriter writer) throws IOException {
	// TODO 4 handle exceptions, do not stop the process unless several
	// errors occur
	// TODO 1 bulk insert, this is far too slow to do it unitary!
	// TODO 4 Field is deprecated, use appropriate fields.

	Document doc = new Document();
	doc.add(new Field(InformationAssetViewFields._id.toString(), trainingDocument.get_id(), Field.Store.YES,
		Field.Index.NOT_ANALYZED));
	doc.add(new Field(InformationAssetViewFields.CATEGORY.toString(), trainingDocument.getCategory(),
		Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
	doc.add(new Field(InformationAssetViewFields.TITLE.toString(), trainingDocument.getTitle(), Field.Store.YES,
		Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
	doc.add(new Field(InformationAssetViewFields.DESCRIPTION.toString(), trainingDocument.getDescription(),
		Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
	writer.addDocument(doc);
    }

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
		continue;
	    }

	}
	logger.debug(".createTrainingSet : END");
    }

}
