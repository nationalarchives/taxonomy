package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.domain.repository.mongo.EvaluationReport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationReportRepository extends CrudRepository<EvaluationReport, Long> {

}
