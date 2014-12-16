package gov.tna.discovery.taxonomy.cli;

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
import org.apache.commons.cli.HelpFormatter;
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

    private static final String ACTION_HELP = "help";

    private static final String ACTION_INDEX = "index";

    private static final String ACTION_UPDATE = "update";

    private static final String ACTION_TEST_CATEGORISE_ALL = "testCategoriseAll";

    private static final String ACTION_TEST_CATEGORISE_SINGLE = "testCategoriseSingle";

    private static final String ACTION_CREATE_EVALUATION_DATA_SET = "createEvalDataSet";

    private static final String ACTION_CATEGORISE_EVALUATION_DATA_SET = "categoriseEvalDataSet";

    private static final String ACTION_GET_EVALUATION_REPORT = "getEvaluationReport";

    private static final String OPTION_CIAID = "ciaid";

    private static final String OPTION_CAT_DOC_REF = "catDocRef";

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

	Options options = registerAvailableActionsAndOptions();

	CommandLineParser parser = new BasicParser();
	CommandLine cmd = parser.parse(options, cliArgs);

	if (cliArgs.length > 0) {
	    logger.info("args: {} ", Arrays.asList(cliArgs).toString());
	} else {
	    logger.warn("no valid argument provided");
	}

	if (cmd.hasOption(ACTION_UPDATE)) {
	    logger.info("update (create if not existing) training set");
	    String categoryCiaid = null;
	    if (cmd.hasOption(OPTION_CIAID)) {
		categoryCiaid = cmd.getOptionValue(OPTION_CIAID);
	    }
	    Integer fixedLimitSize = null;
	    if (cmd.hasOption(OPTION_FIXED_SIZE)) {
		fixedLimitSize = Integer.valueOf(cmd.getOptionValue(OPTION_FIXED_SIZE));
	    }

	    updateTrainingSet(categoryCiaid, fixedLimitSize);
	}

	if (cmd.hasOption(ACTION_INDEX)) {
	    trainingSetService.indexTrainingSet();
	}

	if (cmd.hasOption(ACTION_TEST_CATEGORISE_SINGLE)) {
	    String catDocRef = cmd.getOptionValue(OPTION_CAT_DOC_REF);
	    testCategoriseSingle(catDocRef);
	}

	if (cmd.hasOption(ACTION_TEST_CATEGORISE_ALL)) {
	    categoriser.testCategoriseIAViewSolrIndex();
	}

	if (cmd.hasOption(ACTION_HELP)) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp("help", options);
	}

	logger.info("Stop cat CLI Runner.");
    }

    private Options registerAvailableActionsAndOptions() {
	// create Options object
	Options options = new Options();

	// add t option
	options.addOption(ACTION_UPDATE, false, "update (or create if not existing) training set Mongo collection");
	options.addOption(OPTION_CIAID, true, "on -" + ACTION_UPDATE + ": id of the category to work on");
	options.addOption(
		OPTION_FIXED_SIZE,
		true,
		"on -"
			+ ACTION_UPDATE
			+ ": allows to filter the records to add to the training set for a category by max number of elements INSTEAD OF by score. Specify in argument the number");

	options.addOption(ACTION_INDEX, false, "index training set from mongo collection");

	options.addOption(ACTION_TEST_CATEGORISE_SINGLE, false, "test the categorisation of one IAView Solr element");
	options.addOption(OPTION_CAT_DOC_REF, true, "on -" + ACTION_TEST_CATEGORISE_SINGLE
		+ ": cat doc ref of the element to work on");

	options.addOption(ACTION_TEST_CATEGORISE_ALL, false, "test the categorisation of the whole IAView Solr index");

	options.addOption(ACTION_HELP, false, "print help");
	return options;
    }

    private void testCategoriseSingle(String catDocRef) {
	if (StringUtils.isEmpty(catDocRef)) {
	    catDocRef = "CO 273/632/2";
	}

	categoriser.categoriseIAViewSolrDocument(catDocRef);
    }

    private void updateTrainingSet(String categoryCiaid, Integer fixedLimitSize) throws IOException, ParseException {
	if (StringUtils.isEmpty(categoryCiaid)) {
	    trainingSetService.createTrainingSet(null, fixedLimitSize);
	} else {
	    Category category = categoryRepository.findByCiaid(categoryCiaid);
	    trainingSetService.updateTrainingSetForCategory(category, null, fixedLimitSize);
	}
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
