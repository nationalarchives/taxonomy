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
	final Props props = Props.create(CategorisationSupervisorActor.class, nbOfDocsToCategoriseAtATime);
	final TestActorRef<CategorisationSupervisorActor> ref = TestActorRef.create(actorSystem, props, "testA");
	final CategorisationSupervisorActor actor = ref.underlyingActor();

	CategoriserService categoriserService = Mockito.mock(CategoriserService.class);
	IAViewService iaViewService = Mockito.mock(IAViewService.class);
	Mockito.when(iaViewService.getTotalNbOfDocs()).thenReturn(10);

	actor.setCategoriserService(categoriserService);
	actor.setIaViewService(iaViewService);
	actor.startEpic(new CategoriseAllDocumentsEpic());

	Mockito.verify(iaViewService, Mockito.times(1)).getTotalNbOfDocs();
	Mockito.verify(categoriserService, Mockito.times(1)).refreshTaxonomyIndex();
    }
}
