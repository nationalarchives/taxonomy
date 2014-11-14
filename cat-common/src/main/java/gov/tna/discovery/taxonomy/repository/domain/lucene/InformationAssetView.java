package gov.tna.discovery.taxonomy.repository.domain.lucene;

import java.util.List;

public class InformationAssetView {
	
	private String _id;
	private String URLPARAMS;
	private String CATDOCREF;
	private String TITLE;
	private String DESCRIPTION;
	private String[] CORPBODYS;
	private String[] SUBJECTS;
	private String[] PLACE_NAME;
	private String[] PLACE_TOWN;
	private String[] PLACE_COUNTY;
	private String[] PLACE_REGION;
	private String[] PLACE_COUNTRY;
	private String[] PERSON_FULLNAME;
	private String CATEGORY;


//search properties
	private float score;

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	private Integer shardIndex;
	
	
	public Integer getShardIndex() {
	    return shardIndex;
	}

	public void setShardIndex(Integer shardIndex) {
	    this.shardIndex = shardIndex;
	}

	private Integer doc;
	
	public Integer getDoc() {
	    return doc;
	}

	public void setDoc(Integer doc) {
	    this.doc = doc;
	}

	// category
	private List<String> cids;

	public List<String> getCids() {
		return cids;
	}

	public void setCids(List<String> cids) {
		this.cids = cids;
	}
	
	// //////////////////////
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getURLPARAMS() {
		return URLPARAMS;
	}
	
	public void setURLPARAMS(String uRLPARMAS) {
		URLPARAMS = uRLPARMAS;
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

	public String[] getPLACE_TOWN() {
		return PLACE_TOWN;
	}

	public void setPLACE_TOWN(String[] pLACE_TOWN) {
		PLACE_TOWN = pLACE_TOWN;
	}
	
	public String[] getPLACE_COUNTY() {
		return PLACE_COUNTY;
	}

	public void setPLACE_COUNTY(String[] pLACE_COUNTY) {
		PLACE_COUNTY = pLACE_COUNTY;
	}

	public String[] getPLACE_REGION() {
		return PLACE_REGION;
	}

	public void setPLACE_REGION(String[] pLACE_REGION) {
		PLACE_REGION = pLACE_REGION;
	}

	public String[] getPLACE_COUNTRY() {
		return PLACE_COUNTRY;
	}

	public void setPLACE_COUNTRY(String[] pLACE_COUNTRY) {
		PLACE_COUNTRY = pLACE_COUNTRY;
	}

	public String[] getPERSON_FNAME() {
		return PERSON_FULLNAME;
	}
	
	public void setPERSON_FULLNAME(String[] pERSON_FULLNAME) {
		PERSON_FULLNAME = pERSON_FULLNAME;
	}

	public String getCATEGORY() {
	    return CATEGORY;
	}

	public void setCATEGORY(String cATEGORY) {
	    CATEGORY = cATEGORY;
	}

	public String[] getPERSON_FULLNAME() {
	    return PERSON_FULLNAME;
	}
	
	
		
}		
		
