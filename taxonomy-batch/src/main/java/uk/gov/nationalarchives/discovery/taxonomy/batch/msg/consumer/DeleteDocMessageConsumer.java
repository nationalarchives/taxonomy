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
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;

/**
 * Consumer dedicated to handling all Deletion requests sent to activeMQ
 * dedicated queue: whenever a document has to be deleted from index, a message
 * is sent and processed in that batch to make sure the specified documents are
 * removed from mongo collections so that if mongo collections must be directly
 * used to update solr, no empty document will be created
 * 
 * @author jcharlet
 *
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = "delete-documents-request-messages")
public class DeleteDocMessageConsumer extends TaxonomyDocMessageConsumer {

    private final InformationAssetViewMongoRepository informationAssetViewMongoRepository;
    private final IAViewUpdateRepository iaViewUpdateRepository;

    private static final Logger logger = LoggerFactory.getLogger(DeleteDocMessageConsumer.class);

    @Autowired
    public DeleteDocMessageConsumer(InformationAssetViewMongoRepository informationAssetViewMongoRepository,
	    IAViewUpdateRepository iaViewUpdateRepository) {
	super();
	this.informationAssetViewMongoRepository = informationAssetViewMongoRepository;
	this.iaViewUpdateRepository = iaViewUpdateRepository;
    }

    @Override
    public void handleMessage(Message message) {
	if (isTextMessageInvalid(message)) {
	    logger.error("message is invalid and was not processed: {}", message.toString());
	    return;
	}

	TaxonomyDocumentMessageHolder deleteDocumentMessage = getTaxonomyDocumentMessageFromMessage(message);

	logger.info("received Delete Document message: {}, docReferences: {}",
		deleteDocumentMessage.getMessageId(),
		ArrayUtils.toString(deleteDocumentMessage.getListOfDocReferences()));

	for (String docReference : deleteDocumentMessage.getListOfDocReferences()) {
	    try {
		removeDocumentFromMongoByDocReference(docReference);
	    } catch (TaxonomyException e) {
		deleteDocumentMessage.addDocReferenceInError(docReference);
		logger.error("an error occured while processing Document: {}, from message: {}", docReference,
			deleteDocumentMessage.getMessageId(), e);
	    }
	}

	if (deleteDocumentMessage.hasProcessingErrors()) {
	    logger.warn("completed treatment for message: {} with {} errors", deleteDocumentMessage.getMessageId(),
		    deleteDocumentMessage.getListOfDocReferencesInError().size());
	    logger.error("DOCREFERENCES that raise an issue while deleting: {}",
		    Arrays.toString(deleteDocumentMessage.getListOfDocReferencesInError().toArray()));
	} else {
	    logger.info("completed treatment for message: {}", deleteDocumentMessage.getMessageId());
	}
    }

    private void removeDocumentFromMongoByDocReference(String docReference) {
	informationAssetViewMongoRepository.delete(docReference);
	iaViewUpdateRepository.findAndRemoveByDocReference(docReference);
    }

}