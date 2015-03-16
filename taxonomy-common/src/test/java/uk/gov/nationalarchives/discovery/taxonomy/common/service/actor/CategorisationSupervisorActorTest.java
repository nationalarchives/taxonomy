package uk.gov.nationalarchives.discovery.taxonomy.common.service.actor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.ActorConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

@SuppressWarnings("rawtypes")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ActorConfigurationTest.class)
public class CategorisationSupervisorActorTest {

    @Autowired
    private ActorSystem actorSystem;

    // TODO 5 need to provide javaagent in jvm args to run that test
    // -javaagent:/home/jcharlet/.m2/repository/org/springframework/spring-instrument/4.0.7.RELEASE/spring-instrument-4.0.7.RELEASE.jar
    @Test
    @Ignore
    public void testCreateEpic() {
	final Props props = Props.create(CategorisationSupervisorActor.class);
	final TestActorRef<CategorisationSupervisorActor> ref = TestActorRef.create(actorSystem, props, "testA");
	final CategorisationSupervisorActor actor = ref.underlyingActor();

	CategoriserService categoriserService = Mockito.mock(CategoriserService.class);
	IAViewService iaViewService = Mockito.mock(IAViewService.class);
	Mockito.when(iaViewService.getTotalNbOfDocs()).thenReturn(10);

	actor.setCategoriserService(categoriserService);
	actor.setIaViewService(iaViewService);
	actor.startEpic();

	Mockito.verify(iaViewService, Mockito.times(1)).getTotalNbOfDocs();
	Mockito.verify(categoriserService, Mockito.times(1)).refreshTaxonomyIndex();
    }
}
