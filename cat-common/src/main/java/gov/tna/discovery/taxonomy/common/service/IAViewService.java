package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.domain.service.PaginatedList;

/**
 * Service dedicated to the retrieval of IAViews
 * 
 * @author jcharlet
 *
 */
public interface IAViewService {

    PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit, Integer offset);

}
