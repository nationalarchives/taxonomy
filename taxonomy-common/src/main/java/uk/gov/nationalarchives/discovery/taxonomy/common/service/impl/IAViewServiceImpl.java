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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;

import java.util.ArrayList;
import java.util.List;

@Service
public class IAViewServiceImpl implements IAViewService {

    private final IAViewRepository iaViewRepository;
    private final InformationAssetViewMongoRepository informationAssetViewMongoRepository;

    @Autowired
    public IAViewServiceImpl(IAViewRepository iaViewRepository, InformationAssetViewMongoRepository informationAssetViewMongoRepository) {
        super();
	this.iaViewRepository = iaViewRepository;
        this.informationAssetViewMongoRepository = informationAssetViewMongoRepository;
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

    @Override
    public PaginatedList<InformationAssetView> findUntaggedDocumentsBySeries(String seriesIaid, Integer limit, Integer offset) {
        //FIXME JCT HIGH when using the exact same method in the dao layer, the paging fails

        PaginatedList<InformationAssetView> toReturn = new PaginatedList<>();
//        List<MongoInformationAssetView> untaggedDocumentsBySeries = informationAssetViewMongoRepository
//                .findUntaggedDocumentsBySeries(seriesIaid, limit, offset);

        toReturn.setNumberOfResults(countUntaggedDocumentsBySeriesMongo(seriesIaid).intValue());
        List<MongoInformationAssetView> untaggedDocumentsBySeries = findUntaggedDocumentsBySeriesMongo(seriesIaid, limit, offset);

        List<InformationAssetView> informationAssetViews = new ArrayList<>();
        for (MongoInformationAssetView untaggedDocumentBySeries : untaggedDocumentsBySeries) {
            informationAssetViews.add(iaViewRepository.searchDocByDocReference(untaggedDocumentBySeries.getDocReference
                    ()));
        }
        toReturn.setResults(informationAssetViews);

        toReturn.setLimit(limit);
        toReturn.setOffset(offset);
        return toReturn;
    }

    //FIXME JCT HIGH remove mongoTemplate as soon as we are making mongo request findUntaggedDocumentsBySeriesMongo
    // from dao layer
    @Autowired
    MongoTemplate mongoTemplate;

    public List<MongoInformationAssetView> findUntaggedDocumentsBySeriesMongo(String seriesIaid, Integer limit, Integer
            offset) {
        Query query = Query.query(Criteria.where("series").is(seriesIaid).and("categories").size(0));
        query.limit(limit);
        query.skip(offset);
        query.with(new Sort(Sort.Direction.ASC, "_id"));
        return mongoTemplate.find(query, MongoInformationAssetView.class);
    }

    public Long countUntaggedDocumentsBySeriesMongo(String seriesIaid) {
        Query query = Query.query(Criteria.where("series").is(seriesIaid).and("categories").size(0));
        query.with(new Sort(Sort.Direction.ASC, "_id"));
        return mongoTemplate.count(query, MongoInformationAssetView.class);
    }

}
