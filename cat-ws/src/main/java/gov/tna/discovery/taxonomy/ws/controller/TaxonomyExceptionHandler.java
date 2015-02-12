package gov.tna.discovery.taxonomy.ws.controller;

import gov.tna.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import gov.tna.discovery.taxonomy.ws.model.TaxonomyErrorResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class TaxonomyExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyExceptionHandler.class);

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<TaxonomyErrorResponse> handleLuceneParseException(ParseException ex, WebRequest webRequest) {
	TaxonomyErrorResponse errorResponse = new TaxonomyErrorResponse(TaxonomyErrorType.INVALID_CATEGORY_QUERY,
		ex.getMessage());
	logger.error("{} < {}", extractPathFromWebRequest(webRequest), errorResponse.toString());
	return new ResponseEntity<TaxonomyErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaxonomyException.class)
    public ResponseEntity<TaxonomyErrorResponse> handleTaxonomyException(TaxonomyException ex, WebRequest webRequest) {
	TaxonomyErrorResponse errorResponse = new TaxonomyErrorResponse(ex.getTaxonomyErrorType(), ex.getMessage());
	logger.error("{} < {}", extractPathFromWebRequest(webRequest), errorResponse.toString());
	return new ResponseEntity<TaxonomyErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String extractPathFromWebRequest(WebRequest webRequest) {
	String[] uriElements = webRequest.getDescription(false).split("uri=/taxonomy");
	String requestPath;
	if (uriElements.length == 2) {
	    requestPath = uriElements[1];
	} else {
	    requestPath = uriElements[0];
	}
	return requestPath;
    }
}
