package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.config.PropertiesConfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.github.fakemongo.Fongo;

@Configuration
@ComponentScan("gov.tna.discovery.taxonomy.common.repository.mongo")
@EnableMongoRepositories
@ConfigurationProperties(prefix = "spring.data.mongodb")
@EnableConfigurationProperties
@Import(value = PropertiesConfiguration.class)
public class MongoConfigurationTest {

    private String host;
    private Integer port;

    private String database;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public Integer getPort() {
	return port;
    }

    public void setPort(Integer port) {
	this.port = port;
    }

    public String getDatabase() {
	return database;
    }

    public void setDatabase(String database) {
	this.database = database;
    }

    public @Bean(name = "fongo") Fongo fongo() {
	return new Fongo("Taxonomy Memory Mongo Database");
    }

    public @Bean MongoDbFactory mongoDbFactory() throws Exception {
	return new SimpleMongoDbFactory(fongo().getMongo(), database);
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {

	// remove _class
	MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
	converter.setTypeMapper(new DefaultMongoTypeMapper(null));

	MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

	return mongoTemplate;
    }

}