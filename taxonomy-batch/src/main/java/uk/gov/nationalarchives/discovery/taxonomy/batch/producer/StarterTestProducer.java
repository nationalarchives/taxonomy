package uk.gov.nationalarchives.discovery.taxonomy.batch.producer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * To send a message in activemq message queue. For testing purposes only.<br/>
 * disable @Component annotation to send a message at batch application start
 * 
 * @author jcharlet
 *
 */
// TODO 5 to remove once batch is finished
// @Component
public class StarterTestProducer implements CommandLineRunner {

    @Value("${spring.activemq.categorise-doc-queue-name}")
    String queueName;

    @Autowired
    ConfigurableApplicationContext context;

    public StarterTestProducer(String queueName) {
	super();
	this.queueName = queueName;
    }

    public StarterTestProducer() {
	super();
    }

    public void sendMessage(ConfigurableApplicationContext context) {
	// Send a message
	MessageCreator messageCreator = new MessageCreator() {
	    @Override
	    public Message createMessage(Session session) throws JMSException {
		return session.createTextMessage("C14906;14966");
	    }
	};
	JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
	System.out.println("Sending a new message.");
	jmsTemplate.send(queueName, messageCreator);
    }

    @Override
    public void run(String... args) throws Exception {
	sendMessage(this.context);

    }
}
