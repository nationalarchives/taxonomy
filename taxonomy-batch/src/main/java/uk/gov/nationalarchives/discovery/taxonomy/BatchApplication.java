/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.impl.LegacySystemRepositoryImpl;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("application.yml")
public class BatchApplication {

    public static void main(String[] args) throws IOException, ParseException {
	SpringApplication.run(BatchApplication.class, args);
    }

    /**
     * This bean is used in EvaluationService but dedicated method is not useful
     * for the batch
     * 
     * @return
     */
    @Bean
    LegacySystemRepositoryImpl legacySystemRepository() {
	return null;
    };
}
