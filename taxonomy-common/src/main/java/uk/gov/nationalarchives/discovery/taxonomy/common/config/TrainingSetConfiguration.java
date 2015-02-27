package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.TaxonomyTrainingSetAnalyser;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;

@Configuration
@ConfigurationProperties(prefix = "lucene.index")
@EnableConfigurationProperties
public class TrainingSetConfiguration {

    private String trainingSetCollectionPath;

    private Boolean useStopFilter;

    private Boolean useSynonymFilter;

    private String maxShingleSize;

    /**
     ************************* TSet Based
     */

    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean Directory trainingSetDirectory() throws IOException {
	// TODO TSETBASED Use MMapDirectory to be faster. is used on solr
	// Server
	File file = new File(trainingSetCollectionPath);
	return new SimpleFSDirectory(file);
    }

    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean ReaderManager trainingSetReaderManager() throws IOException {
	return new ReaderManager(trainingSetDirectory());
    }

    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean IndexReader trainingSetIndexReader() throws IOException {
	return DirectoryReader.open(trainingSetDirectory());
    }

    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean SearcherManager trainingSetSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, null);
	return new SearcherManager(trainingSetDirectory(), null);
    }

    /**
     * Analyzer dedicated to indexing elements into training set and comparing
     * them with document to categorise
     * 
     * @return
     * @throws ParseException
     * @throws NumberFormatException
     */
    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean Analyzer trainingSetAnalyser(StopFilterFactory stopFilterFactory,
	    SynonymFilterFactory synonymFilterFactory) throws NumberFormatException, ParseException {

	StopFilterFactory stopFilterFactoryForTSet = null;
	if (useStopFilter) {
	    stopFilterFactoryForTSet = stopFilterFactory;
	}
	SynonymFilterFactory synonymFilterFactoryForTSet = null;
	if (useSynonymFilter) {
	    synonymFilterFactoryForTSet = synonymFilterFactory;
	}
	return new TaxonomyTrainingSetAnalyser(stopFilterFactoryForTSet, synonymFilterFactoryForTSet,
		Integer.valueOf(maxShingleSize));
    }

    /**
     * Necessary to provide an empty trainingSetService if using query based
     * categorisation profile, otherwise the service being autowired in WS and
     * CLI makes the application fail to start
     * 
     * @return
     */
    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
    public @Bean TrainingSetService trainingSetService() {
	return null;
    }

    public void setTrainingSetCollectionPath(String trainingSetCollectionPath) {
	this.trainingSetCollectionPath = trainingSetCollectionPath;
    }

    public void setUseStopFilter(Boolean useStopFilter) {
	this.useStopFilter = useStopFilter;
    }

    public void setUseSynonymFilter(Boolean useSynonymFilter) {
	this.useSynonymFilter = useSynonymFilter;
    }

    public void setMaxShingleSize(String maxShingleSize) {
	this.maxShingleSize = maxShingleSize;
    }

}