/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.ActorConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.CategoriseAllDocumentsEpic;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor.CategorisationSupervisorActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

@SuppressWarnings("rawtypes")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ActorConfigurationTest.class)
@ActiveProfiles("batch")
public class CategorisationSupervisorActorTest {

    @Value("${batch.categorise-all.message-size}")
    private int nbOfDocsToCategoriseAtATime;

    @Value("${batch.categorise-all.supervisor-address}")
    private String supervisorAddress;

    @Autowired
    private ActorSystem actorSystem;

    @Test
    public void testCreateEpic() {
	CategoriserService categoriserService = Mockito.mock(CategoriserService.class);
	IAViewService iaViewService = Mockito.mock(IAViewService.class);
	Mockito.when(iaViewService.getTotalNbOfDocs()).thenReturn(10);

	final Props props = Props.create(CategorisationSupervisorActor.class, nbOfDocsToCategoriseAtATime,
		iaViewService, categoriserService);
	final TestActorRef<CategorisationSupervisorActor> ref = TestActorRef.create(actorSystem, props, "testA");
	final CategorisationSupervisorActor actor = ref.underlyingActor();

	actor.startEpic(new CategoriseAllDocumentsEpic());

	Mockito.verify(iaViewService, Mockito.times(1)).getTotalNbOfDocs();
	Mockito.verify(categoriserService, Mockito.times(1)).refreshTaxonomyIndex();
    }
}
