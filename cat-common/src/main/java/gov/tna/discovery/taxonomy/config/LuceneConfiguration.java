package gov.tna.discovery.taxonomy.config;

import gov.tna.discovery.taxonomy.repository.lucene.analyzer.TaxonomyGeneralIndexAnalyzer;
import gov.tna.discovery.taxonomy.repository.lucene.analyzer.TaxonomyGeneralQueryAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.fst.FST;
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
class LuceneConfiguration {
    private String iaviewCollectionPath;
    private String trainingSetCollectionPath;
    private String version;

    /**
     ************************* Directories
     */

    public @Bean Directory trainingSetDirectory() throws IOException {
	File file = new File(trainingSetCollectionPath);
	return new SimpleFSDirectory(file);
    }

    public @Bean Directory iaViewDirectory() throws IOException {
	File file = new File(iaviewCollectionPath);
	return new SimpleFSDirectory(file);
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
	return new SearcherManager(iaViewDirectory(), null);
    }

    public @Bean SearcherManager trainingSetSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, null);
	return new SearcherManager(trainingSetDirectory(), null);
    }

    /**
     ************************* Analyzers
     */

    public @Bean Analyzer indexAnalyser() {
	return new TaxonomyGeneralIndexAnalyzer(Version.valueOf(version));
    }

    public @Bean Analyzer queryAnalyser() {
	return new TaxonomyGeneralQueryAnalyzer(Version.valueOf(version));
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