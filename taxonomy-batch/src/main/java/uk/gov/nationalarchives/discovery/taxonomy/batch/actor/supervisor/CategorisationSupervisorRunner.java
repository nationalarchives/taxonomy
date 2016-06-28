/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.supervisor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AllDeadLetters;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.batch.actor.DeadLetterActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;

import java.util.concurrent.Executors;

;

/**
 * Supervisor Batch that works on categorising all documents<br/>
 * main task is to start the supervisor actor
 * 
 * @see CategorisationSupervisorActor
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = { "categorise-all.supervisor" })
@SuppressWarnings("rawtypes")
public class CategorisationSupervisorRunner implements CommandLineRunner {

    // private static final Logger logger =
    // LoggerFactory.getLogger(CategorisationSupervisorRunner.class);

    @Value("${batch.categorise-all.message-size}")
    private int nbOfDocsToCategoriseAtATime;

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final IAViewService iaViewService;
    private final CategoriserService categoriserService;

    @Autowired
    public CategorisationSupervisorRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
	    IAViewService iaViewService, CategoriserService categoriserService) {
	super();
	this.deadLettersActorSystem = deadLettersActorSystem;
	this.actorSystem = actorSystem;
	this.iaViewService = iaViewService;
	this.categoriserService = categoriserService;
    }

    @Override
    public void run(String... args) throws Exception {
        Executors.newSingleThreadExecutor().submit(() -> {
            trackDeadLetters();
            actorSystem.actorOf(Props.create(CategorisationSupervisorActor.class, nbOfDocsToCategoriseAtATime,
                    iaViewService, categoriserService), "supervisorActor");
        });
    }

    private void trackDeadLetters() {
	final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
	actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
