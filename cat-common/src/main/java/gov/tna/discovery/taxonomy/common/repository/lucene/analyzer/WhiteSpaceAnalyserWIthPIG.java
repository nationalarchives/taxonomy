package gov.tna.discovery.taxonomy.common.repository.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

/**
 * Modification of WhiteSpaceAnalyser with setPositionIncrementGap implemented
 * 
 * @author jcharlet
 *
 */
public final class WhiteSpaceAnalyserWIthPIG extends Analyzer {
    private int positionIncrementGap;

    /**
     * Creates a new {@link WhitespaceAnalyzer}
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public WhiteSpaceAnalyserWIthPIG() {
    }

    @Override
    public int getPositionIncrementGap(String fieldName) {
	return this.positionIncrementGap;
    }

    public void setPositionIncrementGap(int positionIncrementGap) {
	this.positionIncrementGap = positionIncrementGap;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
	return new TokenStreamComponents(new WhitespaceTokenizer(reader));
    }
}