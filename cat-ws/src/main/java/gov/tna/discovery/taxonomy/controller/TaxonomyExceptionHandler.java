package gov.tna.discovery.taxonomy.controller;

import gov.tna.discovery.taxonomy.domain.TaxonomyErrorResponse;
import gov.tna.discovery.taxonomy.domain.TaxonomyErrorType;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TaxonomyExceptionHandler {

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<TaxonomyErrorResponse> handleLuceneParseException(ParseException ex) {
	return new ResponseEntity<TaxonomyErrorResponse>(new TaxonomyErrorResponse(TaxonomyErrorType.INVALID_CATEGORY_QUERY,
		ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
