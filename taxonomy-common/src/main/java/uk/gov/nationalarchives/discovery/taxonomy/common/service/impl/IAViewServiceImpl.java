package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;

@Service
public class IAViewServiceImpl implements IAViewService {

    @Autowired
    private IAViewRepository iaViewRepository;

    @Override
    public PaginatedList<InformationAssetView> performSearch(String categoryQuery, Double score, Integer limit,
	    Integer offset) {
	return iaViewRepository.performSearch(categoryQuery, score, limit, offset);
    }

    @Override
    public void refreshIAViewIndex() {
	iaViewRepository.refreshIndexUsedForCategorisation();
    }

    private static final int NB_OF_FAKE_DOCS = 20;
    List<String> docReferences;

    @PostConstruct
    private void initPoc() {
	this.docReferences = generateDocRefArray();
    }

    private List<String> generateDocRefArray() {
	List<String> listOfReferences = new ArrayList<String>();
	for (int i = 0; i < NB_OF_FAKE_DOCS; i++) {
	    listOfReferences.add("doc_" + (i + 1));
	}
	return listOfReferences;
    }

    @Override
    public int getTotalNbOfDocs() {
	return iaViewRepository.getTotalNbOfDocs();
    }

    @Override
    public BrowseAllDocsResponse browseAllDocs(ScoreDoc after, int size) {
	return iaViewRepository.browseAllDocs(after, size);
    }

}
