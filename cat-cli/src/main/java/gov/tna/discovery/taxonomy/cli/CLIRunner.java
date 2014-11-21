package gov.tna.discovery.taxonomy.cli;

import java.io.IOException;

import gov.tna.discovery.taxonomy.CLIApplication;
import gov.tna.discovery.taxonomy.service.Categoriser;
import gov.tna.discovery.taxonomy.service.TrainingSetService;

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

    public void run(String... args) throws IOException, ParseException {

	logger.info("Start cat CLI Runner.");

	// trainingSetService.createTrainingSet(null);

	// trainingSetService.indexTrainingSet();

	// categoriser.categoriseIAViewSolrDocument("CO 273/632/2");

	categoriser.testCategoriseIAViewSolrIndex();

	logger.info("Stop cat CLI Runner.");
    }
}
