/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer.TaxonomyTrainingSetAnalyser;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

@Configuration
@ConfigurationProperties(prefix = "lucene.index")
@EnableConfigurationProperties
public class TrainingSetConfiguration {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String trainingSetCollectionPath;

    private Boolean useStopFilter;

    private Boolean useSynonymFilter;

    private String maxShingleSize;
    private double iaViewMaxMergeSizeMB;
    private double iaViewMaxCachedMB;

    /**
     ************************* TSet Based
     */

    @ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
    public @Bean Directory trainingSetDirectory() throws IOException {
        logger.info("lucene trainingSet index location: {}", trainingSetCollectionPath );
        Directory fsDir = FSDirectory.open(Paths.get(trainingSetCollectionPath));
        return new NRTCachingDirectory(fsDir, iaViewMaxMergeSizeMB, iaViewMaxCachedMB);
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