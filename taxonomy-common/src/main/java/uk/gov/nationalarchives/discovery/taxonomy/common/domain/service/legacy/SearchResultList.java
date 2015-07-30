/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "IAID", "CitableReference", "Title", "Description", "Places", "People", "Subjects", "StartDate",
	"EndDate", "References", "TnaCustomData", "URLParams", "Department", "XCoord", "YCoord" })
public class SearchResultList {

    @JsonProperty("IAID")
    private String IAID;
    @JsonProperty("CitableReference")
    private String CitableReference;
    @JsonProperty("Title")
    private String Title;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("Places")
    private List<Object> Places = new ArrayList<Object>();
    @JsonProperty("People")
    private List<Object> People = new ArrayList<Object>();
    @JsonProperty("Subjects")
    private List<String> Subjects = new ArrayList<String>();
    @JsonProperty("StartDate")
    private String StartDate;
    @JsonProperty("EndDate")
    private String EndDate;
    @JsonProperty("References")
    private List<String> References = new ArrayList<String>();
    @JsonProperty("TnaCustomData")
    private Object TnaCustomData;
    @JsonProperty("URLParams")
    private String URLParams;
    @JsonProperty("Department")
    private String Department;
    @JsonProperty("XCoord")
    private Object XCoord;
    @JsonProperty("YCoord")
    private Object YCoord;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return The IAID
     */
    @JsonProperty("IAID")
    public String getIAID() {
	return IAID;
    }

    /**
     * 
     * @param IAID
     *            The IAID
     */
    @JsonProperty("IAID")
    public void setIAID(String IAID) {
	this.IAID = IAID;
    }

    /**
     * 
     * @return The CitableReference
     */
    @JsonProperty("CitableReference")
    public String getCitableReference() {
	return CitableReference;
    }

    /**
     * 
     * @param CitableReference
     *            The CitableReference
     */
    @JsonProperty("CitableReference")
    public void setCitableReference(String CitableReference) {
	this.CitableReference = CitableReference;
    }

    /**
     * 
     * @return The Title
     */
    @JsonProperty("Title")
    public String getTitle() {
	return Title;
    }

    /**
     * 
     * @param Title
     *            The Title
     */
    @JsonProperty("Title")
    public void setTitle(String Title) {
	this.Title = Title;
    }

    /**
     * 
     * @return The Description
     */
    @JsonProperty("Description")
    public String getDescription() {
	return Description;
    }

    /**
     * 
     * @param Description
     *            The Description
     */
    @JsonProperty("Description")
    public void setDescription(String Description) {
	this.Description = Description;
    }

    /**
     * 
     * @return The Places
     */
    @JsonProperty("Places")
    public List<Object> getPlaces() {
	return Places;
    }

    /**
     * 
     * @param Places
     *            The Places
     */
    @JsonProperty("Places")
    public void setPlaces(List<Object> Places) {
	this.Places = Places;
    }

    /**
     * 
     * @return The People
     */
    @JsonProperty("People")
    public List<Object> getPeople() {
	return People;
    }

    /**
     * 
     * @param People
     *            The People
     */
    @JsonProperty("People")
    public void setPeople(List<Object> People) {
	this.People = People;
    }

    /**
     * 
     * @return The Subjects
     */
    @JsonProperty("Subjects")
    public List<String> getSubjects() {
	return Subjects;
    }

    /**
     * 
     * @param Subjects
     *            The Subjects
     */
    @JsonProperty("Subjects")
    public void setSubjects(List<String> Subjects) {
	this.Subjects = Subjects;
    }

    /**
     * 
     * @return The StartDate
     */
    @JsonProperty("StartDate")
    public String getStartDate() {
	return StartDate;
    }

    /**
     * 
     * @param StartDate
     *            The StartDate
     */
    @JsonProperty("StartDate")
    public void setStartDate(String StartDate) {
	this.StartDate = StartDate;
    }

    /**
     * 
     * @return The EndDate
     */
    @JsonProperty("EndDate")
    public String getEndDate() {
	return EndDate;
    }

    /**
     * 
     * @param EndDate
     *            The EndDate
     */
    @JsonProperty("EndDate")
    public void setEndDate(String EndDate) {
	this.EndDate = EndDate;
    }

    /**
     * 
     * @return The References
     */
    @JsonProperty("References")
    public List<String> getReferences() {
	return References;
    }

    /**
     * 
     * @param References
     *            The References
     */
    @JsonProperty("References")
    public void setReferences(List<String> References) {
	this.References = References;
    }

    /**
     * 
     * @return The TnaCustomData
     */
    @JsonProperty("TnaCustomData")
    public Object getTnaCustomData() {
	return TnaCustomData;
    }

    /**
     * 
     * @param TnaCustomData
     *            The TnaCustomData
     */
    @JsonProperty("TnaCustomData")
    public void setTnaCustomData(Object TnaCustomData) {
	this.TnaCustomData = TnaCustomData;
    }

    /**
     * 
     * @return The URLParams
     */
    @JsonProperty("URLParams")
    public String getURLParams() {
	return URLParams;
    }

    /**
     * 
     * @param URLParams
     *            The URLParams
     */
    @JsonProperty("URLParams")
    public void setURLParams(String URLParams) {
	this.URLParams = URLParams;
    }

    /**
     * 
     * @return The Department
     */
    @JsonProperty("Department")
    public String getDepartment() {
	return Department;
    }

    /**
     * 
     * @param Department
     *            The Department
     */
    @JsonProperty("Department")
    public void setDepartment(String Department) {
	this.Department = Department;
    }

    /**
     * 
     * @return The XCoord
     */
    @JsonProperty("XCoord")
    public Object getXCoord() {
	return XCoord;
    }

    /**
     * 
     * @param XCoord
     *            The XCoord
     */
    @JsonProperty("XCoord")
    public void setXCoord(Object XCoord) {
	this.XCoord = XCoord;
    }

    /**
     * 
     * @return The YCoord
     */
    @JsonProperty("YCoord")
    public Object getYCoord() {
	return YCoord;
    }

    /**
     * 
     * @param YCoord
     *            The YCoord
     */
    @JsonProperty("YCoord")
    public void setYCoord(Object YCoord) {
	this.YCoord = YCoord;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("SearchResultList [IAID=");
	builder.append(IAID);
	builder.append(", CitableReference=");
	builder.append(CitableReference);
	builder.append(", Title=");
	builder.append(Title);
	builder.append(", Subjects=");
	builder.append(Subjects);
	builder.append("]");
	return builder.toString();
    }

}
