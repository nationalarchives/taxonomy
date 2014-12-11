package gov.tna.discovery.taxonomy.common.config;

import gov.tna.discovery.taxonomy.common.config.PropertiesConfiguration;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IOContext.Context;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
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
@Import({ PropertiesConfiguration.class, LuceneConfiguration.class })
public class LuceneConfigurationTest {

    private String trainingSetCollectionPath;

    public @Bean Directory trainingSetDirectory() throws IOException {
	File file = new File(trainingSetCollectionPath);
	Directory directory = new SimpleFSDirectory(file);
	return new RAMDirectory(directory, new IOContext(Context.DEFAULT));
    }
}