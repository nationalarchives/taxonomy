/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.mapper;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TestCategoriseSingleRequest;

public class WSTaxonomyMapper {

    public static InformationAssetView getIAviewFromRequest(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCONTEXTDESCRIPTION(testCategoriseSingleRequest.getContextDescription());
	iaView.setDESCRIPTION(testCategoriseSingleRequest.getDescription());
	iaView.setTITLE(testCategoriseSingleRequest.getTitle());
	iaView.setCATDOCREF(testCategoriseSingleRequest.getCatDocRef());
	iaView.setCORPBODYS(testCategoriseSingleRequest.getCorpBodys());
	iaView.setCOVERINGDATES(testCategoriseSingleRequest.getCoveringDates());
	iaView.setDOCREFERENCE(testCategoriseSingleRequest.getDocReference());
	iaView.setPERSON_FULLNAME(testCategoriseSingleRequest.getPerson_FULLNAME());
	iaView.setPLACE_NAME(testCategoriseSingleRequest.getPlace_NAME());
	iaView.setSUBJECTS(testCategoriseSingleRequest.getSubjects());
	return iaView;
    }

}
