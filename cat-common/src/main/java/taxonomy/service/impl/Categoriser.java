package taxonomy.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * class dedicated to the categorisation of documents<br/>
 * use the More Like This feature of Lucene
 *
 */
public class Categoriser {

	/**
	 * run More Like This process on a document by comparing its description to the description of all items of the training set<br/>
	 * currently we get a fixed number of the top results
	 * @param modelPath
	 * path of the training set index
	 * @param reader
	 * reader of the document being tested
	 * @param maxResults
	 * max number of results to return
	 * @return
	 * @throws IOException
	 */
	// TODO 1 check and update fields that are being retrieved to create training set, used for MLT (run MLT on title, context desc and desc at least. returns results by score not from a fixed number)
	public List<String> runMlt(String modelPath, Reader reader, int maxResults)
			throws IOException {

		Directory directory = FSDirectory.open(new File(modelPath));

		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_44);

		MoreLikeThis moreLikeThis = new MoreLikeThis(ireader);
		moreLikeThis.setAnalyzer(analyzer);
		moreLikeThis.setFieldNames(new String[] { "description" });

		Query query = moreLikeThis.like(reader, "description");
		
		TopDocs topDocs = isearcher.search(query, maxResults);

		List<String> result = new ArrayList<String>();

		int size = 0;
		if (topDocs.totalHits <= 100) {
			size = topDocs.totalHits;
		}

		for (int i = 0; i < size; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document hitDoc = isearcher.doc(scoreDoc.doc);
			String category = hitDoc.get("category");
			result.add(category);
		}
		
		ireader.close();
		
		return new ArrayList<String>(new LinkedHashSet<String>(result));
	}
}
