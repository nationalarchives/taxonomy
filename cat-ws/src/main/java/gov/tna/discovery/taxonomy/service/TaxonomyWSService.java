package gov.tna.discovery.taxonomy.service;

import gov.tna.discovery.taxonomy.domain.CategoryRelevancy;
import gov.tna.discovery.taxonomy.domain.TestCategoriseSingleRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.util.List;

/**
 * Service that handles all requests from Taxonomy WS Controller
 * 
 * @author jcharlet
 *
 */
public interface TaxonomyWSService {

    void publishUpdateOnCategory(String ciaid);

    List<InformationAssetView> performSearch(String categoryQuery, Float score, Integer limit, Integer offset);

    List<CategoryRelevancy> testCategoriseSingle(TestCategoriseSingleRequest testCategoriseSingleRequest);

}