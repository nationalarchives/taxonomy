/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * IAView with its categories <br/>
 * this collection contains the latest results of categorisation for every
 * unique IAView categorised.<br/>
 * It's a copy of the IAView Solr/Lucene index with only docRefs and categories
 * 
 * @author jcharlet
 *
 */
@Document(collection = "iaViews")
public class MongoInformationAssetView {

    @Id
    private String docReference;

    private Date creationDate;

    private List<CategoryLight> categories;
    private String catDocRef;
    private String series;

    public MongoInformationAssetView(Date creationDate) {
	super();
	this.creationDate = creationDate;
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

    public List<CategoryLight> getCategories() {
	return categories;
    }

    public void setCategories(List<CategoryLight> categories) {
	this.categories = categories;
    }

    public String getCatDocRef() {
	return catDocRef;
    }

    public void setCatDocRef(String catDocRef) {
	this.catDocRef = catDocRef;
    }

    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    public String getSeries() {
	return series;
    }

    public void setSeries(String series) {
	this.series = series;
    }

}
