package gov.tna.discovery.taxonomy.common.repository.mongo;

import org.springframework.stereotype.Repository;

/**
 * Repository dedicated to training document database. <br/>
 * dedicated to methods that are too complex to be provided with spring
 * repository<br/>
 * returns the number of removed elements
 * 
 * @author jcharlet
 */
@Repository
public interface TrainingDocumentRepositoryCustom {
    int deleteByCategory(String category);
}