/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;

@Service
public class IAViewServiceImpl implements IAViewService {

    private final IAViewRepository iaViewRepository;

    @Autowired
    public IAViewServiceImpl(IAViewRepository iaViewRepository) {
	super();
	this.iaViewRepository = iaViewRepository;
    }

    @Override
    public PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit,
	    Integer offset) {
	return iaViewRepository.performSearch(categoryQuery, score, limit, offset);
    }

    @Override
    public int getTotalNbOfDocs() {
	return iaViewRepository.getTotalNbOfDocs();
    }

    @Override
    public BrowseAllDocsResponse browseAllDocs(ScoreDoc after, int size) {
	return iaViewRepository.browseAllDocs(after, size);
    }

}
