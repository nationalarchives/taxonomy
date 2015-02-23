package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.AnalyzerType;

/**
 * General search no stemming case insensitive punctuation removed originals
 * preserved
 * 
 * @author jcharlet
 *
 */
public final class IAViewTextNoCasNoPuncAnalyser extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(IAViewTextNoCasNoPuncAnalyser.class);

    private final Version matchVersion;

    private WordDelimiterFilterFactory wordDelimiterFilterFactory;
    private final SynonymFilterFactory synonymFilterFactory;
    private AnalyzerType analyzerType;
    private int positionIncrementGap;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public IAViewTextNoCasNoPuncAnalyser(Version matchVersion, SynonymFilterFactory synonymFilterFactory,
	    WordDelimiterFilterFactory wordDelimiterFilterFactory, AnalyzerType analyzerType) {
	this.matchVersion = matchVersion;
	this.synonymFilterFactory = synonymFilterFactory;
	this.wordDelimiterFilterFactory = wordDelimiterFilterFactory;
	this.analyzerType = analyzerType;
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
	Tokenizer source = new ClassicTokenizer(reader);

	TokenStream result = null;

	if (AnalyzerType.QUERY.equals(analyzerType)) {
	    if (synonymFilterFactory != null) {
		result = this.synonymFilterFactory.create(source);
	    } else {
		logger.warn(".createComponents: synonymFilter disabled");
	    }
	}
	result = this.wordDelimiterFilterFactory.create(result == null ? source : result);

	result = new EnglishPossessiveFilter(this.matchVersion, result);

	result = new ASCIIFoldingFilter(result);

	result = new LowerCaseFilter(result);

	return new TokenStreamComponents(source, result);
    }

    @Override
    public void close() {
	super.close();
    }

}