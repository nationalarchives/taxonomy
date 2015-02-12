package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.domain.service.PaginatedList;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.service.IAViewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IAViewServiceImpl implements IAViewService {

    @Autowired
    private IAViewRepository iaViewRepository;

    @Override
    public PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit,
	    Integer offset) {
	return iaViewRepository.performSearch(categoryQuery, score, limit, offset);
    }

}
