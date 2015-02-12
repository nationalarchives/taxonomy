package gov.tna.discovery.taxonomy.common.mapper;

import gov.tna.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.domain.repository.mongo.TestDocument;

public class TaxonomyMapper {
    public static TestDocument getTestDocumentFromIAView(InformationAssetView iaView) {
	TestDocument testDocument = new TestDocument();
	testDocument.setDescription(iaView.getDESCRIPTION());
	testDocument.setContextDescription(iaView.getCONTEXTDESCRIPTION());
	testDocument.setTitle(iaView.getTITLE());
	testDocument.setDocReference(iaView.getDOCREFERENCE());
	testDocument.setCatDocRef(iaView.getCATDOCREF());
	testDocument.setCorpBodys(iaView.getCORPBODYS());
	testDocument.setPersonFullName(iaView.getPERSON_FULLNAME());
	testDocument.setPlaceName(iaView.getPLACE_NAME());
	testDocument.setSubjects(iaView.getSUBJECTS());
	return testDocument;
    }

    public static InformationAssetView getIAViewFromTestDocument(TestDocument testDocument) {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION(testDocument.getDescription());
	iaView.setCONTEXTDESCRIPTION(testDocument.getContextDescription());
	iaView.setTITLE(testDocument.getTitle());
	iaView.setDOCREFERENCE(testDocument.getDocReference());
	iaView.setCATDOCREF(testDocument.getCatDocRef());
	iaView.setCORPBODYS(testDocument.getCorpBodys());
	iaView.setPERSON_FULLNAME(testDocument.getPersonFullName());
	iaView.setPLACE_NAME(testDocument.getPlaceName());
	iaView.setSUBJECTS(testDocument.getSubjects());
	return iaView;
    }

}
