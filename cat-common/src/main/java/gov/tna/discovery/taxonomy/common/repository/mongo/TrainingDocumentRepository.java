package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.domain.repository.TrainingDocument;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingDocumentRepository extends CrudRepository<TrainingDocument, Long>,
	TrainingDocumentRepositoryCustom {

    public List<TrainingDocument> findByCategory(String category);

}
