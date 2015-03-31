package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.EvaluationReport;

import org.springframework.data.repository.CrudRepository;

public interface EvaluationReportRepository extends CrudRepository<EvaluationReport, Long> {

}
