package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.UpdateSolrService;

@Service
public class UpdateSolrServiceImpl implements UpdateSolrService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(UpdateSolrServiceImpl.class);

    @Autowired
    private SolrIAViewRepository solrIAViewRepository;

    @Autowired
    private InformationAssetViewMongoRepository informationAssetViewMongoRepository;

    @Override
    public void updateCategoriesOnIAView(String docReference) {
	SolrDocument iaView = solrIAViewRepository.getByDocReference(docReference);

	String[] categories = retrieveCategoriesForIAView(docReference);

	SolrInputDocument document = createDocumentForUpdate(iaView, categories);

	solrIAViewRepository.save(document);
    }

    private SolrInputDocument createDocumentForUpdate(SolrDocument iaView, String[] categories) {
	iaView.removeFields(InformationAssetViewFields.TAXONOMY.toString());
	iaView.addField(InformationAssetViewFields.TAXONOMY.toString(), categories);

	// TODO 1 Check that the solr doc updated has still all other fields:
	// search with DOCREF:"" & text_gen:""
	// TODO 1 what about boost? are there any in schema.xml?
	SolrInputDocument document = ClientUtils.toSolrInputDocument(iaView);
	return document;
    }

    private String[] retrieveCategoriesForIAView(String docReference) {
	MongoInformationAssetView iaViewWithCategories = informationAssetViewMongoRepository
		.findByDocReference(docReference);
	String[] categories = iaViewWithCategories.getCategories();
	return categories;
    }

    @Override
    public void bulkUpdateCategoriesOnIAViews(String[] docReferences) {
	List<SolrInputDocument> listOfUpdateDocuments = new ArrayList<SolrInputDocument>();

	for (String docReference : docReferences) {
	    SolrDocument iaView = solrIAViewRepository.getByDocReference(docReference);

	    String[] categories = retrieveCategoriesForIAView(docReference);

	    SolrInputDocument document = createDocumentForUpdate(iaView, categories);

	    listOfUpdateDocuments.add(document);
	}

	solrIAViewRepository.saveAll(listOfUpdateDocuments);
    }

    @Override
    public void bulkUpdateCategoriesOnIAViewsOnTimeRange(long start, long end) {
	// TODO Auto-generated method stub

    }
}
