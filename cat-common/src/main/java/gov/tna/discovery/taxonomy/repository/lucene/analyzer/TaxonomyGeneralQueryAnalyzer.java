package gov.tna.discovery.taxonomy.repository.lucene.analyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.CharsRef;
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
public final class TaxonomyGeneralQueryAnalyzer extends Analyzer {

    private final Version matchVersion;

    /**
     * Creates a new tokenizer
     * 
     * @param matchVersion
     *            Lucene version to match See
     *            {@link <a href="#version">above</a>}
     */
    public TaxonomyGeneralQueryAnalyzer(Version matchVersion) {
	this.matchVersion = matchVersion;
    }

    // FIXME use file synonyms.txt & stopwords.txt
    // FIXME use ignoreCase and other params
    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
	Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);

	CharArraySet stopWords = StopFilter.makeStopSet(matchVersion, "OR,AND");
	TokenStream result = new StopFilter(matchVersion, source, stopWords);

	result = new LowerCaseFilter(matchVersion, result);

	String base1 = "fast";
	String syn1 = "rapid";
	String base2 = "slow";
	String syn2 = "sluggish";

	SynonymMap.Builder sb = new SynonymMap.Builder(true);
	sb.add(new CharsRef(base1), new CharsRef(syn1), true);
	sb.add(new CharsRef(base2), new CharsRef(syn2), true);
	try {
	    SynonymMap smap = sb.build();
	    result = new SynonymFilter(result, smap, true);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return new TokenStreamComponents(source, result);
    }

    // public @Bean StopFilter stopFilterFactory() {
    // Map<String, String> stopFilterArgs = new HashMap<String, String>();
    // stopFilterArgs.put("words",
    // "/home/jcharlet/_workspace/cat/cat-common/src/test/resources/lucene/stopwords.txt");
    // stopFilterArgs.put("enablePositionIncrements", "true");
    // stopFilterArgs.put("ignoreCase", "true");
    // stopFilterArgs.put("luceneMatchVersion", version);
    // return new StopFilter(Version.valueOf(version),
    // }

    // public @Bean SynonymFilterFactory synonymFilter() {
    // Map<String, String> synonymFilterArgs = new HashMap<String, String>();
    // synonymFilterArgs.put("synonyms",
    // "/home/jcharlet/_workspace/cat/cat-common/src/test/resources/lucene/synonyms.txt");
    // synonymFilterArgs.put("expand", "true");
    // synonymFilterArgs.put("ignoreCase", "true");
    // synonymFilterArgs.put("luceneMatchVersion", version);
    // return new SynonymFilterFactory(synonymFilterArgs);
    // }

}