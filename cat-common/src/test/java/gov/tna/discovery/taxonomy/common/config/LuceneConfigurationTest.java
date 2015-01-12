package gov.tna.discovery.taxonomy.common.config;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IOContext.Context;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Value;
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
@EnableConfigurationProperties
@ComponentScan(basePackages = "gov.tna.discovery.taxonomy.common.repository.lucene")
@Import({ PropertiesConfiguration.class, LuceneConfiguration.class })
public class LuceneConfigurationTest {

    @Value("${lucene.index.trainingSetCollectionPath}")
    private String trainingSetCollectionPath;

    @Value("${lucene.index.iaviewCollectionPath}")
    private String iaviewCollectionPath;

    public @Bean Directory trainingSetDirectory() throws IOException {
	File file = new File(trainingSetCollectionPath);
	Directory directory = new SimpleFSDirectory(file);
	return new RAMDirectory(directory, new IOContext(Context.DEFAULT));
    }

    public @Bean Directory iaViewDirectory() throws IOException {
	File file = new File(iaviewCollectionPath);
	Directory directory = new SimpleFSDirectory(file);
	return new RAMDirectory(directory, new IOContext(Context.DEFAULT));
    }
}