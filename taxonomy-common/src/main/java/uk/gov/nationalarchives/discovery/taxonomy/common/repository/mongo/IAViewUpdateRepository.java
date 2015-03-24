package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

@Repository
public interface IAViewUpdateRepository extends PagingAndSortingRepository<IAViewUpdate, String>,
	IAViewUpdateRepositoryCustom {

    Page<IAViewUpdate> findByIdGreaterThan(ObjectId id, Pageable pageable);
}
