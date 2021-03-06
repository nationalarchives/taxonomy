/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p/>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.worker;

import akka.actor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.batch.actor.DeadLetterActor;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseAllDocumentsEpic;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;

import java.util.concurrent.Executors;

;

/**
 * Worker Batch that works on categorising all documents<br/>
 * main task is to start the worker (worker) actor
 *
 * @see CategorisationWorkerActor
 */
@Component
@ConditionalOnProperty(prefix = "batch.role.", value = {"categorise-all.worker"})
@SuppressWarnings("rawtypes")
public class CategorisationWorkerRunner implements CommandLineRunner {

    private final ActorSystem deadLettersActorSystem;
    private final ActorSystem actorSystem;
    private final CategoriserService categoriserService;
    private final LuceneHelperTools luceneHelperTools;
    private final CategoryRepository categoryRepository;

    @Value("${batch.categorise-all.supervisor-hostname}")
    private String supervisorHostname;

    @Value("${batch.categorise-all.supervisor-port}")
    private String supervisorPort;

    @Value("${batch.categorise-all.afterDocNumber}")
    private Integer afterDocNumber;

    @Value("${batch.categorise-all.startEpic}")
    private Boolean startEpic;

    @Autowired
    public CategorisationWorkerRunner(ActorSystem deadLettersActorSystem, ActorSystem actorSystem,
                                      CategoriserService categoriserService, LuceneHelperTools luceneHelperTools,
                                      CategoryRepository categoryRepository) {
        super();
        this.deadLettersActorSystem = deadLettersActorSystem;
        this.actorSystem = actorSystem;
        this.categoriserService = categoriserService;
        this.luceneHelperTools = luceneHelperTools;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args)  {
        Executors.newSingleThreadExecutor().submit(() -> {
            trackDeadLetters();

            String supervisorAddress = "akka.tcp://supervisor@" + supervisorHostname + ":" + supervisorPort
                    + "/user/supervisorActor";
            ActorRef worker = actorSystem.actorOf(Props.create(CategorisationWorkerActor.class, supervisorAddress,
                    categoriserService, luceneHelperTools, categoryRepository), "workerActor");

            try{
                Thread.sleep(2000);
            }catch (Exception e){
                //do nothing
            }

            if (this.startEpic) {
                ActorSelection supervisorReference = actorSystem.actorSelection(supervisorAddress);
                if (afterDocNumber == null) {
                    supervisorReference.tell(new CategoriseAllDocumentsEpic(), worker);
                } else {
                    supervisorReference.tell(new CategoriseAllDocumentsEpic(this.afterDocNumber), worker);
                }
            }
        });

    }

    private void trackDeadLetters() {
        final ActorRef actor = deadLettersActorSystem.actorOf(Props.create(DeadLetterActor.class));
        actorSystem.eventStream().subscribe(actor, AllDeadLetters.class);
    }
}
