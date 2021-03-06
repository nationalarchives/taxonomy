/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Service dedicated to the management of the training set
 * 
 * @author jcharlet
 *
 */
public interface TrainingSetService {

    /**
     * Update training set for a category:<br/>
     * <ul>
     * <li>search ia view solr index from category query</li>
     * <li>add all documents found to the training set mongo db</li>
     * </ul>
     * 
     * @param category
     * @param fixedLimitScore
     * @param fixedLimitSize
     */
    public void updateTrainingSetForCategory(Category category, Float fixedLimitScore, Integer fixedLimitSize);

    /**
     * Update training set for a category:<br/>
     * <ul>
     * <li>search ia view solr index from category query</li>
     * <li>add all documents found to the training set mongo db</li>
     * </ul>
     * 
     * @param categoryCiaid
     * @param fixedLimitScore
     * @param fixedLimitSize
     */
    public void updateTrainingSetForCategory(String categoryCiaid, Integer fixedLimitScore, Integer fixedLimitSize);

    /**
     * 
     * @param fixedLimitScore
     * @throws IOException
     * @throws ParseException
     * @param fixedLimitSize
     */
    public void createTrainingSet(Float fixedLimitScore, Integer fixedLimitSize) throws IOException, ParseException;

    /**
     * index training Set mongo db for a category<br/>
     * delete records for a category in the index then add records from the
     * trainingset db<br/>
     * executes both tasks at the same time to use the same indexWriter
     * 
     * @param category
     */
    public void deleteAndUpdateTraingSetIndexForCategory(Category category);

    /**
     * build index of trainingDocument from mongo db trainingset<br/>
     * remove punctuation from Description, title
     * 
     * @throws IOException
     */
    public void indexTrainingSet();

    /**
     * delete training document from mongoDb by category
     * 
     * @param ttl
     */
    public void deleteMongoTrainingDocumentByCategory(String category);

    void updateCategoriesScores(int minNumber, int maxNumber);

    /**
     * publish update on a category:
     * <ul>
     * <li>lock category</li>
     * <li>update training set mongo db for category</li>
     * <li>index training set for category</li>
     * </ul>
     * 
     * @param ciaid
     */
    public void publishUpdateOnCategory(String ciaid);

}