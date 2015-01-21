package gov.tna.discovery.taxonomy.ws.mapper;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.ws.domain.TestCategoriseSingleRequest;

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
