package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import org.apache.lucene.search.ScoreDoc;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;

/**
 * Service dedicated to the retrieval of IAViews
 * 
 * @author jcharlet
 *
 */
public interface IAViewService {

    /**
     * perform a search on IAViews
     * 
     * @param query
     *            query to search
     * @param score
     *            score to use as threshold on the results
     * @param limit
     * @param offset
     * @return
     */
    PaginatedList<InformationAssetView> performSearch(String query, Double score, Integer limit, Integer offset);

    /**
     * get total nb of IAViews
     * 
     * @return
     */
    int getTotalNbOfDocs();

    /**
     * Finds the top n hits from whole Index where all results are after a
     * previous result (after)
     * 
     * @param after
     *            the last doc from previous search
     * @param nDocs
     *            nb of elements to retrieve in total
     * @return
     */
    BrowseAllDocsResponse browseAllDocs(ScoreDoc after, int size);

}
