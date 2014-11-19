package gov.tna.discovery.taxonomy.service;

import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.util.List;

/**
 * Service that handles all requests from Taxonomy WS Controller
 * 
 * @author jcharlet
 *
 */
public interface TaxonomyWSService {

    public abstract void publishUpdateOnCategory(String ciaid);

    public abstract List<InformationAssetView> performSearch(String categoryQuery, Float score, Integer limit,
	    Integer offset);

}