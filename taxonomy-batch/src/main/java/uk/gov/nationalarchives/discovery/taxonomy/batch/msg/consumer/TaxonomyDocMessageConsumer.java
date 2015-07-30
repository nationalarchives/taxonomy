/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer;

import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer.message.TaxonomyDocumentMessageHolder;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;

/**
 * abstract Consumer dedicated to handling all requests sent to activeMQ
 * dedicated queues
 * 
 * @author jcharlet
 *
 */
public abstract class TaxonomyDocMessageConsumer {

    public TaxonomyDocMessageConsumer() {
	super();
    }

    /**
     * handles the message received from queue
     * 
     * @param message
     */
    public abstract void handleMessage(Message message);

    protected TaxonomyDocumentMessageHolder getTaxonomyDocumentMessageFromMessage(Message message) {
	return new TaxonomyDocumentMessageHolder(getJMSMessageIdFromMessage(message),
		getListOfDocReferencesFromMessage((TextMessage) message));
    }

    protected boolean isTextMessageInvalid(Message message) {
	return !(message instanceof TextMessage);
    }

    protected String getJMSMessageIdFromMessage(Message message) {
	String messageId;
	try {
	    messageId = message.getJMSMessageID();
	} catch (JMSException e) {
	    throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
	}
	return messageId;
    }

    protected List<String> getListOfDocReferencesFromMessage(TextMessage message) {
	String listOfDocReferencesString;
	try {
	    listOfDocReferencesString = message.getText();
	} catch (JMSException e) {
	    throw new TaxonomyException(TaxonomyErrorType.JMS_EXCEPTION, e);
	}
	return Arrays.asList(listOfDocReferencesString.split(";"));
    }
}