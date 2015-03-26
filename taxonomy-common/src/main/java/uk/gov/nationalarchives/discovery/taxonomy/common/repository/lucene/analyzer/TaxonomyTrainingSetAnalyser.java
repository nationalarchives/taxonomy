package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
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
public final class TaxonomyTrainingSetAnalyser extends Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyTrainingSetAnalyser.class);

    private final StopFilterFactory stopFilterFactory;

    private final SynonymFilterFactory synonymFilterFactory;

    private Integer maxShingleSize;

    private TokenStream result;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public TaxonomyTrainingSetAnalyser(StopFilterFactory stopFilterFactory, SynonymFilterFactory synonymFilterFactory,
	    Integer maxShingleSize) {
	this.stopFilterFactory = stopFilterFactory;
	this.synonymFilterFactory = synonymFilterFactory;
	this.maxShingleSize = maxShingleSize;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
	Tokenizer source = new WhitespaceTokenizer(reader);

	result = new LowerCaseFilter(source);

	if (stopFilterFactory != null) {
	    result = this.stopFilterFactory.create(result);
	} else {
	    logger.warn(".createComponents: stopFilter disabled");
	}

	if (synonymFilterFactory != null) {
	    result = this.synonymFilterFactory.create(result);
	} else {
	    logger.warn(".createComponents: synonymFilter disabled");
	}

	if (maxShingleSize != null && maxShingleSize >= 2) {
	    result = new ShingleFilter(result, this.maxShingleSize);
	} else {
	    logger.warn(".createComponents: shingleFilter disabled");
	}

	return new TokenStreamComponents(source, result);
    }

    @Override
    public void close() {
	LuceneHelperTools.closeCloseableObjectQuietly(result);
	super.close();
    }

}