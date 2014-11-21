package gov.tna.discovery.taxonomy.config;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
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

    public @Bean Directory trainingSetDirectory() throws IOException {
	File file = new File(trainingSetCollectionPath);
	return new SimpleFSDirectory(file);
    }

    public @Bean Directory iaViewDirectory() throws IOException {
	File file = new File(iaviewCollectionPath);
	return new SimpleFSDirectory(file);
    }

    public @Bean IndexReader iaViewIndexReader() throws IOException {
	return DirectoryReader.open(iaViewDirectory());
    }

    public @Bean IndexReader trainingSetIndexReader() throws IOException {
	return DirectoryReader.open(trainingSetDirectory());
    }

    public @Bean SearcherManager iaviewSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, null);
	return new SearcherManager(iaViewDirectory(), null);
    }

    public @Bean SearcherManager trainingSetSearcherManager() throws IOException {
	// return new SearcherManager(iaviewIndexWriter(), true, null);
	return new SearcherManager(trainingSetDirectory(), null);
    }

    public @Bean Analyzer analyzer() {
	return new WhitespaceAnalyzer(Version.valueOf(version));
    }

    public @Bean IndexWriterConfig indexWriterConfig() {
	return new IndexWriterConfig(Version.valueOf(version), analyzer());
    }

    /**
     * Getters and Setters
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

    // FIXME how to make sure that readers see writer's modifications?

    // public @Bean IndexWriter trainingSetIndexWriter() throws IOException {
    // Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
    // IndexWriterConfig config = new
    // IndexWriterConfig(CatConstants.LUCENE_VERSION, analyzer);
    // File file = new File(CatConstants.TRAINING_INDEX);
    // SimpleFSDirectory index = new SimpleFSDirectory(file);
    //
    // return new IndexWriter(index, config);
    // }
    //
    // public @Bean IndexWriter iaviewIndexWriter() throws IOException {
    // Analyzer analyzer = new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION);
    // IndexWriterConfig config = new
    // IndexWriterConfig(CatConstants.LUCENE_VERSION, analyzer);
    // File file = new File(CatConstants.IAVIEW_INDEX);
    // SimpleFSDirectory index = new SimpleFSDirectory(file);
    //
    // return new IndexWriter(index, config);
    // }
}