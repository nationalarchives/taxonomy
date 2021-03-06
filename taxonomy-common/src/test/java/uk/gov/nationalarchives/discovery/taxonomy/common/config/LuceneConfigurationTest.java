/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IOContext.Context;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Configuration dedicated to Lucene:<br/>
 * provides all necessary beans (directory, reader, etc)
 * 
 * @author jcharlet
 *
 */
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene")
@Import({ PropertiesConfiguration.class, LuceneIAViewConfiguration.class, TrainingSetConfiguration.class })
public class LuceneConfigurationTest {

    @Value("${lucene.index.trainingSetCollectionPath}")
    private String trainingSetCollectionPath;

    @Value("${lucene.index.iaviewCollectionPath}")
    private String iaviewCollectionPath;

    public @Bean Directory trainingSetDirectory() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(trainingSetCollectionPath));
        return new RAMDirectory(directory, new IOContext(Context.DEFAULT));
    }

    public @Bean Directory iaViewDirectory() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(iaviewCollectionPath));
        return new RAMDirectory(directory, new IOContext(Context.DEFAULT));
    }
}