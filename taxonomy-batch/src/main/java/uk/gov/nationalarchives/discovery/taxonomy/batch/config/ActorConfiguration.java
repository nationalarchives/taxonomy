package uk.gov.nationalarchives.discovery.taxonomy.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import akka.actor.ActorSystem;

import com.typesafe.config.ConfigFactory;

/**
 * Configuration dedicated to the actor based system to categorise all documents
 */
@Configuration
@ConditionalOnProperty(prefix = "batch.role.", value = "categorise-all")
@ConfigurationProperties(prefix = "batch.role.categorise-all")
@EnableSpringConfigured
@EnableLoadTimeWeaving
public class ActorConfiguration {

    // the application context is needed to initialize the Akka Spring Extension
    @Autowired
    private ApplicationContext applicationContext;

    private boolean supervisor;

    // TODO 2 use cluster to avoid defining by hand who is the master and who he
    // talks to
    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
	ActorSystem system = null;
	if (this.supervisor) {
	    system = ActorSystem.create("supervisor", ConfigFactory.load("supervisor.conf"));
	} else {
	    system = ActorSystem.create("slave", ConfigFactory.load("slave.conf"));
	}

	// initialize the application context in the Akka Spring Extension
	// SpringExtProvider.get(system).initialize(applicationContext);

	// system.logConfiguration();
	return system;
    }

    @Bean
    public ActorSystem deadLettersActorSystem() {
	ActorSystem system = ActorSystem.create("DeadLetters");
	// initialize the application context in the Akka Spring Extension
	// SpringExtProvider.get(system).initialize(applicationContext);
	return system;
    }

    public void setSupervisor(boolean supervisor) {
	this.supervisor = supervisor;
    }

}
