package gov.tna.discovery.taxonomy;

import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class cat {
    
    private static final Logger logger = LoggerFactory.getLogger(Searcher.class);

	// TODO 1 do not use local index and mongo db, but use dev platform
	// TODO 3 handle lack of results: many NPE
	// TODO 3 empty the collections and index before repopulating them
	// TODO 3 handle concurrency issues: while creating the training set, if collection is browsed from the solr admin GUI, it crashes
	
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
