package gov.tna.discovery.taxonomy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;

@ComponentScan
@Configuration
@EnableMongoRepositories
class MongoConfiguration  {

    public @Bean Mongo mongo() throws Exception {
        return new Mongo(CatConstants.MONGO_HOST);
    }

    public @Bean MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), CatConstants.MONGO_TAXONOMY_DB);
    }

}