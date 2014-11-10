package taxonomy.repository.lucene;

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
import org.apache.lucene.util.Version;

import taxonomy.CatConstants;
import taxonomy.repository.domain.InformationAssetView;

public class Searcher {
	
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

	public List<InformationAssetView> performSearch(String queryString, int size)
			throws IOException, ParseException {

		File file = new File(CatConstants.IAVIEW_INDEX);
		Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
		SimpleFSDirectory index = new SimpleFSDirectory(file);
		DirectoryReader ireader = DirectoryReader.open(index);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		QueryParser parser = new QueryParser(CatConstants.LUCENE_VERSION, "DESCRIPTION",
				analyzer);
		Query query = parser.parse(queryString);
		TopDocs topDocs = isearcher.search(query, size);
		List<InformationAssetView> docs = new ArrayList<InformationAssetView>();
		if (topDocs.totalHits <= size) {
			size = topDocs.totalHits;
		}
		for (int i = 0; i < size; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document hitDoc = isearcher.doc(scoreDoc.doc);
			InformationAssetView assetView = new InformationAssetView();
			assetView.set_id(hitDoc.get("id"));
			assetView.setCATDOCREF(hitDoc.get("CATDOCREF"));
			assetView.setTITLE(hitDoc.get("TITLE"));
			assetView.setDESCRIPTION(hitDoc.get("DESCRIPTION"));
			assetView.setScore(scoreDoc.score);
			String[] iaidArray = hitDoc.get("URLPARAMS").split("/");
			String iaid = iaidArray[iaidArray.length - 1];
			assetView.setURLPARAMS(iaid);
			docs.add(assetView);
		}
		ireader.close();
		index.close();
		return docs;
	}
}
