/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

/**
 * Repository dedicated to training document database. <br/>
 * dedicated to methods that are too complex to be provided with spring
 * repository<br/>
 * returns the number of removed elements
 * 
 * @author jcharlet
 */
public interface TrainingDocumentRepositoryCustom {
    int deleteByCategory(String category);
}