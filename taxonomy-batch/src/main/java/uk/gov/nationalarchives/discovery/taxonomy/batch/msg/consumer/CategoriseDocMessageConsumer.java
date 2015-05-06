package uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer;

import java.util.Arrays;

import javax.jms.Message;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer.message.TaxonomyDocumentMessageHolder;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;

/**
 * Consumer dedicated to handling all Categorisation requests sent to activeMQ
 * dedicated queue
 * 
 * @author jcharlet
 *
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = "check-categorisation-request-messages")
public class CategoriseDocMessageConsumer extends TaxonomyDocMessageConsumer {

    private final CategoriserService<CategorisationResult> categoriserService;

    private static final Logger logger = LoggerFactory.getLogger(CategoriseDocMessageConsumer.class);

    @Autowired
    public CategoriseDocMessageConsumer(CategoriserService<CategorisationResult> categoriserService) {
	super();
	this.categoriserService = categoriserService;
    }

    @Override
    public void handleMessage(Message message) {
	if (isTextMessageInvalid(message)) {
	    logger.error("message is invalid and was not processed: {}", message.toString());
	    return;
	}

	TaxonomyDocumentMessageHolder categoriseDocumentMessage = getTaxonomyDocumentMessageFromMessage(message);

	logger.info("received Categorise Document message: {}, docReferences: {}",
		categoriseDocumentMessage.getMessageId(),
		ArrayUtils.toString(categoriseDocumentMessage.getListOfDocReferences()));

	categoriserService.refreshTaxonomyIndex();

	for (String docReference : categoriseDocumentMessage.getListOfDocReferences()) {
	    try {
		categoriserService.categoriseSingle(docReference);
	    } catch (TaxonomyException e) {
		categoriseDocumentMessage.addDocReferenceInError(docReference);
		if (TaxonomyErrorType.DOC_NOT_FOUND.equals(e.getTaxonomyErrorType())) {
		    logger.error(
			    "document could not be processed because it was not found in the index: {}, from message: {}",
			    docReference, categoriseDocumentMessage.getMessageId());
		} else {
		    logger.error("an error occured while processing Document: {}, from message: {}", docReference,
			    categoriseDocumentMessage.getMessageId(), e);
		}
	    }
	}

	if (categoriseDocumentMessage.hasProcessingErrors()) {
	    logger.warn("completed treatment for message: {} with {} errors", categoriseDocumentMessage.getMessageId(),
		    categoriseDocumentMessage.getListOfDocReferencesInError().size());
	    logger.error("DOCREFERENCES THAT COULD NOT BE CATEGORISED: {}",
		    Arrays.toString(categoriseDocumentMessage.getListOfDocReferencesInError().toArray()));
	} else {
	    logger.info("completed treatment for message: {}", categoriseDocumentMessage.getMessageId());
	}
    }

}