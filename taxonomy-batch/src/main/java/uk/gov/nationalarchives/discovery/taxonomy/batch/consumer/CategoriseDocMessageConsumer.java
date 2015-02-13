package uk.gov.nationalarchives.discovery.taxonomy.batch.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CategoriseDocMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CategoriseDocMessageConsumer.class);

    public void handleMessage(String message) {
	logger.info("received Categorise Document message: {}", message);
    }
}