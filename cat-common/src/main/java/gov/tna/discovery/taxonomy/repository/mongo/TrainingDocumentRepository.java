package gov.tna.discovery.taxonomy.repository.mongo;

import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingDocumentRepository extends CrudRepository<TrainingDocument,Long>, CategoryRepositoryCustom{

}
