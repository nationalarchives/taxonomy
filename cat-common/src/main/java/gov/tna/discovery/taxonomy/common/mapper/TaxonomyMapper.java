package gov.tna.discovery.taxonomy.common.mapper;

import gov.tna.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import gov.tna.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
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

    public static MongoInformationAssetView getMongoIAViewFromLuceneIAView(
	    InformationAssetView iaViewFromLuceneDocument, long timestamp) {
	MongoInformationAssetView mongoIaView = new MongoInformationAssetView(timestamp);
	mongoIaView.setDocReference(iaViewFromLuceneDocument.getDOCREFERENCE());
	mongoIaView.setCatDocRef(iaViewFromLuceneDocument.getCATDOCREF());
	mongoIaView.setCategories(iaViewFromLuceneDocument.getCATEGORIES());
	mongoIaView.setSeries(iaViewFromLuceneDocument.getSERIES());
	return mongoIaView;
    }

    public static IAViewUpdate getIAViewUpdateFromLuceneIAView(InformationAssetView iaViewFromLuceneDocument,
	    long timestamp) {
	IAViewUpdate iaViewUpdate = new IAViewUpdate(timestamp);
	iaViewUpdate.setDocReference(iaViewFromLuceneDocument.getDOCREFERENCE());
	iaViewUpdate.setCatDocRef(iaViewFromLuceneDocument.getCATDOCREF());
	iaViewUpdate.setCategories(iaViewFromLuceneDocument.getCATEGORIES());
	return iaViewUpdate;
    }

}
