package gov.tna.discovery.taxonomy.repository.mongo;

import org.springframework.stereotype.Repository;

/**
 * Repository dedicated to training document database. <br/>
 * dedicated to methods that are too complex to be provided with spring
 * repository
 * 
 * @author jcharlet
 *
 */
@Repository
public interface TrainingDocumentRepositoryCustom {
    void deleteByCategory(String category);
}