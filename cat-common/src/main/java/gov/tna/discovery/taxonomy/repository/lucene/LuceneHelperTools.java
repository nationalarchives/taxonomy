package gov.tna.discovery.taxonomy.repository.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHelperTools {

    private static final Logger logger = LoggerFactory.getLogger(LuceneHelperTools.class);

    /**
     * Release an an object from a manager without throwing any error<br/>
     * log if any error occurs
     * 
     * @param searcherManager
     * @param searcher
     */
    public static void releaseSearcherManagerQuietly(SearcherManager iaviewSearcherManager, IndexSearcher searcher) {
	try {
	    if (searcher != null) {
		iaviewSearcherManager.release(searcher);
	    }
	} catch (IOException ioe) {
	    logger.error("releaseSearcherManagerQuietly failed", ioe);
	}
    }

    /**
     * Close a writer without throwing any error<br/>
     * log if any error occurs
     * 
     * @param writer
     */
    public static void closeIndexWriterQuietly(IndexWriter writer) {
	try {
	    if (writer != null) {
		writer.close();
	    }
	} catch (IOException ioe) {
	    logger.error("closeWriterQuietly failed", ioe);
	}
    }

}
