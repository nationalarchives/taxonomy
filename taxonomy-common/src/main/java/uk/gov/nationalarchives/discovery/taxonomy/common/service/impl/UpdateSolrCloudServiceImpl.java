package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrCloudIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrCloudService;

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

    private SolrInputDocument createDocumentForAtomicUpdate(String docReference, List<CategoryLight> categories) {
	SolrInputDocument solrInputDocument = new SolrInputDocument();
	solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), docReference);
	List<String> listOfCiaidAndTtls = new ArrayList<String>();
	List<String> listOfCiaids = new ArrayList<String>();
	for (CategoryLight category : categories) {
	    listOfCiaidAndTtls.add(category.getCiaidAndTtl());
	    listOfCiaids.add(category.getCiaid());
	}
	addMultiValuedFieldToSolrInputDocument(InformationAssetViewFields.TAXONOMY.toString(), listOfCiaidAndTtls,
		solrInputDocument);
	addMultiValuedFieldToSolrInputDocument(InformationAssetViewFields.TAXONOMYID.toString(), listOfCiaids,
		solrInputDocument);
	resetFieldOnSolrDocument(InformationAssetViewFields.TAXONOMY.toString(), solrInputDocument);
	resetFieldOnSolrDocument(InformationAssetViewFields.TAXONOMYID.toString(), solrInputDocument);
	return solrInputDocument;
    }

    private void resetFieldOnSolrDocument(String fieldToReset, SolrInputDocument solrInputDocument) {
	Map<String, Object> removeFieldModifier = new HashMap<>(1);
	removeFieldModifier.put(FIELD_MODIFIER_KEY_SET, null);
	solrInputDocument.addField(fieldToReset, removeFieldModifier);
    }

    private void addMultiValuedFieldToSolrInputDocument(String fieldKey, List<String> fieldValues,
	    SolrInputDocument solrInputDocument) {
	Map<String, List<String>> setFieldModifier = new HashMap<String, List<String>>();
	setFieldModifier.put(FIELD_MODIFIER_KEY_SET, fieldValues);
	solrInputDocument.addField(fieldKey, setFieldModifier);
    }
}
