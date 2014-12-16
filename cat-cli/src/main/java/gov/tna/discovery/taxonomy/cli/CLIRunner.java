package gov.tna.discovery.taxonomy.cli;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.CategoryEvaluationResult;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.EvaluationReport;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.EvaluationService;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CLIRunner implements CommandLineRunner {

    private static final String ACTION_HELP = "help";

    private static final String ACTION_INDEX = "TSETindex";

    private static final String ACTION_UPDATE = "TSETupdate";
    private static final String OPTION_CIAID = "TSETciaid";
    private static final String OPTION_FIXED_SIZE = "TSETfixedSize";

    private static final String ACTION_UPDATE_CATEGORIES_SCORES = "TSETupdateCategoriesScores";
    private static final String OPTION_MIN_ELEMENTS_PER_CAT = "TSETminElements";
    private static final String OPTION_MAX_ELEMENTS_PER_CAT = "TSETmaxElements";

    private static final String ACTION_TEST_CATEGORISE_ALL = "CATtestCategoriseAll";

    private static final String ACTION_TEST_CATEGORISE_SINGLE = "CATtestCategoriseSingle";
    private static final String OPTION_CAT_DOC_REF = "CATcatDocRef";

    private static final String ACTION_CREATE_EVALUATION_DATA_SET = "EVALcreateEvalDataSet";
    private static final String OPTION_MINIMUM_SIZE_PER_CATEGORY = "EVALminimumSizePerCat";

    private static final String ACTION_CATEGORISE_EVALUATION_DATA_SET = "EVALcategoriseEvalDataSet";

    private static final String ACTION_GET_EVALUATION_REPORT = "EVALgetEvaluationReport";
    private static final String OPTION_EVALUATION_REPORT_COMMENTS = "EVALcomments";

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
    EvaluationService evaluationService;

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
	    logger.info("Stop cat CLI Runner.");
	    return;
	}

	/**
	 * Management of training Set
	 */

	if (cmd.hasOption(ACTION_UPDATE_CATEGORIES_SCORES)) {
	    logger.info("update categories scores ");
	    String minNumber = cmd.getOptionValue(OPTION_MIN_ELEMENTS_PER_CAT);
	    String maxNumber = cmd.getOptionValue(OPTION_MAX_ELEMENTS_PER_CAT);
	    trainingSetService.updateCategoriesScores(Integer.valueOf(minNumber), Integer.valueOf(maxNumber));
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

	/**
	 * Run Categorisation
	 */

	if (cmd.hasOption(ACTION_TEST_CATEGORISE_SINGLE)) {
	    String catDocRef = cmd.getOptionValue(OPTION_CAT_DOC_REF);
	    testCategoriseSingle(catDocRef);
	}

	if (cmd.hasOption(ACTION_TEST_CATEGORISE_ALL)) {
	    categoriser.testCategoriseIAViewSolrIndex();
	}

	/**
	 * Evaluate Categorisation System
	 */

	if (cmd.hasOption(ACTION_CREATE_EVALUATION_DATA_SET)) {
	    Integer minimumSizePerCat = null;
	    if (cmd.hasOption(OPTION_MINIMUM_SIZE_PER_CATEGORY)) {
		minimumSizePerCat = Integer.valueOf(cmd.getOptionValue(OPTION_MINIMUM_SIZE_PER_CATEGORY));
	    }
	    evaluationService.createEvaluationTestDataset(minimumSizePerCat);
	}

	if (cmd.hasOption(ACTION_CATEGORISE_EVALUATION_DATA_SET)) {
	    evaluationService.runCategorisationOnTestDataSet();
	}

	if (cmd.hasOption(ACTION_GET_EVALUATION_REPORT)) {
	    String comments = null;
	    if (cmd.hasOption(OPTION_EVALUATION_REPORT_COMMENTS)) {
		comments = cmd.getOptionValue(OPTION_EVALUATION_REPORT_COMMENTS);
	    }
	    getEvaluationReport(comments);
	}

	/**
	 * get Help
	 */

	if (cmd.hasOption(ACTION_HELP)) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.setWidth(150);
	    formatter.printHelp("help", options);
	}

	logger.info("Stop cat CLI Runner.");
    }

    private void getEvaluationReport(String comments) throws JsonProcessingException {
	EvaluationReport report = evaluationService.getEvaluationReport(comments);
	ObjectMapper mapper = new ObjectMapper();
	logger.info("Report: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));

	List<String> listOfCategoriesNotFound = new ArrayList<String>();
	List<String> listOfCategoriesWithBadAccuracy = new ArrayList<String>();
	List<String> listOfCategoriesWithBadRecall = new ArrayList<String>();
	for (CategoryEvaluationResult categoryEvaluationResult : report.getResults()) {
	    if (!categoryEvaluationResult.isFoundInTDocCat() && !categoryEvaluationResult.isFoundInTDocLegacyCat()) {
		listOfCategoriesNotFound.add(categoryEvaluationResult.getCategory());
		logger.debug(
			"Category '{}' from current System was not found in any document among legacy and current cats",
			categoryEvaluationResult.getCategory());
	    }
	    if (categoryEvaluationResult.getAccuracy() < 0.5d) {
		listOfCategoriesWithBadAccuracy.add(categoryEvaluationResult.getCategory());
		logger.debug("Category '{}' has bad accuracy: {}", categoryEvaluationResult.getCategory(),
			categoryEvaluationResult.getAccuracy());

	    }
	    if (categoryEvaluationResult.getRecall() < 0.5d) {
		listOfCategoriesWithBadRecall.add(categoryEvaluationResult.getCategory());
		logger.debug("Category '{}' has bad recall: {}", categoryEvaluationResult.getCategory(),
			categoryEvaluationResult.getRecall());

	    }
	}
	logger.info("{} categories were processed", report.getResults().size());
	logger.info("{} categories from current system were not found anywhere: {}", listOfCategoriesNotFound.size(),
		Arrays.toString(listOfCategoriesNotFound.toArray()));
	logger.info("{} categories have accuracy < 0.5: {}", listOfCategoriesWithBadAccuracy.size(),
		Arrays.toString(listOfCategoriesWithBadAccuracy.toArray()));
	logger.info("{} categories have recall < 0.5: {}", listOfCategoriesWithBadRecall.size(),
		Arrays.toString(listOfCategoriesWithBadRecall.toArray()));
	logger.info("");
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

	options.addOption(ACTION_CREATE_EVALUATION_DATA_SET, false, "Create the evaluation data set from Legacy System");
	options.addOption(OPTION_MINIMUM_SIZE_PER_CATEGORY, true, "on -" + ACTION_CREATE_EVALUATION_DATA_SET
		+ ": set the minimum number of tes documents to provide for every category");

	options.addOption(ACTION_CATEGORISE_EVALUATION_DATA_SET, false,
		"categorise the evaluation data set with current system");

	options.addOption(ACTION_GET_EVALUATION_REPORT, false,
		"get evaluation report from current evaluation data set containing categories from legacy and current system");
	options.addOption(OPTION_EVALUATION_REPORT_COMMENTS, true, "on -" + ACTION_GET_EVALUATION_REPORT
		+ ": provide comments for evaluation report (describe configuration used, etc)");

	options.addOption(ACTION_UPDATE_CATEGORIES_SCORES, false,
		"Set the score for every category so it represents the best the whole index (using log function)");
	options.addOption(OPTION_MIN_ELEMENTS_PER_CAT, true, "on -" + ACTION_UPDATE_CATEGORIES_SCORES
		+ " Minimum number of elements to retrieve for categories");
	options.addOption(OPTION_MAX_ELEMENTS_PER_CAT, true, "on -" + ACTION_UPDATE_CATEGORIES_SCORES
		+ " Maximum number of elements to retrieve for categories");

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
