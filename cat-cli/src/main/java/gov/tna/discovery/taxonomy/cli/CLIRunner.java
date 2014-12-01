package gov.tna.discovery.taxonomy.cli;

import gov.tna.discovery.taxonomy.CLIApplication;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
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
import org.springframework.util.StringUtils;

@Component
public class CLIRunner implements CommandLineRunner {

    private static final String OPTION_TEST_CATEGORISE_ALL = "testCategoriseAll";

    private static final String OPTION_TEST_CATEGORISE_SINGLE = "testCategoriseSingle";

    private static final String OPTION_INDEX = "index";

    private static final String OPTION_UPDATE = "update";

    private static final Logger logger = LoggerFactory.getLogger(CLIApplication.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    CategoryRepository categoryRepository;

    public void run(String... args) throws IOException, ParseException, org.apache.commons.cli.ParseException {

	logger.info("Start cat CLI Runner.");

	// create Options object
	Options options = new Options();

	// add t option
	options.addOption(OPTION_UPDATE, false, "update (create if not existing) training set");
	options.addOption(OPTION_INDEX, false, "index training set");
	options.addOption(OPTION_TEST_CATEGORISE_SINGLE, false, "test the categorisation of one IAView Solr element");
	options.addOption(OPTION_TEST_CATEGORISE_ALL, false, "test the categorisation of the whole IAView Solr index");

	CommandLineParser parser = new BasicParser();
	CommandLine cmd = parser.parse(options, args);

	if (args != null) {
	    List<String> listOfArgs = Arrays.asList(args);
	    logger.info("args: {} ", listOfArgs.toString());

	}

	if (cmd.hasOption(OPTION_UPDATE)) {
	    logger.info("update (create if not existing) training set");
	    String categoryCiaid = cmd.getOptionValue(OPTION_UPDATE);
	    if (StringUtils.isEmpty(categoryCiaid)) {
		trainingSetService.createTrainingSet(null);
	    } else {
		Category category = categoryRepository.findByCiaid(categoryCiaid);
		trainingSetService.updateTrainingSetForCategory(category, null);
	    }
	}

	if (cmd.hasOption(OPTION_INDEX)) {
	    logger.info("index training set");
	    trainingSetService.indexTrainingSet();
	}

	if (cmd.hasOption(OPTION_TEST_CATEGORISE_SINGLE)) {
	    logger.info("testCategoriseSingle on document: {} ", "");
	    categoriser.categoriseIAViewSolrDocument("CO 273/632/2");
	}

	if (cmd.hasOption(OPTION_TEST_CATEGORISE_ALL)) {
	    logger.info("test the categorisation of the whole IAView Solr index");
	    categoriser.testCategoriseIAViewSolrIndex();
	}

	logger.info("Stop cat CLI Runner.");
    }
}
