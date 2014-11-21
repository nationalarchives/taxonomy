package gov.tna.discovery.taxonomy.repository.lucene;

import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHelperTools {

    private static final Logger logger = LoggerFactory.getLogger(LuceneHelperTools.class);

    /**
     * Release an an object from a manager without throwing any error
     * 
     * @param searcherManager
     * @param searcher
     */
    public static void releaseQuietly(SearcherManager iaviewSearcherManager, IndexSearcher searcher) {
	try {
	    if (searcher != null) {
		iaviewSearcherManager.release(searcher);
	    }
	} catch (IOException ioe) {
	    logger.error("releaseQuietly failed", ioe);
	}
    }

}
