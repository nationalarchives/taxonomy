/**
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk
 * <p/>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryLight;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrCloudIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrCloudService;

import java.util.*;

@Service
@ConditionalOnProperty(prefix = "solr.cloud.", value = "host")
public class UpdateSolrCloudServiceImpl implements UpdateSolrCloudService {

    private static final String FIELD_MODIFIER_KEY_SET = "set";

    private static final Logger logger = LoggerFactory.getLogger(UpdateSolrCloudServiceImpl.class);

    private final SolrCloudIAViewRepository solrIAViewRepository;

    private final InformationAssetViewMongoRepository informationAssetViewMongoRepository;

    @Autowired
    public UpdateSolrCloudServiceImpl(SolrCloudIAViewRepository solrIAViewRepository,
                                      InformationAssetViewMongoRepository informationAssetViewMongoRepository) {
        super();
        this.solrIAViewRepository = solrIAViewRepository;
        this.informationAssetViewMongoRepository = informationAssetViewMongoRepository;
    }

    @Override
    public void updateCategoriesOnIAView(String docReference) {
        logger.info(".updateCategoriesOnIAView: {}", docReference);

        List<CategoryLight> categories = retrieveCategoriesForIAView(docReference);

        SolrInputDocument document = createDocumentForAtomicUpdate(docReference, categories);

        solrIAViewRepository.save(document);
    }

    private List<CategoryLight> retrieveCategoriesForIAView(String docReference) {
        MongoInformationAssetView iaViewWithCategories = informationAssetViewMongoRepository
                .findByDocReference(docReference);
        List<CategoryLight> categories = iaViewWithCategories.getCategories();
        return categories;
    }

    @Override
    public void bulkUpdateCategoriesOnIAViews(List<IAViewUpdate> listOfIAViewUpdatesToProcess) {
        logger.info(".updateCategoriesOnIAView: {}",
                Arrays.toString(retrieveArrayOfDocRefsFromListOfIAViewUpdates(listOfIAViewUpdatesToProcess)));
        List<SolrInputDocument> listOfUpdatesToSubmitToSolr = new ArrayList<SolrInputDocument>();

        for (IAViewUpdate iaViewUpdate : listOfIAViewUpdatesToProcess) {

            SolrInputDocument document = createDocumentForAtomicUpdate(iaViewUpdate.getDocReference(),
                    iaViewUpdate.getCategories());

            listOfUpdatesToSubmitToSolr.add(document);
        }

        solrIAViewRepository.saveAll(listOfUpdatesToSubmitToSolr);
    }

    private String[] retrieveArrayOfDocRefsFromListOfIAViewUpdates(List<IAViewUpdate> listOfIAViewUpdatesToProcess) {
        List<String> listOfDocReferences = new ArrayList<String>();
        for (IAViewUpdate iaViewUpdate : listOfIAViewUpdatesToProcess) {
            listOfDocReferences.add(iaViewUpdate.getDocReference());
        }
        return listOfDocReferences.toArray(new String[0]);
    }

    private SolrInputDocument createDocumentForAtomicUpdate(String docReference, List<CategoryLight> categories) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), docReference);
        List<String> listOfCiaidAndTtls = new ArrayList<String>();
        List<String> listOfCiaids = new ArrayList<String>();
        for (CategoryLight category : categories) {
            listOfCiaidAndTtls.add(category.getCiaidAndTtl());
            listOfCiaids.add(category.getCiaid());
        }
        Map<String, List<String>> partialUpdateOnListOfCiaidAndTtls = new HashMap<>();
        partialUpdateOnListOfCiaidAndTtls.put("set", listOfCiaidAndTtls);
        solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), partialUpdateOnListOfCiaidAndTtls);

        Map<String, List<String>> partialUpdateOnListOfCiaids = new HashMap<>();
        partialUpdateOnListOfCiaids.put("set", listOfCiaids);
        solrInputDocument.addField(InformationAssetViewFields.TAXONOMYID.toString(), partialUpdateOnListOfCiaids);
        return solrInputDocument;
    }

}
