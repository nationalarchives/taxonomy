package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import static uk.gov.nationalarchives.discovery.taxonomy.common.config.actor.SpringExtension.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import akka.actor.ActorSystem;

import com.typesafe.config.ConfigFactory;

/**
 * Configuration dedicated to the actor based system to categorise all documents
 */
@Configuration
@EnableSpringConfigured
@EnableLoadTimeWeaving
@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.common.service.actor")
public class ActorConfigurationTest {

    // the application context is needed to initialize the Akka Spring Extension
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
	ActorSystem system = null;
	system = ActorSystem.create("supervisor", ConfigFactory.load("supervisor.conf"));

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

    @Bean
    public IAViewService iaViewService() {
	return null;
    };

    @SuppressWarnings("rawtypes")
    @Bean
    public CategoriserService categoriserService() {
	return null;
    }

}
