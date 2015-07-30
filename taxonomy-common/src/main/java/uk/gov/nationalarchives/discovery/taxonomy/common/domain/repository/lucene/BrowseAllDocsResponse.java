/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;

public class BrowseAllDocsResponse {
    private final List<String> listOfDocReferences;
    private final ScoreDoc lastScoreDoc;

    public BrowseAllDocsResponse(List<String> listOfDocReferences, ScoreDoc lastScoreDoc) {
	super();
	this.listOfDocReferences = listOfDocReferences;
	this.lastScoreDoc = lastScoreDoc;
    }

    public List<String> getListOfDocReferences() {
	return listOfDocReferences;
    }

    public ScoreDoc getLastScoreDoc() {
	return lastScoreDoc;
    }

}
