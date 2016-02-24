/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo")
@ConfigurationProperties(prefix = "spring.data.mongodb")
@EnableConfigurationProperties
@Component
//TODO JCT ensure index when app starts
public class MongoConfiguration {

    private String host;
    private Integer port;
    private String database;

    @Value("${mongo.categories.hosts}")
    private String categoriesHosts;
    @Value("${mongo.categories.ports}")
    private String categoriesPorts;
    @Value("${mongo.categories.database}")
    private String categoriesDatabase;

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
    public
    @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(host, port), database);
    }

    public
    @Bean
    MongoTemplate mongoTemplate() throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()),
                new MongoMappingContext());
        // remove _class
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory(), converter);
    }

    public

    @Bean
    MongoTemplate categoriesMongoTemplate() throws Exception {
        MongoClient client;
        client = getMongoClientForCategoriesDatabase();

        MongoDbFactory categoriesMongoDbFactory = new SimpleMongoDbFactory(client, database);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(categoriesMongoDbFactory),
                new MongoMappingContext());
        // remove _class
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(categoriesMongoDbFactory, converter);
    }

    private MongoClient getMongoClientForCategoriesDatabase() throws UnknownHostException {
        MongoClient client;
        String[] splitHosts = categoriesHosts.split(",");
        String[] splitPorts = categoriesPorts.split(",");
        if (splitHosts.length > 1) {
            List<ServerAddress> listOfServerAddresses = new ArrayList<>();
            for (int i = 0; i < splitHosts.length; i++) {
                listOfServerAddresses.add(new ServerAddress(splitHosts[i], Integer.valueOf(splitPorts[i])));
            }
            client = new MongoClient(listOfServerAddresses);
        } else {
            client = new MongoClient(categoriesHosts,
                    Integer.valueOf(categoriesPorts));
        }
        return client;
    }
}