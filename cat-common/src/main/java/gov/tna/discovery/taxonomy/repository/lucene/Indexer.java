package gov.tna.discovery.taxonomy.repository.lucene;

import gov.tna.discovery.taxonomy.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.domain.mongo.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO 4 all those methods do not require context and could be static
//TODO 4 use private methods where appropriate
@Service
public class Indexer {

//    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    /**
     * initialize index writer from index directory
     * 
     * @param create
     * @param indexDirectory
     * @return
     * @throws IOException
     */
    private IndexWriter getIndexWriter(boolean create, String indexDirectory) throws IOException {

	IndexWriter indexWriter = null;

	if (indexWriter == null) {
	    Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
	    IndexWriterConfig config = new IndexWriterConfig(CatConstants.LUCENE_VERSION, analyzer);
	    File file = new File(indexDirectory);
	    SimpleFSDirectory index = new SimpleFSDirectory(file);

	    // if (IndexWriter.isLocked(index)) {
	    // logger.warn("IndexWriter locked on directory: {}",
	    // indexDirectory);
	    // IndexWriter.unlock(index);
	    // }

	    indexWriter = new IndexWriter(index, config);
	}
	return indexWriter;
    }

    /**
     * initialize index reader from index directory
     * 
     * @param indexDirectory
     * @return
     * @throws IOException
     */
    public IndexReader getIndexReader(String indexDirectory) throws IOException {

	IndexReader indexReader = null;

	if (indexReader == null) {
//	    Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
//	    IndexWriterConfig config = new IndexWriterConfig(CatConstants.LUCENE_VERSION, analyzer);
	    File file = new File(indexDirectory);
	    SimpleFSDirectory index = new SimpleFSDirectory(file);
	    // TODO 2 make sure it does not get the deleted elements
	    indexReader = DirectoryReader.open(index);
	}
	return indexReader;
    }

    /**
     * build index of trainingDocument from mongo db trainingset<br/>
     * remove punctuation from Description, title
     * 
     * @throws IOException
     */
    public void buildTrainingIndex() throws IOException {

	IndexWriter writer = getIndexWriter(false, CatConstants.TRAINING_INDEX);

	try {
	    writer.deleteAll();

	    Iterator<TrainingDocument> trainingDocumentIterator = trainingDocumentRepository.findAll().iterator();

	    while (trainingDocumentIterator.hasNext()) {
		TrainingDocument trainingDocument = trainingDocumentIterator.next();
		trainingDocument.setDESCRIPTION(trainingDocument.getDESCRIPTION().replaceAll("\\<.*?>", ""));
		trainingDocument.setTITLE(trainingDocument.getTITLE().replaceAll("\\<.*?>", ""));
		indexTrainingSet(trainingDocument, writer);

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
    public void indexTrainingSet(TrainingDocument trainingDocument, IndexWriter writer) throws IOException {
	// TODO 4 handle exceptions, do not stop the process unless several
	// errors occur
	// TODO 1 bulk insert, this is far too slow to do it unitary!
	// TODO 4 Field is deprecated, use appropriate fields.

	Document doc = new Document();
	doc.add(new Field(InformationAssetViewFields._id.toString(), trainingDocument.get_id(), Field.Store.YES,
		Field.Index.NOT_ANALYZED));
	doc.add(new Field(InformationAssetViewFields.CATEGORY.toString(), trainingDocument.getCATEGORY(),
		Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO));
	doc.add(new Field(InformationAssetViewFields.TITLE.toString(), trainingDocument.getTITLE(), Field.Store.YES,
		Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
	doc.add(new Field(InformationAssetViewFields.DESCRIPTION.toString(), trainingDocument.getDESCRIPTION(),
		Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
	writer.addDocument(doc);
    }

    /**
     * build index of IAViews from mongodb of IA
     * 
     * @throws IOException
     */
    // @Deprecated
    // public void buildIndex() throws IOException {
    //
    // MongoAccess mongoAccess = new MongoAccess();
    // DBCollection collection =
    // mongoAccess.getMongoCollection(CatConstants.MONGO_IA_DB,
    // CatConstants.MONGO_IA_COLL);
    // DBCursor cursor = collection.find();
    // try {
    // while (cursor.hasNext()) {
    // BasicDBObject dbObject = (BasicDBObject) cursor.next();
    // String _id = dbObject.getString(InformationAssetFields._id.toString());
    // String catdocref =
    // dbObject.getString(InformationAssetFields.IAID.toString());
    // String title =
    // dbObject.getString(InformationAssetFields.Title.toString()).replaceAll("\\<.*?>",
    // "");
    // DBObject scopecontent = (BasicDBObject)
    // dbObject.get(InformationAssetFields.ScopeContent.toString());
    // String description = (String)
    // scopecontent.get(InformationAssetFields.Description.toString())
    // .toString().replaceAll("\\<.*?>", "");
    // logger.debug(description);
    // String urlparams =
    // dbObject.getString(InformationAssetFields.IAID.toString());
    // InformationAssetViewFull informationAssetView = new
    // InformationAssetViewFull();
    // informationAssetView.set_id(_id);
    // informationAssetView.setCATDOCREF(catdocref);
    // informationAssetView.setTITLE(title);
    // informationAssetView.setDESCRIPTION(description);
    // informationAssetView.setURLPARAMS(urlparams);
    // indexAsset(informationAssetView);
    // logger.debug("IA=" + catdocref + " added to index");
    // }
    // } finally {
    // cursor.close();
    // }
    //
    // }

    /**
     * Create a lucene document from an IAView object and add it to the IAIndex
     * index
     * 
     * @param asset
     * @throws IOException
     */
    // @Deprecated
    // public void indexAsset(InformationAssetViewFull asset) throws IOException
    // {
    // IndexWriter writer = getIndexWriter(false, CatConstants.IAVIEW_INDEX);
    // Document doc = new Document();
    // doc.add(new TextField(InformationAssetViewFields._id.toString(),
    // asset.get_id(), Field.Store.YES));
    // doc.add(new TextField(InformationAssetViewFields.CATDOCREF.toString(),
    // asset.getCATDOCREF(), Field.Store.YES));
    // doc.add(new TextField(InformationAssetViewFields.TITLE.toString(),
    // asset.getTITLE(), Field.Store.YES));
    // doc.add(new TextField(InformationAssetViewFields.DESCRIPTION.toString(),
    // asset.getDESCRIPTION(),
    // Field.Store.YES));
    // doc.add(new TextField(InformationAssetViewFields.URLPARAMS.toString(),
    // asset.getURLPARAMS(), Field.Store.YES));
    // writer.addDocument(doc);
    // writer.close();
    // }
}
