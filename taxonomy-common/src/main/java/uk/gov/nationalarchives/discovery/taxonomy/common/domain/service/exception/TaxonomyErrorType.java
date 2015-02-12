package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception;

public enum TaxonomyErrorType {
    INVALID_CATEGORY_QUERY, LOCKED_CATEGORY, LUCENE_IO_EXCEPTION, INVALID_PARAMETER, LUCENE_PARSE_EXCEPTION, HTTPCLIENT_ERROR, LUCENE_PARSE_VERSION;
}
