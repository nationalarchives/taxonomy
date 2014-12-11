package gov.tna.discovery.taxonomy.common.repository.lucene;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;

import java.io.IOException;

import org.apache.lucene.document.Document;
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

    /**
     * Convert a lucene document from a search to InformationAssetView object
     * 
     * @param document
     * @return
     */
    public static InformationAssetView getIAViewFromLuceneDocument(Document document) {
	InformationAssetView assetView = new InformationAssetView();
	assetView.setDOCREFERENCE(document.get(InformationAssetViewFields.DOCREFERENCE.toString()));
	assetView.setTITLE(document.get(InformationAssetViewFields.TITLE.toString()));
	assetView.setDESCRIPTION(document.get(InformationAssetViewFields.DESCRIPTION.toString()));
	assetView.setCATDOCREF(document.get(InformationAssetViewFields.CATDOCREF.toString()));
	assetView.setCONTEXTDESCRIPTION(document.get(InformationAssetViewFields.CONTEXTDESCRIPTION.toString()));
	assetView.setCORPBODYS(document.getValues(InformationAssetViewFields.CORPBODYS.toString()));
	assetView.setCOVERINGDATES(document.get(InformationAssetViewFields.COVERINGDATES.toString()));
	assetView.setPERSON_FULLNAME(document.getValues(InformationAssetViewFields.PERSON_FULLNAME.toString()));
	assetView.setPLACE_NAME(document.getValues(InformationAssetViewFields.PLACE_NAME.toString()));
	assetView.setSUBJECTS(document.getValues(InformationAssetViewFields.SUBJECTS.toString()));
	return assetView;
    }

}
