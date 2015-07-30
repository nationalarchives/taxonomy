/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class LegacySystemServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(LegacySystemServiceTest.class);

    @Autowired
    public LegacySystemRepository legacySystemService;

    /**
     * To keep ignored not to contact external service
     */
    // @Test
    // @Ignore
    public void testFindLegacyDocumentsByQuery() {

	Map<String, String[]> mapOfDocumentIaidsAndCategories = legacySystemService.findLegacyDocumentsByCategory(
		"Labour", 1);

	assertThat(mapOfDocumentIaidsAndCategories, is(notNullValue()));
	assertThat(mapOfDocumentIaidsAndCategories.entrySet(), is(not(emptyIterable())));
    }
}
