/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;

/**
 * Parser for Taxonomy<br/>
 * by default Lucene does not handle numeric values. This class handles Source
 * intField in term queries (SOURCE:100) and numeric rangse (SOURCE:[100 TO 200]
 * 
 * @author jcharlet
 *
 */
public class TaxonomyQueryParser extends QueryParser {

    public TaxonomyQueryParser(String f, Analyzer a) {
	super(f, a);
    }

    protected Query newRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) {

	if (InformationAssetViewFields.SOURCE.toString().equals(field)) {
	    return NumericRangeQuery.newIntRange(field, Integer.parseInt(part1), Integer.parseInt(part2),
		    startInclusive, endInclusive);
	}
	return (TermRangeQuery) super.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }

    protected Query newTermQuery(Term term) {
	if (InformationAssetViewFields.SOURCE.toString().equals(term.field())) {

	    BytesRefBuilder byteRefBuilder = new BytesRefBuilder();
	    NumericUtils.intToPrefixCoded(Integer.parseInt(term.text()), 0, byteRefBuilder);
	    TermQuery tq = new TermQuery(new Term(term.field(), byteRefBuilder.get()));

	    return tq;
	}
	return super.newTermQuery(term);
    }
}
