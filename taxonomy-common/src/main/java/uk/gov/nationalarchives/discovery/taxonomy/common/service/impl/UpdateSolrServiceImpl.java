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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryLight;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrService;

@Service
@ConditionalOnProperty(prefix = "solr.", value = "host")
public class UpdateSolrServiceImpl implements UpdateSolrService {

    private static final String FIELD_MODIFIER_KEY_ADD = "add";

    private static final String FIELD_MODIFIER_KEY_SET = "set";

    private static final Logger logger = LoggerFactory.getLogger(UpdateSolrServiceImpl.class);

    @Autowired
    private SolrIAViewRepository solrIAViewRepository;

    @Autowired
    private InformationAssetViewMongoRepository informationAssetViewMongoRepository;

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
    public void bulkUpdateCategoriesOnIAViews(String[] docReferences) {
	logger.info(".updateCategoriesOnIAView: {}", Arrays.toString(docReferences));
	List<SolrInputDocument> listOfUpdatesToSubmitToSolr = new ArrayList<SolrInputDocument>();

	for (String docReference : docReferences) {

	    List<CategoryLight> categories = retrieveCategoriesForIAView(docReference);

	    SolrInputDocument document = createDocumentForAtomicUpdate(docReference, categories);

	    listOfUpdatesToSubmitToSolr.add(document);
	}

	solrIAViewRepository.saveAll(listOfUpdatesToSubmitToSolr);
    }

    @Override
    public void bulkUpdateCategoriesOnIAViewsOnTimeRange(long start, long end) {
	// TODO Auto-generated method stub

    }

    private SolrInputDocument createDocumentForAtomicUpdate(String docReference, List<CategoryLight> categories) {
	SolrInputDocument solrInputDocument = new SolrInputDocument();
	solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), docReference);
	for (CategoryLight category : categories) {
	    addFieldToSolrInputDocument(InformationAssetViewFields.TAXONOMY.toString(), category.getCiaidAndTtl(),
		    solrInputDocument);
	    addFieldToSolrInputDocument(InformationAssetViewFields.TAXONOMYID.toString(), category.getCiaid(),
		    solrInputDocument);
	}
	resetFieldOnSolrDocument(InformationAssetViewFields.TAXONOMY.toString(), solrInputDocument);
	resetFieldOnSolrDocument(InformationAssetViewFields.TAXONOMYID.toString(), solrInputDocument);
	return solrInputDocument;
    }

    private void resetFieldOnSolrDocument(String fieldToReset, SolrInputDocument solrInputDocument) {
	Map<String, Object> removeFieldModifier = new HashMap<>(1);
	removeFieldModifier.put(FIELD_MODIFIER_KEY_SET, null);
	solrInputDocument.addField(fieldToReset, removeFieldModifier);
    }

    private void addFieldToSolrInputDocument(String fieldKey, String fieldValue, SolrInputDocument solrInputDocument) {
	Map<String, Object> addFieldModifier = new HashMap<>(1);
	addFieldModifier.put(FIELD_MODIFIER_KEY_ADD, fieldValue);
	solrInputDocument.addField(fieldKey, addFieldModifier);
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
