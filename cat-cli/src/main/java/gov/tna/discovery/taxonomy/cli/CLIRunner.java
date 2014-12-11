package gov.tna.discovery.taxonomy.cli;

import gov.tna.discovery.taxonomy.CLIApplication;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CLIRunner implements CommandLineRunner {

    private static final String OPTION_TEST_CATEGORISE_ALL = "testCategoriseAll";

    private static final String OPTION_TEST_CATEGORISE_SINGLE = "testCategoriseSingle";

    private static final String OPTION_INDEX = "index";

    private static final String OPTION_UPDATE = "update";

    private static final String OPTION_FIXED_SIZE = "fixedSize";

    private static final Logger logger = LoggerFactory.getLogger(CLIRunner.class);

    @Value("${lucene.index.iaviewCollectionPath}")
    private String iaviewCollectionPath;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Autowired
    CategoriserService categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    CategoryRepository categoryRepository;

    public void run(String... args) throws IOException, ParseException, org.apache.commons.cli.ParseException {

	logger.info("Start cat CLI Runner.");
	logger.info("mongo host: {}", host);
	logger.info("mongo solr Index path: {}", iaviewCollectionPath);

	final String[] cliArgs = filterInputToGetOnlyCliArguments(args);

	// create Options object
	Options options = new Options();

	// add t option
	options.addOption(OPTION_UPDATE, false, "update (create if not existing) training set");
	options.addOption(OPTION_INDEX, false, "index training set");
	options.addOption(OPTION_TEST_CATEGORISE_SINGLE, false, "test the categorisation of one IAView Solr element");
	options.addOption(OPTION_TEST_CATEGORISE_ALL, false, "test the categorisation of the whole IAView Solr index");
	options.addOption(
		OPTION_FIXED_SIZE,
		false,
		"allows to filter the records to add to the training set for a category by max number of elements INSTEAD OF by score. Specify in argument the number");

	CommandLineParser parser = new BasicParser();
	CommandLine cmd = parser.parse(options, cliArgs);

	if (cliArgs.length > 0) {
	    logger.info("args: {} ", Arrays.asList(cliArgs).toString());
	} else {
	    logger.warn("no valid argument provided");
	}

	if (cmd.hasOption(OPTION_UPDATE)) {
	    logger.info("update (create if not existing) training set");
	    String categoryCiaid = cmd.getOptionValue(OPTION_UPDATE);
	    // FIXME refactor if conditions with design pattern
	    if (cmd.hasOption(OPTION_FIXED_SIZE)) {
		Integer fixedLimitSize = Integer.valueOf(cmd.getOptionValue(OPTION_FIXED_SIZE));
		if (StringUtils.isEmpty(categoryCiaid)) {
		    trainingSetService.createTrainingSet(null, fixedLimitSize);
		} else {
		    Category category = categoryRepository.findByCiaid(categoryCiaid);
		    trainingSetService.updateTrainingSetForCategory(category, null, fixedLimitSize);
		}
	    } else {
		if (StringUtils.isEmpty(categoryCiaid)) {
		    trainingSetService.createTrainingSet(null, null);
		} else {
		    Category category = categoryRepository.findByCiaid(categoryCiaid);
		    trainingSetService.updateTrainingSetForCategory(category, null, null);
		}
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

    private String[] filterInputToGetOnlyCliArguments(String[] args) {
	List<String> listOfArgs = new ArrayList<String>();
	for (int i = 0; i < args.length; i++) {
	    String argument = args[i];
	    if (!argument.startsWith("-D") && !argument.startsWith("--")) {
		listOfArgs.add(argument);
	    }
	}
	return listOfArgs.toArray(new String[listOfArgs.size()]);
    }
}
