package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.AnalyzerType;

/**
 * taxonomy search no stemming case sensitive punctuation retained
 * 
 * @author jcharlet
 *
 */
public final class IAViewTextCasPuncAnalyser extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(IAViewTextCasPuncAnalyser.class);

    private final StopFilterFactory stopFilterFactory;
    private final SynonymFilterFactory synonymFilterFactory;
    private TokenStream result;
    private AnalyzerType analyzerType;
    private int positionIncrementGap;

    /**
     * Creates a new {@link WhitespaceAnalyzer}
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public IAViewTextCasPuncAnalyser(StopFilterFactory stopFilterFactory, SynonymFilterFactory synonymFilterFactory,
	    AnalyzerType analyzerType) {
	this.stopFilterFactory = stopFilterFactory;
	this.synonymFilterFactory = synonymFilterFactory;
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
	result = null;
	Tokenizer source = new WhitespaceTokenizer(reader);

	if (stopFilterFactory != null) {
	    result = this.stopFilterFactory.create(source);
	} else {
	    logger.warn(".createComponents: stopFilter disabled");
	}

	if (AnalyzerType.QUERY.equals(analyzerType)) {
	    if (synonymFilterFactory != null) {
		result = this.synonymFilterFactory.create(result == null ? source : result);
	    } else {
		logger.warn(".createComponents: synonymFilter disabled");
	    }
	}
	return new TokenStreamComponents(source, result == null ? source : result);
    }
}