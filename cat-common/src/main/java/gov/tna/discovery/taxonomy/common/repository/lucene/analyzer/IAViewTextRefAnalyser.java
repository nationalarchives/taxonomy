package gov.tna.discovery.taxonomy.common.repository.lucene.analyzer;

import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneHelperTools;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.util.Version;

/**
 * Analyser dedicated to fieldType textref from Solr Collection
 * 
 * @author jcharlet
 *
 */
public final class IAViewTextRefAnalyser extends Analyzer {

    private final Version matchVersion;

    private TokenStream result;

    private WordDelimiterFilterFactory wordDelimiterFilterFactory;
    private int positionIncrementGap;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public IAViewTextRefAnalyser(Version matchVersion, WordDelimiterFilterFactory wordDelimiterFilterFactory) {
	this.matchVersion = matchVersion;
	this.wordDelimiterFilterFactory = wordDelimiterFilterFactory;
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
	Tokenizer source = new ClassicTokenizer(this.matchVersion, reader);

	result = this.wordDelimiterFilterFactory.create(source);

	result = new EnglishPossessiveFilter(this.matchVersion, result);

	result = new LowerCaseFilter(this.matchVersion, result);

	return new TokenStreamComponents(source, result);
    }

    @Override
    public void close() {
	LuceneHelperTools.closeTokenStreamQuietly(result);
	super.close();
    }

}