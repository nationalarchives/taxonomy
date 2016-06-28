/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.AnalyzerType;

/**
 * taxonomy search no stemming case sensitive punctuation removed
 * 
 * @author jcharlet
 *
 */
public final class IAViewTextCasNoPuncAnalyser extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(IAViewTextCasNoPuncAnalyser.class);

    private WordDelimiterFilterFactory wordDelimiterFilterFactory;
    private int positionIncrementGap;
    private final SynonymFilterFactory synonymFilterFactory;
    private AnalyzerType analyzerType;

    /**
     * Creates a new tokenizer
     *
     */
    public IAViewTextCasNoPuncAnalyser(SynonymFilterFactory synonymFilterFactory,
                                       WordDelimiterFilterFactory wordDelimiterFilterFactory, AnalyzerType analyzerType) {
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
    protected TokenStreamComponents createComponents(final String fieldName) {
	Tokenizer source = new ClassicTokenizer();

	TokenStream result = null;

	if (AnalyzerType.QUERY.equals(analyzerType)) {
	    if (synonymFilterFactory != null) {
		result = this.synonymFilterFactory.create(source);
	    } else {
		logger.warn(".createComponents: synonymFilter disabled");
	    }
	}
	result = this.wordDelimiterFilterFactory.create(result == null ? source : result);

	result = new EnglishPossessiveFilter(result);

	result = new ASCIIFoldingFilter(result);

	return new TokenStreamComponents(source, result);
    }

    @Override
    public void close() {
	super.close();
    }

}