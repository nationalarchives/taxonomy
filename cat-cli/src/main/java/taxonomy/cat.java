package taxonomy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import taxonomy.repository.domain.Category;
import taxonomy.repository.domain.InformationAsset;
import taxonomy.repository.domain.InformationAssetView;
import taxonomy.repository.domain.TrainingDocument;
import taxonomy.repository.lucene.Indexer;
import taxonomy.repository.lucene.Searcher;
import taxonomy.repository.mongo.MongoAccess;
import taxonomy.service.impl.Categoriser;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class cat {

	// TODO 3 add logging system to improve performances, do not use system.out!
	// TODO 0 0 deadlines, needs, will there be some time to complete things afterwards, can we provide a rough version for testing? Define an agile sprint? Write stories to start?
	// TODO 0 a Analyse the need in terms of user interface: what to put in the REST WS?
	// TODO 0 b set a local git repository
	// TODO 0 c create unit test cases to answer those needs and prepare the refactoring
	// TODO 0 d order classes into packages, then Adapt this to a Controller Service Dao architecture with interfaces (to ease the reading and use of this app)
	// TODO 1 do not use local index and mongo db, but use dev platform
	// TODO 3 handle lack of results: many NPE
	// TODO 3 empty the collections and index before repopulating them
	// TODO 3 handle concurrency issues: while creating the training set, if collection is browsed from the solr admin GUI, it crashes
	// TODO 0 a decide where to store temporary training set and new index
	
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("Start cat application.");

		Categoriser.createTrainingSet(100);
//
//		Categoriser.indexTrainingSet();
//
//		Categoriser.categoriseIAViewSolrDocument("CO 273/632/2");

		
//		Categoriser.categoriseIAViewsFromSolr();
		
		System.out.println("Stop cat application.");
	}

	

}
