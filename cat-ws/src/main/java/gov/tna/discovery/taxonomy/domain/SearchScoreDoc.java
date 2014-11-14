package gov.tna.discovery.taxonomy.domain;

public class SearchScoreDoc {

    /** The score of this document for the query. */
    private Float score;

    /** A hit document's number. */
    private Integer doc;

    /** Only set by */
    private Integer shardIndex;

    /** Constructs a ScoreDoc. */
    public SearchScoreDoc() {
	super();
    }

    public SearchScoreDoc(int doc, float score, int shardIndex) {
	super();
	this.score = score;
	this.doc = doc;
	this.shardIndex = shardIndex;
    }

    // A convenience method for debugging.
    @Override
    public String toString() {
	return "doc=" + doc + " score=" + score + " shardIndex=" + shardIndex;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getDoc() {
        return doc;
    }

    public void setDoc(Integer doc) {
        this.doc = doc;
    }

    public Integer getShardIndex() {
        return shardIndex;
    }

    public void setShardIndex(Integer shardIndex) {
        this.shardIndex = shardIndex;
    }
    
    
}
