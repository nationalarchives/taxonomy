/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;

@Component("DeadLetter")
@Scope("prototype")
public class DeadLetterActor extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterActor.class);

    public DeadLetterActor() {
    }

    @Override
    public void onReceive(Object message) {
	if (message instanceof DeadLetter) {
	    Object innerMessage = ((DeadLetter) message).message();
	    logger.warn(".onReceive: encountered dead letter: {}", innerMessage);
	}
    }

}
