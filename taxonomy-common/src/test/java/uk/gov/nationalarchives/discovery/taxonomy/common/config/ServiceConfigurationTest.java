/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.impl.LegacySystemRepositoryImpl;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "uk.gov.nationalarchives.discovery.taxonomy.common.service",
	"uk.gov.nationalarchives.discovery.taxonomy.common.repository" })
@Import(value = { LuceneConfigurationTest.class, MongoConfigurationTest.class, AsyncConfiguration.class })
public class ServiceConfigurationTest {

    /**
     * This bean is used in EvaluationService but it raises requests to distant
     * service that we don't want to mess in unit test cases
     * 
     * @return
     */
    @Bean
    LegacySystemRepositoryImpl legacySystemRepository() {
	return null;
    };
}