/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "trainingset")
public class TrainingDocument {
    private String _id;
    @Field(value = "TITLE")
    private String title;
    @Field(value = "DESCRIPTION")
    private String description;

    @Field(value = "CATEGORY")
    private String category;

    @Field(value = "DOCREFERENCE")
    private String docReference;

    @Field(value = "CONTEXTDESCRIPTION")
    private String contextDescription;

    @Field(value = "CATDOCREF")
    private String catDocRef;
    @Field(value = "CORPBODYS")
    private String[] corpBodys;
    @Field(value = "SUBJECTS")
    private String[] subjects;
    @Field(value = "PLACE_NAME")
    private String[] placeName;
    @Field(value = "PERSON_FULLNAME")
    private String[] personFullName;

    public String get_id() {
	return _id;
    }

    public void set_id(String _id) {
	this._id = _id;
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

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
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
