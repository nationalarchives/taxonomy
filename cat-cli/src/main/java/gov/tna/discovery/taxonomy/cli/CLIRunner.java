package gov.tna.discovery.taxonomy.cli;

import gov.tna.discovery.taxonomy.CLIApplication;
import gov.tna.discovery.taxonomy.service.Categoriser;
import gov.tna.discovery.taxonomy.service.TrainingSetService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CLIRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CLIApplication.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    public void run(String... args) throws IOException, ParseException, org.apache.commons.cli.ParseException {

	logger.info("Start cat CLI Runner.");

	// create Options object
	Options options = new Options();

	// add t option
	options.addOption("create", false, "create training set");
	options.addOption("index", false, "index training set");
	options.addOption("testCategoriseSingle", false, "test the categorisation of one IAView Solr element");
	options.addOption("testCategoriseAll", false, "test the categorisation of the whole IAView Solr index");

	CommandLineParser parser = new BasicParser();
	CommandLine cmd = parser.parse(options, args);

	if (args != null) {
	    List<String> listOfArgs = Arrays.asList(args);
	    logger.info("args: {} ", listOfArgs.toString());

	}

	if (cmd.hasOption("create training set")) {
	    logger.info("testCategoriseAll");
	    trainingSetService.createTrainingSet(null);
	}

	if (cmd.hasOption("index")) {
	    logger.info("index training set");
	    trainingSetService.indexTrainingSet();
	}

	if (cmd.hasOption("testCategoriseSingle")) {
	    logger.info("testCategoriseSingle on document: {} ", "");
	    categoriser.categoriseIAViewSolrDocument("CO 273/632/2");
	}

	if (cmd.hasOption("testCategoriseAll")) {
	    logger.info("test the categorisation of the whole IAView Solr index");
	    categoriser.testCategoriseIAViewSolrIndex();
	}

	logger.info("Stop cat CLI Runner.");
    }
}
