package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor;

public class CategoriseAllDocumentsEpic extends Epic {

    private static final long serialVersionUID = -1804210292355153153L;

    private Integer afterDocNumber;

    public CategoriseAllDocumentsEpic(Integer afterDocNumber) {
	super();
	this.afterDocNumber = afterDocNumber;
    }

    public CategoriseAllDocumentsEpic() {
	super();
    }

    public Integer getAfterDocNumber() {
	return afterDocNumber;
    }

    public void setAfterDocNumber(Integer afterDocNumber) {
	this.afterDocNumber = afterDocNumber;
    }

}
