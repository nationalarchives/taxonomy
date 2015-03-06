package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception;

public enum TaxonomyErrorType {

    /**
     * if the category query to search is invalid
     */
    INVALID_CATEGORY_QUERY,

    /**
     * when trying to use a category that is currently being published (TSET
     * Based)
     */
    LOCKED_CATEGORY,

    /**
     * when an error occurs while accessing lucene index
     */
    LUCENE_IO_EXCEPTION,

    /**
     * when an invalid parameter was passed to a function (mostly on the front
     * side: WS, CLI, etc)
     */
    INVALID_PARAMETER,

    /**
     * when an error occurs while parsing a search query with Lucene
     */
    LUCENE_PARSE_EXCEPTION,

    /**
     * when an error occurs while creating the beans dedicated to Lucene:
     * parsing of the lucene version failed
     */
    LUCENE_PARSE_VERSION,

    /**
     * When an error occurs while using the Messaging Active MQ queue
     */
    JMS_EXCEPTION,

    /**
     * When an error occurs while reading data from Solr Server
     */
    SOLR_READ_EXCEPTION,

    /**
     * When an error occurs while updating Solr Server
     */
    SOLR_WRITE_EXCEPTION,

    /**
     * Document was not found in lucene Index
     */
    DOC_NOT_FOUND;
}
