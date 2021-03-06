/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools;

import java.util.Arrays;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.util.StringUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.TestDocument;

public class LuceneTaxonomyMapper {
    /**
     * Convert a lucene document from a search to InformationAssetView object
     * 
     * @param document
     * @return
     */
    public static InformationAssetView getIAViewFromLuceneDocument(Document document) {
	InformationAssetView assetView = new InformationAssetView();
	assetView.setDOCREFERENCE(document.get(InformationAssetViewFields.DOCREFERENCE.toString()));
	assetView.setTITLE(document.get(InformationAssetViewFields.TITLE.toString()));
	assetView.setDESCRIPTION(document.get(InformationAssetViewFields.DESCRIPTION.toString()));
	assetView.setCATDOCREF(document.get(InformationAssetViewFields.CATDOCREF.toString()));
	assetView.setCONTEXTDESCRIPTION(document.get(InformationAssetViewFields.CONTEXTDESCRIPTION.toString()));
	assetView.setCORPBODYS(document.getValues(InformationAssetViewFields.CORPBODYS.toString()));
	assetView.setCOVERINGDATES(document.get(InformationAssetViewFields.COVERINGDATES.toString()));
	assetView.setPERSON_FULLNAME(document.getValues(InformationAssetViewFields.PERSON_FULLNAME.toString()));
	assetView.setPLACE_NAME(document.getValues(InformationAssetViewFields.PLACE_NAME.toString()));
	assetView.setSUBJECTS(document.getValues(InformationAssetViewFields.SUBJECTS.toString()));
	assetView.setSERIES(document.get(InformationAssetViewFields.SERIES.toString()));
	assetView.setSOURCE(document.get(InformationAssetViewFields.SOURCE.toString()));
	return assetView;
    }

    /**
     * Retrieve the doc reference from a lucene document
     * 
     * @param document
     * @return
     */
    public static String getDocReferenceFromLuceneDocument(Document document) {
	return document.get(InformationAssetViewFields.DOCREFERENCE.toString());
    }

    public static TestDocument getTestDocumentFromLuceneDocument(Document document) {
	TestDocument testDocument = new TestDocument();
	testDocument.setDescription(document.get(InformationAssetViewFields.DESCRIPTION.toString()));
	testDocument.setContextDescription(document.get(InformationAssetViewFields.CONTEXTDESCRIPTION.toString()));
	testDocument.setTitle(document.get(InformationAssetViewFields.TITLE.toString()));
	testDocument.setDocReference(document.get(InformationAssetViewFields.DOCREFERENCE.toString()));
	testDocument.setCatDocRef(document.get(InformationAssetViewFields.CATDOCREF.toString()));
	testDocument.setCorpBodys(document.getValues(InformationAssetViewFields.CORPBODYS.toString()));
	testDocument.setPersonFullName(document.getValues(InformationAssetViewFields.PERSON_FULLNAME.toString()));
	testDocument.setPlaceName(document.getValues(InformationAssetViewFields.PLACE_NAME.toString()));
	testDocument.setSubjects(document.getValues(InformationAssetViewFields.SUBJECTS.toString()));
	return testDocument;
    }

    public static Document getLuceneDocumentFromTrainingDocument(TrainingDocument trainingDocument) {
	Document doc = new Document();

	doc.add(new StringField(InformationAssetViewFields.DOCREFERENCE.toString(), trainingDocument.getDocReference(),
		Field.Store.YES));

	doc.add(new TextField(InformationAssetViewFields.DESCRIPTION.toString(), trainingDocument.getDescription(),
		Field.Store.YES));

	doc.add(new StringField(InformationAssetViewFields.TAXONOMY.toString(), trainingDocument.getCategory(),
		Field.Store.YES));

	if (!StringUtils.isEmpty(trainingDocument.getCatDocRef())) {
	    doc.add(new StringField(InformationAssetViewFields.CATDOCREF.toString(), trainingDocument.getCatDocRef(),
		    Field.Store.YES));
	}
	if (!StringUtils.isEmpty(trainingDocument.getTitle())) {
	    doc.add(new TextField(InformationAssetViewFields.TITLE.toString(), trainingDocument.getTitle(),
		    Field.Store.YES));
	}
	if (!StringUtils.isEmpty(trainingDocument.getContextDescription())) {
	    doc.add(new TextField(InformationAssetViewFields.CONTEXTDESCRIPTION.toString(), trainingDocument
		    .getContextDescription(), Field.Store.YES));
	}
	if (trainingDocument.getCorpBodys() != null) {
	    doc.add(new TextField(InformationAssetViewFields.CORPBODYS.toString(), Arrays.toString(trainingDocument
		    .getCorpBodys()), Field.Store.YES));
	}
	if (trainingDocument.getPersonFullName() != null) {
	    doc.add(new TextField(InformationAssetViewFields.PERSON_FULLNAME.toString(), Arrays
		    .toString(trainingDocument.getPersonFullName()), Field.Store.YES));
	}
	if (trainingDocument.getPlaceName() != null) {
	    doc.add(new TextField(InformationAssetViewFields.PLACE_NAME.toString(), Arrays.toString(trainingDocument
		    .getPlaceName()), Field.Store.YES));
	}
	if (trainingDocument.getSubjects() != null) {
	    doc.add(new TextField(InformationAssetViewFields.SUBJECTS.toString(), Arrays.toString(trainingDocument
		    .getSubjects()), Field.Store.YES));
	}
	return doc;
    }

    public static Document getLuceneDocumentFromIAView(InformationAssetView iaViewSample) {
	Document doc = new Document();
	doc.add(new StringField(InformationAssetViewFields.DOCREFERENCE.toString(), iaViewSample.getDOCREFERENCE(),
		Field.Store.YES));

	doc.add(new TextField(InformationAssetViewFields.DESCRIPTION.toString(), iaViewSample.getDESCRIPTION(),
		Field.Store.YES));

	if (!StringUtils.isEmpty(iaViewSample.getCATDOCREF())) {
	    doc.add(new StringField(InformationAssetViewFields.CATDOCREF.toString(), iaViewSample.getCATDOCREF(),
		    Field.Store.YES));
	}
	if (!StringUtils.isEmpty(iaViewSample.getTITLE())) {
	    doc.add(new TextField(InformationAssetViewFields.TITLE.toString(), iaViewSample.getTITLE(), Field.Store.YES));
	}
	if (!StringUtils.isEmpty(iaViewSample.getCONTEXTDESCRIPTION())) {
	    doc.add(new TextField(InformationAssetViewFields.CONTEXTDESCRIPTION.toString(), iaViewSample
		    .getCONTEXTDESCRIPTION(), Field.Store.YES));
	}
	if (iaViewSample.getCORPBODYS() != null) {
	    doc.add(new TextField(InformationAssetViewFields.CORPBODYS.toString(), Arrays.toString(iaViewSample
		    .getCORPBODYS()), Field.Store.YES));
	}
	if (iaViewSample.getPERSON_FULLNAME() != null) {
	    doc.add(new TextField(InformationAssetViewFields.PERSON_FULLNAME.toString(), Arrays.toString(iaViewSample
		    .getPERSON_FULLNAME()), Field.Store.YES));
	}
	if (iaViewSample.getPLACE_NAME() != null) {
	    doc.add(new TextField(InformationAssetViewFields.PLACE_NAME.toString(), Arrays.toString(iaViewSample
		    .getPLACE_NAME()), Field.Store.YES));
	}
	if (iaViewSample.getSUBJECTS() != null) {
	    doc.add(new TextField(InformationAssetViewFields.SUBJECTS.toString(), Arrays.toString(iaViewSample
		    .getSUBJECTS()), Field.Store.YES));
	}
	if (iaViewSample.getSOURCE() != null) {
	    doc.add(new IntField(InformationAssetViewFields.SOURCE.toString(), Integer.parseInt(iaViewSample
		    .getSOURCE()), Field.Store.YES));
	}
	return doc;
    }

}
