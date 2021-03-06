/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;

import com.typesafe.config.ConfigFactory;

/**
 * Configuration dedicated to the actor based system to categorise all documents
 */
@Configuration
@ConditionalOnProperty(prefix = "batch.role.", value = "categorise-all")
@ConfigurationProperties(prefix = "batch.role.categorise-all")
public class ActorConfiguration {

    // the application context is needed to initialize the Akka Spring Extension
    @Autowired
    private ApplicationContext applicationContext;

    private boolean supervisor;

    @Value("${batch.categorise-all.supervisor-hostname}")
    private String supervisorHostname;

    @Value("${batch.categorise-all.supervisor-port}")
    private String supervisorPort;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {
	ActorSystem system = null;
	if (this.supervisor) {

	    Map<String, String> akkaParameters = new HashMap<String, String>();
	    akkaParameters.put("akka.remote.netty.tcp.hostname", supervisorHostname);
	    akkaParameters.put("akka.remote.netty.tcp.bind-hostname", "0.0.0.0");
	    akkaParameters.put("akka.remote.netty.tcp.port", supervisorPort);

	    system = ActorSystem.create("supervisor",
		    ConfigFactory.parseMap(akkaParameters).withFallback(ConfigFactory.load("supervisor.conf")));
	} else {
	    system = ActorSystem.create("worker", ConfigFactory.load("worker.conf"));
	}

	// system.logConfiguration();
	return system;
    }

    @Bean
    public ActorSystem deadLettersActorSystem() {
	ActorSystem system = ActorSystem.create("DeadLetters");
	return system;
    }

    public void setSupervisor(boolean supervisor) {
	this.supervisor = supervisor;
    }

}
