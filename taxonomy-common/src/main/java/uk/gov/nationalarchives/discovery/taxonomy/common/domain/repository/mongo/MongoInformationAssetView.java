package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import java.util.Date;

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

    private String[] categories;
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

    public String[] getCategories() {
	return categories;
    }

    public void setCategories(String[] categories) {
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
