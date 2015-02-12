package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;

/**
 * Service dedicated to the retrieval of IAViews
 * 
 * @author jcharlet
 *
 */
public interface IAViewService {

    PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit, Integer offset);

}
