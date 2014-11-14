package gov.tna.discovery.taxonomy;

import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cat {
    
    private static final Logger logger = LoggerFactory.getLogger(Searcher.class);

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
	Categoriser categoriser = new Categoriser();
	logger.debug("Start cat application.");

//	 categoriser.createTrainingSet(100);
	
//	 categoriser.indexTrainingSet();
	
//	 categoriser.categoriseIAViewSolrDocument("CO 273/632/2");

	categoriser.categoriseIAViewsFromSolr();

	logger.debug("Stop cat application.");
    }

}
