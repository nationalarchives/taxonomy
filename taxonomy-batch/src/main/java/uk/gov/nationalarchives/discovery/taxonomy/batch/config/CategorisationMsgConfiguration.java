/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import uk.gov.nationalarchives.discovery.taxonomy.batch.msg.consumer.CategoriseDocMessageConsumer;

/**
 * Configuration dedicated to the messaging service (Active MQ)
 * 
 * @author jcharlet
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "batch.role.", value = "check-categorisation-request-messages")
class CategorisationMsgConfiguration {

    @Value("${spring.activemq.categorise-doc-queue-name}")
    String categoriseDocumentsQueueName;

    @Bean
    MessageListenerAdapter categorisationListenerAdapter(CategoriseDocMessageConsumer categoriseDocMessageConsumer) {
	MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(categoriseDocMessageConsumer);
	messageListenerAdapter.setMessageConverter(null);
	return messageListenerAdapter;
    }

    @Bean
    DefaultMessageListenerContainer categorisationContainer(MessageListenerAdapter categorisationListenerAdapter,
	    ConnectionFactory connectionFactory) {
	DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
	container.setMessageListener(categorisationListenerAdapter);
	container.setConnectionFactory(connectionFactory);
	container.setDestinationName(categoriseDocumentsQueueName);
	return container;
    }

}
