package gov.tna.discovery.taxonomy.repository.lucene;

import gov.tna.discovery.taxonomy.config.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.stereotype.Component;

//TODO put timeout on search requests on index: related to wildcard
@Component
public class Searcher {

    // private static final Logger logger =
    // LoggerFactory.getLogger(Searcher.class);

    public Document getDoc(ScoreDoc scoreDoc) throws IOException {
	File file = new File(CatConstants.IAVIEW_INDEX);
	SimpleFSDirectory index = new SimpleFSDirectory(file);
	DirectoryReader ireader = DirectoryReader.open(index);
	IndexSearcher isearcher = new IndexSearcher(ireader);
	Document hitDoc = isearcher.doc(scoreDoc.doc);
	ireader.close();
	index.close();
	return hitDoc;
    }

    // FIXME 0 return the number of results? > if minimumScore provided,
    // requires to go through the whole collection of topdocs
    public List<InformationAssetView> performSearch(String queryString, Float mimimumScore, Integer limit,
	    Integer offset) {
	List<InformationAssetView> docs = new ArrayList<InformationAssetView>();
	try {
	    File file = new File(CatConstants.IAVIEW_INDEX);
	    Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
	    SimpleFSDirectory index = new SimpleFSDirectory(file);
	    DirectoryReader ireader = DirectoryReader.open(index);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    QueryParser parser = new QueryParser(CatConstants.LUCENE_VERSION,
		    InformationAssetViewFields.DESCRIPTION.toString(), analyzer);
	    parser.setAllowLeadingWildcard(true);
	    Query query;
	    try {
		query = parser.parse(queryString);
	    } catch (ParseException e) {
		throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	    }
	    TopDocs topDocs;
	    topDocs = isearcher.search(query, offset + limit);
	    int totalNumberOfDocumentsToParse = offset + limit;
	    if (topDocs.totalHits < offset) {
		return docs;
	    } else if (topDocs.totalHits < totalNumberOfDocumentsToParse) {
		totalNumberOfDocumentsToParse = topDocs.totalHits;
	    }
	    for (int i = offset; i < totalNumberOfDocumentsToParse; i++) {

		ScoreDoc scoreDoc = topDocs.scoreDocs[i];
		if (mimimumScore != null && scoreDoc.score < mimimumScore) {
		    // FIXME JCT use HitCollector instead? to return the total
		    // number of results later
		    break;
		}
		Document hitDoc = isearcher.doc(scoreDoc.doc);
		InformationAssetView assetView = new InformationAssetView();
		assetView.setDoc(scoreDoc.doc);
		assetView.setShardIndex(scoreDoc.shardIndex);
		assetView.set_id(hitDoc.get(InformationAssetViewFields._id.toString()));
		assetView.setCATDOCREF(hitDoc.get(InformationAssetViewFields.CATDOCREF.toString()));
		assetView.setTITLE(hitDoc.get(InformationAssetViewFields.TITLE.toString()));
		assetView.setDESCRIPTION(hitDoc.get(InformationAssetViewFields.DESCRIPTION.toString()));
		assetView.setScore(scoreDoc.score);
		String[] iaidArray = hitDoc.get(InformationAssetViewFields.URLPARAMS.toString()).split("/");
		String iaid = iaidArray[iaidArray.length - 1];
		assetView.setURLPARAMS(iaid);
		docs.add(assetView);
	    }
	    ireader.close();
	    index.close();
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}
	return docs;
    }

    public void checkCategoryQueryValidity(String qry) {
	QueryParser qp = new QueryParser(CatConstants.LUCENE_VERSION, "CATEGORY", new WhitespaceAnalyzer(
		CatConstants.LUCENE_VERSION));
	try {
	    qp.parse(qry);
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	}
    }
}
