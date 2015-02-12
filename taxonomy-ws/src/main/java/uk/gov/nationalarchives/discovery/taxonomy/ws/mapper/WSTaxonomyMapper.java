package uk.gov.nationalarchives.discovery.taxonomy.ws.mapper;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TestCategoriseSingleRequest;

public class WSTaxonomyMapper {

    public static InformationAssetView getIAviewFromRequest(TestCategoriseSingleRequest testCategoriseSingleRequest) {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCONTEXTDESCRIPTION(testCategoriseSingleRequest.getContextDescription());
	iaView.setDESCRIPTION(testCategoriseSingleRequest.getDescription());
	iaView.setTITLE(testCategoriseSingleRequest.getTitle());
	iaView.setCATDOCREF(testCategoriseSingleRequest.getCatDocRef());
	iaView.setCORPBODYS(testCategoriseSingleRequest.getCorpBodys());
	iaView.setCOVERINGDATES(testCategoriseSingleRequest.getCoveringDates());
	iaView.setDOCREFERENCE(testCategoriseSingleRequest.getDocReference());
	iaView.setPERSON_FULLNAME(testCategoriseSingleRequest.getPerson_FULLNAME());
	iaView.setPLACE_NAME(testCategoriseSingleRequest.getPlace_NAME());
	iaView.setSUBJECTS(testCategoriseSingleRequest.getSubjects());
	return iaView;
    }

}
