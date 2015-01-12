package gov.tna.discovery.taxonomy.common.repository.lucene;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class DismaxQueryParser {

    public static String IMPOSSIBLE_FIELD_NAME = "\uFFFC\uFFFC\uFFFC";
    private DisjunctionQueryParser dqp;

    public DismaxQueryParser(org.apache.lucene.analysis.Analyzer analyzer) {
	dqp = new DisjunctionQueryParser(IMPOSSIBLE_FIELD_NAME, analyzer);
    }

    public Query parse(String query) throws ParseException {

	Query q0 = dqp.parse(DismaxQueryParser.IMPOSSIBLE_FIELD_NAME + ":(" + query + ")");
	Query phrase = dqp.parse(DismaxQueryParser.IMPOSSIBLE_FIELD_NAME + ":(\"" + query + "\")");
	if (phrase instanceof DisjunctionMaxQuery) {
	    BooleanQuery bq = new BooleanQuery(true);
	    bq.add(q0, BooleanClause.Occur.MUST);
	    bq.add(phrase, BooleanClause.Occur.SHOULD);
	    System.out.println(bq);
	    return bq;
	} else {
	    System.out.println(q0);
	    return q0;
	}

    }

    public void addAlias(String field, Alias alias) {
	dqp.addAlias(field, alias);
    }

    static class DisjunctionQueryParser extends QueryParser {

	public DisjunctionQueryParser(String defaultField, org.apache.lucene.analysis.Analyzer analyzer) {
	    super(Version.LUCENE_47, defaultField, analyzer);
	}

	protected Map<String, Alias> aliases = new HashMap<String, Alias>(3);

	// Field to Alias
	public void addAlias(String field, Alias alias) {
	    aliases.put(field, alias);
	}

	protected Query getFieldQuery(String field, String queryText, boolean quoted) {
	    // If field is an alias
	    if (aliases.containsKey(field)) {

		Alias a = aliases.get(field);
		DisjunctionMaxQuery q = new DisjunctionMaxQuery(a.getTie());
		boolean ok = false;

		for (String f : a.getFields().keySet()) {

		    // if query can be created for this field and text
		    Query sub = getFieldQuery(f, queryText, quoted);
		    if (sub != null) {

			// if query was quoted but doesnt generate a phrase
			// query we reject
			if (quoted == false || sub instanceof PhraseQuery) {
			    // If Field has a boost
			    if (a.getFields().get(f) != null) {
				sub.setBoost(a.getFields().get(f));
			    }
			    q.add(sub);
			    ok = true;
			}
		    }
		}
		// Something has been added to disjunction query
		return ok ? q : null;

	    } else {
		// usual Field
		try {
		    return super.getFieldQuery(field, queryText, quoted);
		} catch (Exception e) {
		    return null;
		}
	    }
	}
    }

    static class Alias {
	public Alias() {

	}

	private float tie;
	// Field Boosts
	private Map<String, Float> fields;

	public float getTie() {
	    return tie;
	}

	public void setTie(float tie) {
	    this.tie = tie;
	}

	public Map<String, Float> getFields() {
	    return fields;
	}

	public void setFields(Map<String, Float> fields) {
	    this.fields = fields;
	}
    }
}
