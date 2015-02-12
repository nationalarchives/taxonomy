package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class InformationAssetView {

    @JsonProperty(value = "docReference")
    private String DOCREFERENCE;
    @JsonProperty(value = "catDocRef")
    private String CATDOCREF;
    @JsonProperty(value = "title")
    private String TITLE;
    @JsonProperty(value = "description")
    private String DESCRIPTION;
    @JsonProperty(value = "corpBodys")
    private String[] CORPBODYS;
    @JsonProperty(value = "subjects")
    private String[] SUBJECTS;
    @JsonProperty(value = "placeName")
    private String[] PLACE_NAME;
    @JsonProperty(value = "personFullName")
    private String[] PERSON_FULLNAME;
    @JsonProperty(value = "contextDescription")
    private String CONTEXTDESCRIPTION;
    @JsonProperty(value = "coveringDates")
    private String COVERINGDATES;

    @JsonProperty(value = "categories")
    private String[] CATEGORIES;
    private Float score;
    @JsonProperty(value = "series")
    private String SERIES;

    public String getDOCREFERENCE() {
	return DOCREFERENCE;
    }

    public void setDOCREFERENCE(String dOCREFERENCE) {
	DOCREFERENCE = dOCREFERENCE;
    }

    public String getCATDOCREF() {
	return CATDOCREF;
    }

    public void setCATDOCREF(String cATDOCREF) {
	CATDOCREF = cATDOCREF;
    }

    public String getTITLE() {
	return TITLE;
    }

    public void setTITLE(String tITLE) {
	TITLE = tITLE;
    }

    public String getDESCRIPTION() {
	return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
	DESCRIPTION = dESCRIPTION;
    }

    public String[] getCORPBODYS() {
	return CORPBODYS;
    }

    public void setCORPBODYS(String[] cORPBODYS) {
	CORPBODYS = cORPBODYS;
    }

    public String[] getSUBJECTS() {
	return SUBJECTS;
    }

    public void setSUBJECTS(String[] sUBJECTS) {
	SUBJECTS = sUBJECTS;
    }

    public String[] getPLACE_NAME() {
	return PLACE_NAME;
    }

    public void setPLACE_NAME(String[] pLACE_NAME) {
	PLACE_NAME = pLACE_NAME;
    }

    public String[] getPERSON_FULLNAME() {
	return PERSON_FULLNAME;
    }

    public void setPERSON_FULLNAME(String[] pERSON_FULLNAME) {
	PERSON_FULLNAME = pERSON_FULLNAME;
    }

    public String getCONTEXTDESCRIPTION() {
	return CONTEXTDESCRIPTION;
    }

    public void setCONTEXTDESCRIPTION(String cONTEXTDESCRIPTION) {
	CONTEXTDESCRIPTION = cONTEXTDESCRIPTION;
    }

    public String getCOVERINGDATES() {
	return COVERINGDATES;
    }

    public void setCOVERINGDATES(String cOVERINGDATES) {
	COVERINGDATES = cOVERINGDATES;
    }

    public String[] getCATEGORIES() {
	return CATEGORIES;
    }

    public void setCATEGORIES(String[] cATEGORIES) {
	CATEGORIES = cATEGORIES;
    }

    public Float getScore() {
	return score;
    }

    public void setScore(Float score) {
	this.score = score;
    }

    public String getSERIES() {
	return SERIES;
    }

    public void setSERIES(String sERIES) {
	SERIES = sERIES;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("InformationAssetView [DOCREFERENCE=");
	builder.append(DOCREFERENCE);
	builder.append(", TITLE=");
	builder.append(TITLE);
	builder.append(", CATEGORIES=");
	builder.append(Arrays.toString(CATEGORIES));
	if (score != null) {
	    builder.append(", SCORE=");
	    builder.append(score);
	}
	builder.append("]");
	return builder.toString();
    }

}
