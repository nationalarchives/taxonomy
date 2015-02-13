package uk.gov.nationalarchives.discovery.taxonomy;

import java.io.IOException;
import java.text.ParseException;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import uk.gov.nationalarchives.discovery.taxonomy.batch.consumer.CategoriseDocMessageConsumer;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@PropertySource("application.yml")
public class BatchApplication {

    @Value("${spring.activemq.categorise-doc-queue-name}")
    String categoriseDocumentsQueueName;

    @Bean
    MessageListenerAdapter adapter(CategoriseDocMessageConsumer categoriseDocMessageConsumer) {
	MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(categoriseDocMessageConsumer);
	messageListenerAdapter.setMessageConverter(null);
	return messageListenerAdapter;
    }

    @Bean
    DefaultMessageListenerContainer container(MessageListenerAdapter messageListener,
	    ConnectionFactory connectionFactory) {
	DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
	container.setMessageListener(messageListener);
	container.setConnectionFactory(connectionFactory);
	container.setDestinationName(categoriseDocumentsQueueName);
	return container;
    }

    public static void main(String[] args) throws IOException, ParseException {
	SpringApplication.run(BatchApplication.class, args);
    }

}
