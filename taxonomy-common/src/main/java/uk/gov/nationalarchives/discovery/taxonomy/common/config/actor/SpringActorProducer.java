package uk.gov.nationalarchives.discovery.taxonomy.common.config.actor;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

import org.springframework.context.ApplicationContext;

/**
 * An actor producer that lets Spring create the Actor instances.
 */
// FIXME to remove?
public class SpringActorProducer implements IndirectActorProducer {
    final ApplicationContext applicationContext;
    final String actorBeanName;

    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
	this.applicationContext = applicationContext;
	this.actorBeanName = actorBeanName;
    }

    @Override
    public Actor produce() {
	return (Actor) applicationContext.getBean(actorBeanName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Actor> actorClass() {
	return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}
