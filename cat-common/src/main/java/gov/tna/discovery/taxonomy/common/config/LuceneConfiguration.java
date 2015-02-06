package gov.tna.discovery.taxonomy.common.config;

import gov.tna.discovery.taxonomy.common.repository.lucene.analyzer.IAViewTextRefAnalyser;
import gov.tna.discovery.taxonomy.common.repository.lucene.analyzer.TaxonomyTrainingSetAnalyser;
import gov.tna.discovery.taxonomy.common.repository.lucene.analyzer.WhiteSpaceAnalyserWIthPIG;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration dedicated to Lucene:<br/>
 * provides all necessary beans (directory, reader, etc)
 * 
 * @author jcharlet
 *
 */
@Configuration
@ConfigurationProperties(prefix = "lucene.index")
@EnableConfigurationProperties
public class LuceneConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LuceneConfiguration.class);

    private String iaviewCollectionPath;
    private String trainingSetCollectionPath;
    private String version;
    private double iaViewMaxMergeSizeMB;
    private double iaViewMaxCachedMB;

    @Value("${lucene.index.maxShingleSize}")
    private String maxShingleSize;

    @Value("${lucene.index.useStopFilter}")
    private Boolean useStopFilter;

    @Value("${lucene.index.useSynonymFilter}")
    private Boolean useSynonymFilter;

    /**
     ************************* Directories
     */

    public @Bean Directory trainingSetDirectory() throws IOException {
	// FIXME Use MMapDirectory to be faster. is used on solr Server
	File file = new File(trainingSetCollectionPath);
	return new SimpleFSDirectory(file);
    }

    public @Bean Directory iaViewDirectory() throws IOException {
	Directory fsDir = FSDirectory.open(new File(iaviewCollectionPath));
	return new NRTCachingDirectory(fsDir, iaViewMaxMergeSizeMB, iaViewMaxCachedMB);
    }

    /**
     ************************* Readers
     */

    public @Bean IndexReader iaViewIndexReader() throws IOException {
	return DirectoryReader.open(iaViewDirectory());
    }

    public @Bean ReaderManager trainingSetReaderManager() throws IOException {
	return new ReaderManager(trainingSetDirectory());
    }

    public @Bean IndexReader trainingSetIndexReader() throws IOException {
	return DirectoryReader.open(trainingSetDirectory());
    }

    /**
     ************************* Searchers
     */

    public @Bean SearcherManager iaviewSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, lnull);
	// FIXME how to make sure that updates on IAVIew are taken into account
	// and viewable on GUI? KO ATM
	return new SearcherManager(iaViewDirectory(), null);
    }

    public @Bean SearcherManager trainingSetSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, null);
	return new SearcherManager(trainingSetDirectory(), null);
    }

    /**
     ************************* FilterFactories and Analyzers
     */

    public @Bean StopFilterFactory stopFilterFactory() {
	Map<String, String> stopFilterArgs = new HashMap<String, String>();
	stopFilterArgs.put("words", "stopwords.txt");
	stopFilterArgs.put("enablePositionIncrements", "true");
	stopFilterArgs.put("luceneMatchVersion", version);

	StopFilterFactory stopFilterFactory = new StopFilterFactory(stopFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    stopFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".stopFilterFactory: an error occured while creating the Filter factory: {}", e.getMessage());
	}
	return stopFilterFactory;
    }

    public @Bean SynonymFilterFactory synonymFilterFactory() {
	Map<String, String> synonymFilterArgs = new HashMap<String, String>();
	synonymFilterArgs.put("synonyms", "synonyms.txt");
	synonymFilterArgs.put("expand", "true");
	synonymFilterArgs.put("ignoreCase", "true");
	synonymFilterArgs.put("luceneMatchVersion", version);
	SynonymFilterFactory synonymFilterFactory = new SynonymFilterFactory(synonymFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    synonymFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".synonymFilterFactory: an error occured while creating the Filter factory: {}",
		    e.getMessage());
	}
	return synonymFilterFactory;

    }

    public @Bean WordDelimiterFilterFactory wordDelimiterFilterFactory() {
	Map<String, String> wordDelimiterFilterArgs = new HashMap<String, String>();
	wordDelimiterFilterArgs.put("preserveOriginal", "1");
	wordDelimiterFilterArgs.put("generateWordParts", "1");
	wordDelimiterFilterArgs.put("catenateWords", "1");
	wordDelimiterFilterArgs.put("luceneMatchVersion", version);
	WordDelimiterFilterFactory wordDelimiterFilterFactory = new WordDelimiterFilterFactory(wordDelimiterFilterArgs);

	try {
	    ResourceLoader loader = new ClasspathResourceLoader(getClass());
	    wordDelimiterFilterFactory.inform(loader);
	} catch (IOException e) {
	    logger.error(".wordDelimiterFilterFactory: an error occured while creating the Filter factory: {}",
		    e.getMessage());
	}
	return wordDelimiterFilterFactory;

    }

    /**
     * Analyzer dedicated to indexing elements into training set and comparing
     * them with document to categorise
     * 
     * @return
     * @throws ParseException
     * @throws NumberFormatException
     */
    public @Bean Analyzer trainingSetAnalyser() throws NumberFormatException, ParseException {
	StopFilterFactory stopFilterFactory = null;
	if (useStopFilter) {
	    stopFilterFactory = stopFilterFactory();
	}
	SynonymFilterFactory synonymFilterFactory = null;
	if (useSynonymFilter) {
	    synonymFilterFactory = synonymFilterFactory();
	}
	return new TaxonomyTrainingSetAnalyser(stopFilterFactory, synonymFilterFactory, Integer.valueOf(maxShingleSize));
    }

    /**
     * Analyzer dedicated to running the category queries and finding documents
     * in IAView Index
     * 
     * @return
     * @throws ParseException
     */
    public @Bean Analyzer iaViewSearchAnalyser() throws ParseException {
	Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
	IAViewTextRefAnalyser textRefAnalyser = new IAViewTextRefAnalyser(Version.parseLeniently(version),
		wordDelimiterFilterFactory());
	textRefAnalyser.setPositionIncrementGap(100);
	analyzerPerField.put("CATDOCREF", textRefAnalyser);

	WhiteSpaceAnalyserWIthPIG textTaxAnalyser = new WhiteSpaceAnalyserWIthPIG();
	textTaxAnalyser.setPositionIncrementGap(100);

	PerFieldAnalyzerWrapper iaViewIndexAnalyser = new PerFieldAnalyzerWrapper(textTaxAnalyser, analyzerPerField);

	return iaViewIndexAnalyser;
    }

    /**
     * Analyzer dedicated to indexing a document to categorise in memory
     * database
     * 
     * @return
     * @throws ParseException
     */
    public @Bean Analyzer iaViewIndexAnalyser() throws ParseException {
	return iaViewSearchAnalyser();
    }

    /**
     ************************* Getters and Setters
     */

    public String getIaviewCollectionPath() {
	return iaviewCollectionPath;
    }

    public void setIaviewCollectionPath(String iaviewCollectionPath) {
	this.iaviewCollectionPath = iaviewCollectionPath;
    }

    public String getTrainingSetCollectionPath() {
	return trainingSetCollectionPath;
    }

    public void setTrainingSetCollectionPath(String trainingSetCollectionPath) {
	this.trainingSetCollectionPath = trainingSetCollectionPath;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

}