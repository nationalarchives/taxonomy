package gov.tna.discovery.taxonomy.repository.lucene;

import gov.tna.discovery.taxonomy.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
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
import org.springframework.stereotype.Component;

//TODO use NRTManagerReopenThread to instanciate indexWriters
//http://stackoverflow.com/questions/11382971/how-to-safely-close-an-indexreader
@Component
public class Indexer {

    // private static final Logger logger =
    // LoggerFactory.getLogger(Indexer.class);

    /**
     * initialize index writer from index directory
     * 
     * @param create
     * @param indexDirectory
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter(boolean create, String indexDirectory) throws IOException {

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
	    File file = new File(indexDirectory);
	    SimpleFSDirectory index = new SimpleFSDirectory(file);
	    // TODO 2 make sure it does not get the deleted elements
	    indexReader = DirectoryReader.open(index);
	}
	return indexReader;
    }

}
