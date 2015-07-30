/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "testdocuments")
public class TestDocument {

    private String[] legacyCategories;

    private String title;
    private String description;

    private String[] categories;

    @Id
    private String docReference;

    private String contextDescription;

    private String catDocRef;
    private String[] corpBodys;
    private String[] subjects;
    private String[] placeName;
    private String[] personFullName;

    public TestDocument() {
	super();
    }

    public String[] getLegacyCategories() {
	return legacyCategories;
    }

    public void setLegacyCategories(String[] legacyCategories) {
	this.legacyCategories = legacyCategories;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String[] getCategories() {
	return categories;
    }

    public void setCategories(String[] categories) {
	this.categories = categories;
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

    public String getContextDescription() {
	return contextDescription;
    }

    public void setContextDescription(String contextDescription) {
	this.contextDescription = contextDescription;
    }

    public String getCatDocRef() {
	return catDocRef;
    }

    public void setCatDocRef(String catDocRef) {
	this.catDocRef = catDocRef;
    }

    public String[] getCorpBodys() {
	return corpBodys;
    }

    public void setCorpBodys(String[] corpBodys) {
	this.corpBodys = corpBodys;
    }

    public String[] getSubjects() {
	return subjects;
    }

    public void setSubjects(String[] subjects) {
	this.subjects = subjects;
    }

    public String[] getPlaceName() {
	return placeName;
    }

    public void setPlaceName(String[] placeName) {
	this.placeName = placeName;
    }

    public String[] getPersonFullName() {
	return personFullName;
    }

    public void setPersonFullName(String[] personFullName) {
	this.personFullName = personFullName;
    }

}
