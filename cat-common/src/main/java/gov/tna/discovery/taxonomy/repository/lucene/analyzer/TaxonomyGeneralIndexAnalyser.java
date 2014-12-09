package gov.tna.discovery.taxonomy.repository.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text General Analyser dedicated to indexing from Solr configuration for
 * IAViews<br/>
 * Applies the following tokenizers and filters:
 * <ul>
 * <li>WhitespaceTokenizer</li>
 * <li>StopFilter</li>
 * <li>LowerCaseFilter</li>
 * </ul>
 * 
 * @author jcharlet
 *
 */
public final class TaxonomyGeneralIndexAnalyser extends Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyGeneralIndexAnalyser.class);

    private final Version matchVersion;

    private final StopFilterFactory stopFilterFactory;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public TaxonomyGeneralIndexAnalyser(Version matchVersion, StopFilterFactory stopFilterFactory) {
	this.matchVersion = matchVersion;
	this.stopFilterFactory = stopFilterFactory;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
	Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);

	TokenStream result = stopFilterFactory.create(source);

	result = new LowerCaseFilter(matchVersion, result);

	return new TokenStreamComponents(source, result);
    }

}