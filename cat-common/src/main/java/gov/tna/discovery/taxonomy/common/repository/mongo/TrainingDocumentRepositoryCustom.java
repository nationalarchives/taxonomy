package gov.tna.discovery.taxonomy.common.repository.mongo;

/**
 * Repository dedicated to training document database. <br/>
 * dedicated to methods that are too complex to be provided with spring
 * repository<br/>
 * returns the number of removed elements
 * 
 * @author jcharlet
 */
public interface TrainingDocumentRepositoryCustom {
    int deleteByCategory(String category);
}