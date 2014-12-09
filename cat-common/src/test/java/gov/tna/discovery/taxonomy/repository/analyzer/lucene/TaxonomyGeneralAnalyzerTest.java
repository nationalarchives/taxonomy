package gov.tna.discovery.taxonomy.repository.analyzer.lucene;

import static org.junit.Assert.*;
import gov.tna.discovery.taxonomy.config.AbstractTaxonomyTestCase;
import gov.tna.discovery.taxonomy.config.LuceneConfigurationTest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.expression.spel.ast.Indexer;

@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaxonomyGeneralAnalyzerTest extends AbstractTaxonomyTestCase {

    private static final String QUERY_WITHOUT_WILDCARD = "\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"";

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    private Analyzer queryAnalyser;

    @Autowired
    private Analyzer indexAnalyser;

    private static final String QUERY_WITH_LEADING_WILDCARD = "\"renewable energy\" OR \"renewable energies\" OR \"renewable electricity\" OR \"alternative energy\" OR \"alternative energies\" OR \"renewable fuel\" OR \"renewable fuels\" OR \"biogas\" OR \"biomass\" OR \"biofuel\" OR \"hydroelectric\" OR \"hydroelectricity\" OR \"hydropower\" OR (\"wind energy\"~5) OR \"wind farm\" OR \"wind farms\" OR \"wind power\" OR \"wind turbine\" OR \"wind turbines\" OR (\"solar power\"~5) OR (\"solar energy\"~5) OR \"solar panel\" OR \"solar panels\" OR \"landfill gas\" OR \"landfill gases\" OR \"geothermal\" OR \"photovoltaic\" OR \"tidal energy\" OR \"tidal energies\" OR \"tidal power\" OR \"wave farm\" OR \"wave farms\" OR (\"ocean energy\"~5) OR (\"kinetic energy\"~5) OR (\"kinetic energies\"~5) OR (*thermal AND energy) OR (*thermal AND energies) OR \"Renewables Advisory Board\" OR \"Renewable Energy Agency\" OR \"Geothermal Association\" OR \"Energy Saving Trust\" OR \"Non-Fossil Fuel Obligation\" OR \"Renewables Obligation\" OR \"Renewables Directive\" OR \"green energy\" OR \"green energies\" OR (\"energy conservation\"~2)";

    @Test
    public void testQueryAnalyserWithStopWords() throws IOException {
	StringReader reader = new StringReader("archives OR melody");

	TokenStream stream = queryAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "melody" }, null, null, null, null, null, null,
		null, null, true);
    }

    @Test
    public void testQueryAnalyserWithSynonyms() throws IOException {
	Reader reader = new StringReader("agonise");

	TokenStream stream = queryAnalyser.tokenStream("test", reader);

	assertTokenStreamContents(stream, new String[] { "agonise", "agonize" }, null, null, null, new int[] { 1, 0 },
		null, null, null, null, true);
    }

    @Test
    public void testQueryAnalyserWithCapitalLetters() throws IOException {
	StringReader reader = new StringReader("archiveS tEst MELODY");

	TokenStream stream = queryAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "test", "melody" }, null, null, null, null, null,
		null, null, null, true);
    }

    @Test
    public void testIndexAnalyserWithStopWords() throws IOException {
	StringReader reader = new StringReader("archives OR melody");

	TokenStream stream = indexAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "melody" }, null, null, null, null, null, null,
		null, null, true);
    }

    @Test(expected = AssertionError.class)
    public void testIndexAnalyserWithSynonyms() throws IOException {
	Reader reader = new StringReader("agonise");

	TokenStream stream = indexAnalyser.tokenStream("test", reader);

	assertTokenStreamContents(stream, new String[] { "agonise", "agonize" }, null, null, null, new int[] { 1, 0 },
		null, null, null, null, true);
    }

    @Test
    public void testIndexAnalyserWithCapitalLetters() throws IOException {
	StringReader reader = new StringReader("archiveS tEst MELODY");

	TokenStream stream = indexAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "test", "melody" }, null, null, null, null, null,
		null, null, null, true);
    }

    /**
     * Attribute that records if it was cleared or not. This is used for testing
     * that clearAttributes() was called correctly.
     */
    public static interface CheckClearAttributesAttribute extends Attribute {
	boolean getAndResetClearCalled();
    }

    /**
     * Attribute that records if it was cleared or not. This is used for testing
     * that clearAttributes() was called correctly.
     */
    public static final class CheckClearAttributesAttributeImpl extends AttributeImpl implements
	    CheckClearAttributesAttribute {
	private boolean clearCalled = false;

	@Override
	public boolean getAndResetClearCalled() {
	    try {
		return clearCalled;
	    } finally {
		clearCalled = false;
	    }
	}

	@Override
	public void clear() {
	    clearCalled = true;
	}

	@Override
	public boolean equals(Object other) {
	    return (other instanceof CheckClearAttributesAttributeImpl && ((CheckClearAttributesAttributeImpl) other).clearCalled == this.clearCalled);
	}

	@Override
	public int hashCode() {
	    return 76137213 ^ Boolean.valueOf(clearCalled).hashCode();
	}

	@Override
	public void copyTo(AttributeImpl target) {
	    ((CheckClearAttributesAttributeImpl) target).clear();
	}
    }

    public static void assertTokenStreamContents(TokenStream ts, String[] output, int startOffsets[], int endOffsets[],
	    String types[], int posIncrements[], int posLengths[], Integer finalOffset, Integer finalPosInc,
	    boolean[] keywordAtts, boolean offsetsAreCorrect) throws IOException {
	assertNotNull(output);
	CheckClearAttributesAttribute checkClearAtt = ts.addAttribute(CheckClearAttributesAttribute.class);

	CharTermAttribute termAtt = null;
	if (output.length > 0) {
	    assertTrue("has no CharTermAttribute", ts.hasAttribute(CharTermAttribute.class));
	    termAtt = ts.getAttribute(CharTermAttribute.class);
	}

	OffsetAttribute offsetAtt = null;
	if (startOffsets != null || endOffsets != null || finalOffset != null) {
	    assertTrue("has no OffsetAttribute", ts.hasAttribute(OffsetAttribute.class));
	    offsetAtt = ts.getAttribute(OffsetAttribute.class);
	}

	TypeAttribute typeAtt = null;
	if (types != null) {
	    assertTrue("has no TypeAttribute", ts.hasAttribute(TypeAttribute.class));
	    typeAtt = ts.getAttribute(TypeAttribute.class);
	}

	PositionIncrementAttribute posIncrAtt = null;
	if (posIncrements != null || finalPosInc != null) {
	    assertTrue("has no PositionIncrementAttribute", ts.hasAttribute(PositionIncrementAttribute.class));
	    posIncrAtt = ts.getAttribute(PositionIncrementAttribute.class);
	}

	PositionLengthAttribute posLengthAtt = null;
	if (posLengths != null) {
	    assertTrue("has no PositionLengthAttribute", ts.hasAttribute(PositionLengthAttribute.class));
	    posLengthAtt = ts.getAttribute(PositionLengthAttribute.class);
	}

	KeywordAttribute keywordAtt = null;
	if (keywordAtts != null) {
	    assertTrue("has no KeywordAttribute", ts.hasAttribute(KeywordAttribute.class));
	    keywordAtt = ts.getAttribute(KeywordAttribute.class);
	}

	// Maps position to the start/end offset:
	final Map<Integer, Integer> posToStartOffset = new HashMap<>();
	final Map<Integer, Integer> posToEndOffset = new HashMap<>();

	ts.reset();
	int pos = -1;
	int lastStartOffset = 0;
	for (int i = 0; i < output.length; i++) {
	    // extra safety to enforce, that the state is not preserved and also
	    // assign bogus values
	    ts.clearAttributes();
	    termAtt.setEmpty().append("bogusTerm");
	    if (offsetAtt != null)
		offsetAtt.setOffset(14584724, 24683243);
	    if (typeAtt != null)
		typeAtt.setType("bogusType");
	    if (posIncrAtt != null)
		posIncrAtt.setPositionIncrement(45987657);
	    if (posLengthAtt != null)
		posLengthAtt.setPositionLength(45987653);
	    if (keywordAtt != null)
		keywordAtt.setKeyword((i & 1) == 0);

	    checkClearAtt.getAndResetClearCalled(); // reset it, because we
						    // called clearAttribute()
						    // before
	    assertTrue("token " + i + " does not exist", ts.incrementToken());
	    assertTrue("clearAttributes() was not called correctly in TokenStream chain",
		    checkClearAtt.getAndResetClearCalled());

	    assertEquals("term " + i, output[i], termAtt.toString());
	    if (startOffsets != null) {
		assertEquals("startOffset " + i, startOffsets[i], offsetAtt.startOffset());
	    }
	    if (endOffsets != null) {
		assertEquals("endOffset " + i, endOffsets[i], offsetAtt.endOffset());
	    }
	    if (types != null) {
		assertEquals("type " + i, types[i], typeAtt.type());
	    }
	    if (posIncrements != null) {
		assertEquals("posIncrement " + i, posIncrements[i], posIncrAtt.getPositionIncrement());
	    }
	    if (posLengths != null) {
		assertEquals("posLength " + i, posLengths[i], posLengthAtt.getPositionLength());
	    }
	    if (keywordAtts != null) {
		assertEquals("keywordAtt " + i, keywordAtts[i], keywordAtt.isKeyword());
	    }

	    // we can enforce some basic things about a few attributes even if
	    // the caller doesn't check:
	    if (offsetAtt != null) {
		final int startOffset = offsetAtt.startOffset();
		final int endOffset = offsetAtt.endOffset();
		if (finalOffset != null) {
		    assertTrue("startOffset must be <= finalOffset", startOffset <= finalOffset.intValue());
		    assertTrue("endOffset must be <= finalOffset: got endOffset=" + endOffset + " vs finalOffset="
			    + finalOffset.intValue(), endOffset <= finalOffset.intValue());
		}

		if (offsetsAreCorrect) {
		    assertTrue("offsets must not go backwards startOffset=" + startOffset + " is < lastStartOffset="
			    + lastStartOffset, offsetAtt.startOffset() >= lastStartOffset);
		    lastStartOffset = offsetAtt.startOffset();
		}

		if (offsetsAreCorrect && posLengthAtt != null && posIncrAtt != null) {
		    // Validate offset consistency in the graph, ie
		    // all tokens leaving from a certain pos have the
		    // same startOffset, and all tokens arriving to a
		    // certain pos have the same endOffset:
		    final int posInc = posIncrAtt.getPositionIncrement();
		    pos += posInc;

		    final int posLength = posLengthAtt.getPositionLength();

		    if (!posToStartOffset.containsKey(pos)) {
			// First time we've seen a token leaving from this
			// position:
			posToStartOffset.put(pos, startOffset);
			// System.out.println("  + s " + pos + " -> " +
			// startOffset);
		    } else {
			// We've seen a token leaving from this position
			// before; verify the startOffset is the same:
			// System.out.println("  + vs " + pos + " -> " +
			// startOffset);
			assertEquals("pos=" + pos + " posLen=" + posLength + " token=" + termAtt,
				posToStartOffset.get(pos).intValue(), startOffset);
		    }

		    final int endPos = pos + posLength;

		    if (!posToEndOffset.containsKey(endPos)) {
			// First time we've seen a token arriving to this
			// position:
			posToEndOffset.put(endPos, endOffset);
			// System.out.println("  + e " + endPos + " -> " +
			// endOffset);
		    } else {
			// We've seen a token arriving to this position
			// before; verify the endOffset is the same:
			// System.out.println("  + ve " + endPos + " -> " +
			// endOffset);
			assertEquals("pos=" + pos + " posLen=" + posLength + " token=" + termAtt,
				posToEndOffset.get(endPos).intValue(), endOffset);
		    }
		}
	    }
	    if (posIncrAtt != null) {
		if (i == 0) {
		    assertTrue("first posIncrement must be >= 1", posIncrAtt.getPositionIncrement() >= 1);
		} else {
		    assertTrue("posIncrement must be >= 0", posIncrAtt.getPositionIncrement() >= 0);
		}
	    }
	    if (posLengthAtt != null) {
		assertTrue("posLength must be >= 1", posLengthAtt.getPositionLength() >= 1);
	    }
	}

	if (ts.incrementToken()) {
	    fail("TokenStream has more tokens than expected (expected count=" + output.length + "); extra token="
		    + termAtt.toString());
	}

	// repeat our extra safety checks for end()
	ts.clearAttributes();
	if (termAtt != null)
	    termAtt.setEmpty().append("bogusTerm");
	if (offsetAtt != null)
	    offsetAtt.setOffset(14584724, 24683243);
	if (typeAtt != null)
	    typeAtt.setType("bogusType");
	if (posIncrAtt != null)
	    posIncrAtt.setPositionIncrement(45987657);
	if (posLengthAtt != null)
	    posLengthAtt.setPositionLength(45987653);

	checkClearAtt.getAndResetClearCalled(); // reset it, because we called
						// clearAttribute() before

	ts.end();
	assertTrue("super.end()/clearAttributes() was not called correctly in end()",
		checkClearAtt.getAndResetClearCalled());

	if (finalOffset != null) {
	    assertEquals("finalOffset", finalOffset.intValue(), offsetAtt.endOffset());
	}
	if (offsetAtt != null) {
	    assertTrue("finalOffset must be >= 0", offsetAtt.endOffset() >= 0);
	}
	if (finalPosInc != null) {
	    assertEquals("finalPosInc", finalPosInc.intValue(), posIncrAtt.getPositionIncrement());
	}

	ts.close();
    }

}
