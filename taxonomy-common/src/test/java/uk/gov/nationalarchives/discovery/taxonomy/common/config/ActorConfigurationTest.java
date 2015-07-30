/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.common.service.actor")
@Import({ PropertiesConfiguration.class })
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

	// system.logConfiguration();
	return system;
    }

    @Bean
    public ActorSystem deadLettersActorSystem() {
	ActorSystem system = ActorSystem.create("DeadLetters");
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
