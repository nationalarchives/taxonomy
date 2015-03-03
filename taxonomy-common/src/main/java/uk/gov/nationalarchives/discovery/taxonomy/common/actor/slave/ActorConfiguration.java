package uk.gov.nationalarchives.discovery.taxonomy.common.actor.slave;

import akka.actor.ActorSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.typesafe.config.ConfigFactory;

import static uk.gov.nationalarchives.discovery.taxonomy.common.actor.common.SpringExtension.*;

/**
 * The application configuration.
 */
@Configuration
class ActorConfiguration {

    // the application context is needed to initialize the Akka Spring Extension
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
	ActorSystem system = ActorSystem.create("slave", ConfigFactory.load("slave.conf"));
	// initialize the application context in the Akka Spring Extension
	SpringExtProvider.get(system).initialize(applicationContext);
	// system.logConfiguration();
	return system;
    }

    @Bean
    public ActorSystem deadLettersActorSystem() {
	ActorSystem system = ActorSystem.create("DeadLetters");
	// initialize the application context in the Akka Spring Extension
	SpringExtProvider.get(system).initialize(applicationContext);
	return system;
    }

}
