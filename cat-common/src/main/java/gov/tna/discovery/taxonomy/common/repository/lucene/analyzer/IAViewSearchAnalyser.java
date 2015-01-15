package gov.tna.discovery.taxonomy.common.repository.lucene.analyzer;

import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneHelperTools;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text General Analyser dedicated to querying from Solr configuration for
 * IAViews<br/>
 * Applies the following tokenizers and filters:
 * <ul>
 * <li>WhitespaceTokenizer</li>
 * <li>StopFilter</li>
 * <li>LowerCaseFilter</li>
 * <li>SynonymFilter</li>
 * </ul>
 * 
 * @author jcharlet
 *
 */
public final class IAViewSearchAnalyser extends Analyzer {

    private final Version matchVersion;
    private int positionIncrementGap;

    /**
     * Creates a new {@link WhitespaceAnalyzer}
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public IAViewSearchAnalyser(Version matchVersion) {
	this.matchVersion = matchVersion;
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
	return new TokenStreamComponents(new WhitespaceTokenizer(matchVersion, reader));
    }
}