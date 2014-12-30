package gov.tna.discovery.taxonomy.common.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

@Service
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class QueryBasedCategoriserServiceImpl implements CategoriserService {

    private static final Logger logger = LoggerFactory.getLogger(QueryBasedCategoriserServiceImpl.class);

    @Override
    public List<CategorisationResult> categoriseIAViewSolrDocument(String catdocref) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void testCategoriseIAViewSolrIndex() throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public List<CategorisationResult> runMlt(Document document) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView) {
	logger.info("QUERY BASED");
	return null;
    }

}
