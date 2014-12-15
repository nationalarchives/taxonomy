package gov.tna.discovery.taxonomy.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoURI;

@Configuration
@EnableMongoRepositories
@ConfigurationProperties(prefix = "spring.data.mongodb")
@EnableConfigurationProperties
public class MongoConfiguration {

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

    /**
     * We need to use this mongoDbFactory with MappingMongoConvert to prevent a
     * "_class" field to be stored in mongo collection
     * 
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public @Bean MongoDbFactory mongoDbFactory() throws Exception {
	return new SimpleMongoDbFactory(new Mongo(host, port), database);
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
	// remove _class
	MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()),
		new MongoMappingContext());
	converter.setTypeMapper(new DefaultMongoTypeMapper(null));

	return new MongoTemplate(mongoDbFactory(), converter);
    }
}