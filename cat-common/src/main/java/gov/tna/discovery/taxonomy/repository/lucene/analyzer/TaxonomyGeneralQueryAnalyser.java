package gov.tna.discovery.taxonomy.repository.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.util.Version;

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
public final class TaxonomyGeneralQueryAnalyser extends Analyzer {

    private final Version matchVersion;

    private final StopFilterFactory stopFilterFactory;

    private final SynonymFilterFactory synonymFilterFactory;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public TaxonomyGeneralQueryAnalyser(Version matchVersion, StopFilterFactory stopFilterFactory,
	    SynonymFilterFactory synonymFilterFactory) {
	this.matchVersion = matchVersion;
	this.stopFilterFactory = stopFilterFactory;
	this.synonymFilterFactory = synonymFilterFactory;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
	Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);

	TokenStream result = this.stopFilterFactory.create(source);

	result = new LowerCaseFilter(matchVersion, result);

	result = synonymFilterFactory.create(result);

	return new TokenStreamComponents(source, result);
    }

}