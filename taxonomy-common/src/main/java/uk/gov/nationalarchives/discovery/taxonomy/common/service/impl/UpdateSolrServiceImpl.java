package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrService;

@Service
public class UpdateSolrServiceImpl implements UpdateSolrService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSolrServiceImpl.class);

    @Autowired
    private SolrIAViewRepository solrIAViewRepository;

    @Autowired
    private InformationAssetViewMongoRepository informationAssetViewMongoRepository;

    @Override
    public void updateCategoriesOnIAView(String docReference) {
	logger.info(".updateCategoriesOnIAView: {}", docReference);

	String[] categories = retrieveCategoriesForIAView(docReference);

	SolrInputDocument document = createDocumentForAtomicUpdate(docReference, categories);

	solrIAViewRepository.save(document);
    }

    private String[] retrieveCategoriesForIAView(String docReference) {
	MongoInformationAssetView iaViewWithCategories = informationAssetViewMongoRepository
		.findByDocReference(docReference);
	String[] categories = iaViewWithCategories.getCategories();
	return categories;
    }

    @Override
    public void bulkUpdateCategoriesOnIAViews(String[] docReferences) {
	logger.info(".updateCategoriesOnIAView: {}", Arrays.toString(docReferences));
	List<SolrInputDocument> listOfUpdatesToSubmitToSolr = new ArrayList<SolrInputDocument>();

	for (String docReference : docReferences) {

	    String[] categories = retrieveCategoriesForIAView(docReference);

	    SolrInputDocument document = createDocumentForAtomicUpdate(docReference, categories);

	    listOfUpdatesToSubmitToSolr.add(document);
	}

	solrIAViewRepository.saveAll(listOfUpdatesToSubmitToSolr);
    }

    @Override
    public void bulkUpdateCategoriesOnIAViewsOnTimeRange(long start, long end) {
	// TODO Auto-generated method stub

    }

    private SolrInputDocument createDocumentForAtomicUpdate(String docReference, String[] categories) {
	SolrInputDocument solrInputDocument = new SolrInputDocument();
	solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), docReference);
	for (String category : categories) {
	    Map<String, Object> addFieldModifier = new HashMap<>(1);
	    addFieldModifier.put("add", category);
	    solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), addFieldModifier);
	}
	Map<String, Object> removeFieldModifier = new HashMap<>(1);
	removeFieldModifier.put("set", null);
	solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), removeFieldModifier);
	return solrInputDocument;
    }

    @Deprecated
    private SolrInputDocument createDocumentForOverwriteUpdate(String docReference, String[] categories) {
	SolrDocument iaView = solrIAViewRepository.getByDocReference(docReference);

	iaView.removeFields(InformationAssetViewFields.TAXONOMY.toString());
	iaView.addField(InformationAssetViewFields.TAXONOMY.toString(), categories);

	// TODO 1 Check that the solr doc updated has still all other fields:
	// search with DOCREF:"" & text_gen:""
	// TODO 1 what about boost? are there any in schema.xml?
	SolrInputDocument document = ClientUtils.toSolrInputDocument(iaView);
	return document;
    }
}
