package gov.tna.discovery.taxonomy.common.config;

import gov.tna.discovery.taxonomy.common.config.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@ComponentScan(basePackages = "gov.tna.discovery.taxonomy.common.repository.lucene")
@Import({PropertiesConfiguration.class,LuceneConfiguration.class})
public class LuceneConfigurationTest {

}