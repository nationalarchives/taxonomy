package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

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
     */
    public void updateTrainingSetForCategory(Category category, Float fixedLimitScore);

    /**
     * 
     * @param fixedLimitScore
     * @throws IOException
     * @throws ParseException
     */
    public void createTrainingSet(Float fixedLimitScore) throws IOException, ParseException;

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

}