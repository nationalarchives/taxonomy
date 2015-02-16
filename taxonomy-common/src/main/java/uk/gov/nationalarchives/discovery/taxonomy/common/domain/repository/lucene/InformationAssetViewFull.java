package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene;

import java.util.List;

public class InformationAssetViewFull {
    private String _id;
    private String DOCREFERENCE;
    private String URLPARAMS;
    private String SOURCE;
    private String SOURCELEVEL;
    private String SCHEMA;
    private String DEPARTMENT;
    private String DEPARTMENTFILTER;
    private String SERIES;
    private String CATDOCREF;
    private String CATDOCREFTEXT;
    private String CATDOCREFSORT;
    private String[] REFERENCES;
    private String[] REFERENCESTEXT;
    private String TITLE;
    private String DESCRIPTION;
    private String CONTEXTDESCRIPTION;
    private String NOTE;
    private String ADINHIST;
    private String ARRANGEMENT;
    private String[] CORPBODYS;
    private String[] SUBJECTS;
    private String[] PLACE_NAME;
    private String[] PLACE_TOWN;
    private String[] PLACE_COUNTY;
    private String[] PLACE_REGION;
    private String[] PLACE_COUNTRY;
    private String[] PERIODS;
    private String STARTDATE;
    private String ENDDATE;
    private String NUMSTARTDATE;
    private String NUMENDDATE;
    private String COVERINGDATES;
    private String CLOSURECODE;
    private String CLOSURETYPE;
    private String CLOSURESTATUS;
    private String CLOSURE;
    private String OPENINGDATE;
    private String[] PERSON_TNAME;
    private String[] PERSON_FNAME;
    private String[] PERSON_SNAME;
    private String[] PERSON_FULLNAME;
    private String[] PERSON_NATIONALITY;
    private String[] PERSON_STATS;
    private String[] PERSON_DOB;
    private String[] PERSON_REFS;
    private String[] HB;
    private String[] HBTEXT;
    private String[] HBCODE;
    private String[] HBTYPECODE;
    private String MAPDESIGNATION;
    private String MAPSCALE;
    private String PHYSICALCOND;
    private String CATEGORIES;

    // search properties
    private float score;

    public float getScore() {
	return score;
    }

    public void setScore(float score) {
	this.score = score;
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

    public String getDOCREFERENCE() {
	return DOCREFERENCE;
    }

    public void setDOCREFERENCE(String dOCREFERENCE) {
	DOCREFERENCE = dOCREFERENCE;
    }

    public String getURLPARAMS() {
	return URLPARAMS;
    }

    public void setURLPARAMS(String uRLPARAMS) {
	URLPARAMS = uRLPARAMS;
    }

    public String getSOURCE() {
	return SOURCE;
    }

    public void setSOURCE(String sOURCE) {
	SOURCE = sOURCE;
    }

    public String getSOURCELEVEL() {
	return SOURCELEVEL;
    }

    public void setSOURCELEVEL(String sOURCELEVEL) {
	SOURCELEVEL = sOURCELEVEL;
    }

    public String getSCHEMA() {
	return SCHEMA;
    }

    public void setSCHEMA(String sCHEMA) {
	SCHEMA = sCHEMA;
    }

    public String getDEPARTMENT() {
	return DEPARTMENT;
    }

    public void setDEPARTMENT(String dEPARTMENT) {
	DEPARTMENT = dEPARTMENT;
    }

    public String getDEPARTMENTFILTER() {
	return DEPARTMENTFILTER;
    }

    public void setDEPARTMENTFILTER(String dEPARTMENTFILTER) {
	DEPARTMENTFILTER = dEPARTMENTFILTER;
    }

    public String getSERIES() {
	return SERIES;
    }

    public void setSERIES(String sERIES) {
	SERIES = sERIES;
    }

    public String getCATDOCREF() {
	return CATDOCREF;
    }

    public void setCATDOCREF(String cATDOCREF) {
	CATDOCREF = cATDOCREF;
    }

    public String getCATDOCREFTEXT() {
	return CATDOCREFTEXT;
    }

    public void setCATDOCREFTEXT(String cATDOCREFTEXT) {
	CATDOCREFTEXT = cATDOCREFTEXT;
    }

    public String getCATDOCREFSORT() {
	return CATDOCREFSORT;
    }

    public void setCATDOCREFSORT(String cATDOCREFSORT) {
	CATDOCREFSORT = cATDOCREFSORT;
    }

    public String[] getREFERENCES() {
	return REFERENCES;
    }

    public void setREFERENCES(String[] rEFERENCES) {
	REFERENCES = rEFERENCES;
    }

    public String[] getREFERENCESTEXT() {
	return REFERENCESTEXT;
    }

    public void setREFERENCESTEXT(String[] rEFERENCESTEXT) {
	REFERENCESTEXT = rEFERENCESTEXT;
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

    public String getNOTE() {
	return NOTE;
    }

    public void setNOTE(String nOTE) {
	NOTE = nOTE;
    }

    public String getADINHIST() {
	return ADINHIST;
    }

    public void setADINHIST(String aDINHIST) {
	ADINHIST = aDINHIST;
    }

    public String getARRANGEMENT() {
	return ARRANGEMENT;
    }

    public void setARRANGEMENT(String aRRANGEMENT) {
	ARRANGEMENT = aRRANGEMENT;
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

    public String[] getPERIODS() {
	return PERIODS;
    }

    public void setPERIODS(String[] pERIODS) {
	PERIODS = pERIODS;
    }

    public String getSTARTDATE() {
	return STARTDATE;
    }

    public void setSTARTDATE(String sTARTDATE) {
	STARTDATE = sTARTDATE;
    }

    public String getENDDATE() {
	return ENDDATE;
    }

    public void setENDDATE(String eNDDATE) {
	ENDDATE = eNDDATE;
    }

    public String getNUMSTARTDATE() {
	return NUMSTARTDATE;
    }

    public void setNUMSTARTDATE(String nUMSTARTDATE) {
	NUMSTARTDATE = nUMSTARTDATE;
    }

    public String getNUMENDDATE() {
	return NUMENDDATE;
    }

    public void setNUMENDDATE(String nUMENDDATE) {
	NUMENDDATE = nUMENDDATE;
    }

    public String getCOVERINGDATES() {
	return COVERINGDATES;
    }

    public void setCOVERINGDATES(String cOVERINGDATES) {
	COVERINGDATES = cOVERINGDATES;
    }

    public String getCLOSURECODE() {
	return CLOSURECODE;
    }

    public void setCLOSURECODE(String cLOSURECODE) {
	CLOSURECODE = cLOSURECODE;
    }

    public String getCLOSURETYPE() {
	return CLOSURETYPE;
    }

    public void setCLOSURETYPE(String cLOSURETYPE) {
	CLOSURETYPE = cLOSURETYPE;
    }

    public String getCLOSURESTATUS() {
	return CLOSURESTATUS;
    }

    public void setCLOSURESTATUS(String cLOSURESTATUS) {
	CLOSURESTATUS = cLOSURESTATUS;
    }

    public String getCLOSURE() {
	return CLOSURE;
    }

    public void setCLOSURE(String cLOSURE) {
	CLOSURE = cLOSURE;
    }

    public String getOPENINGDATE() {
	return OPENINGDATE;
    }

    public void setOPENINGDATE(String oPENINGDATE) {
	OPENINGDATE = oPENINGDATE;
    }

    public String[] getPERSON_TNAME() {
	return PERSON_TNAME;
    }

    public void setPERSON_TNAME(String[] pERSON_TNAME) {
	PERSON_TNAME = pERSON_TNAME;
    }

    public String[] getPERSON_FNAME() {
	return PERSON_FNAME;
    }

    public void setPERSON_FNAME(String[] pERSON_FNAME) {
	PERSON_FNAME = pERSON_FNAME;
    }

    public String[] getPERSON_SNAME() {
	return PERSON_SNAME;
    }

    public void setPERSON_SNAME(String[] pERSON_SNAME) {
	PERSON_SNAME = pERSON_SNAME;
    }

    public String[] getPERSON_FULLNAME() {
	return PERSON_FULLNAME;
    }

    public void setPERSON_FULLNAME(String[] pERSON_FULLNAME) {
	PERSON_FULLNAME = pERSON_FULLNAME;
    }

    public String[] getPERSON_NATIONALITY() {
	return PERSON_NATIONALITY;
    }

    public void setPERSON_NATIONALITY(String[] pERSON_NATIONALITY) {
	PERSON_NATIONALITY = pERSON_NATIONALITY;
    }

    public String[] getPERSON_STATS() {
	return PERSON_STATS;
    }

    public void setPERSON_STATS(String[] pERSON_STATS) {
	PERSON_STATS = pERSON_STATS;
    }

    public String[] getPERSON_DOB() {
	return PERSON_DOB;
    }

    public void setPERSON_DOB(String[] pERSON_DOB) {
	PERSON_DOB = pERSON_DOB;
    }

    public String[] getPERSON_REFS() {
	return PERSON_REFS;
    }

    public void setPERSON_REFS(String[] pERSON_REFS) {
	PERSON_REFS = pERSON_REFS;
    }

    public String[] getHB() {
	return HB;
    }

    public void setHB(String[] hB) {
	HB = hB;
    }

    public String[] getHBTEXT() {
	return HBTEXT;
    }

    public void setHBTEXT(String[] hBTEXT) {
	HBTEXT = hBTEXT;
    }

    public String[] getHBCODE() {
	return HBCODE;
    }

    public void setHBCODE(String[] hBCODE) {
	HBCODE = hBCODE;
    }

    public String[] getHBTYPECODE() {
	return HBTYPECODE;
    }

    public void setHBTYPECODE(String[] hBTYPECODE) {
	HBTYPECODE = hBTYPECODE;
    }

    public String getMAPDESIGNATION() {
	return MAPDESIGNATION;
    }

    public void setMAPDESIGNATION(String mAPDESIGNATION) {
	MAPDESIGNATION = mAPDESIGNATION;
    }

    public String getMAPSCALE() {
	return MAPSCALE;
    }

    public void setMAPSCALE(String mAPSCALE) {
	MAPSCALE = mAPSCALE;
    }

    public String getPHYSICALCOND() {
	return PHYSICALCOND;
    }

    public void setPHYSICALCOND(String pHYSICALCOND) {
	PHYSICALCOND = pHYSICALCOND;
    }

    public String getCATEGORIES() {
	return CATEGORIES;
    }

    public void setCATEGORIES(String CATEGORIES) {
	this.CATEGORIES = CATEGORIES;
    }

    public String getCONTEXTDESCRIPTION() {
	return CONTEXTDESCRIPTION;
    }

    public void setCONTEXTDESCRIPTION(String cONTEXTDESCRIPTION) {
	CONTEXTDESCRIPTION = cONTEXTDESCRIPTION;
    }

}
