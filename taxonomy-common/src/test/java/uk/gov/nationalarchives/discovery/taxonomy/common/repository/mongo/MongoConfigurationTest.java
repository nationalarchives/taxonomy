/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.PropertiesConfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.github.fakemongo.Fongo;

@Configuration
@ComponentScan("uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo")
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

    public @Bean MongoTemplate mongoTemplate() throws Exception {
	return new MongoTemplate(fongo().getMongo(), database);
    }

}