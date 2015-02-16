package uk.gov.nationalarchives.discovery.taxonomy.batch.consumer;

import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.batch.consumer.message.CategoriseDocumentMessage;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;

/**
 * Consumer dedicated to handling all Categorisation requests sent to activeMQ
 * dedicated queue
 * 
 * @author jcharlet
 *
 */
// TODO 1 handling of messages in error? log? retry?
@Component
public class CategoriseDocMessageConsumer {

    @Autowired
    private CategoriserService<CategorisationResult> categoriserService;

    private static final Logger logger = LoggerFactory.getLogger(CategoriseDocMessageConsumer.class);

    public void handleMessage(Message message) {
	if (isTextMessageInvalid(message)) {
	    logger.error("message is invalid and was not processed: {}", message.toString());
	    return;
	}

	CategoriseDocumentMessage categoriseDocumentMessage = getCategoriseDocumentMessageFromMessage(message);

	logger.info("received Categorise Document message: {}, docReferences: {}",
		categoriseDocumentMessage.getMessageId(), categoriseDocumentMessage.getListOfDocReferences());

	for (String docReference : categoriseDocumentMessage.getListOfDocReferences()) {
	    try {
		categoriserService.categoriseSingle(docReference);
	    } catch (TaxonomyException e) {
		categoriseDocumentMessage.setHasProcessingErrors(true);
		logger.error("an error occured while processing Document: {}, from message: {}", docReference,
			categoriseDocumentMessage.getMessageId(), e);
	    }
	}

	if (categoriseDocumentMessage.hasProcessingErrors()) {
	    logger.warn("completed treatment for message: {} with errors", categoriseDocumentMessage.getMessageId());
	} else {
	    logger.info("completed treatment for message: {}", categoriseDocumentMessage.getMessageId());
	}
    }

    private CategoriseDocumentMessage getCategoriseDocumentMessageFromMessage(Message message) {
	return new CategoriseDocumentMessage(getJMSMessageIdFromMessage(message),
		getListOfDocReferencesFromMessage((TextMessage) message));
    }

    private boolean isTextMessageInvalid(Message message) {
	return !(message instanceof TextMessage);
    }

    private String getJMSMessageIdFromMessage(Message message) {
	String messageId;
	try {
	    messageId = message.getJMSMessageID();
	} catch (JMSException e) {
	    throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
	}
	return messageId;
    }

    private List<String> getListOfDocReferencesFromMessage(TextMessage message) {
	String listOfDocReferencesString;
	try {
	    listOfDocReferencesString = message.getText();
	} catch (JMSException e) {
	    throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
	}
	return Arrays.asList(listOfDocReferencesString.split(";"));
    }
}