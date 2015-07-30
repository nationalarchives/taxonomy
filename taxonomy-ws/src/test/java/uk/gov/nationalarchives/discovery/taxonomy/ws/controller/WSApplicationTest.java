/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.AsyncConfiguration;
import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.ws")
@EnableAutoConfiguration
@PropertySource("application.yml")
@Import(value = { ServiceConfigurationTest.class, AsyncConfiguration.class })
public class WSApplicationTest {

    public static void main(String[] args) throws Exception {
	SpringApplication.run(WSApplicationTest.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

}
