package gov.tna.discovery.taxonomy.common.repository.lucene.analyzer;

import static org.junit.Assert.*;
import gov.tna.discovery.taxonomy.common.config.LuceneConfigurationTest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests dedicated to the analysers<br/>
 * the helper methods are taken from test classes from lucene
 * 
 * @author jcharlet
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaxonomyGeneralAnalyzerTest {

    // private static final Logger logger =
    // LoggerFactory.getLogger(Indexer.class);

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private StopFilterFactory stopFilterFactory;

    @Autowired
    private SynonymFilterFactory synonymFilterFactory;

    Analyzer trainingSetAnalyser;

    @After
    public void closeAnalyser() {
	trainingSetAnalyser.close();
    }

    @Test
    public void testQueryAnalyserWithStopWords() throws IOException {
	trainingSetAnalyser = new TaxonomyTrainingSetAnalyser(Version.valueOf(luceneVersion), stopFilterFactory, null,
		null);
	StringReader reader = new StringReader("archives OR melody");

	TokenStream stream = trainingSetAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "melody" }, null, null, null, null, null, null,
		null, null, true);
    }

    @Test
    public void testQueryAnalyserWithSynonyms() throws IOException {
	trainingSetAnalyser = new TaxonomyTrainingSetAnalyser(Version.valueOf(luceneVersion), null,
		synonymFilterFactory, null);
	Reader reader = new StringReader("agonise");

	TokenStream stream = trainingSetAnalyser.tokenStream("test", reader);

	assertTokenStreamContents(stream, new String[] { "agonise", "agonize" }, null, null, null, new int[] { 1, 0 },
		null, null, null, null, true);
    }

    @Test
    public void testQueryAnalyserWithCapitalLetters() throws IOException {
	trainingSetAnalyser = new TaxonomyTrainingSetAnalyser(Version.valueOf(luceneVersion), null, null, null);
	StringReader reader = new StringReader("archiveS tEst MELODY");

	TokenStream stream = trainingSetAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "test", "melody" }, null, null, null, null, null,
		null, null, null, true);
    }

    @Test
    public void testQueryAnalyserWithShingleFilter() throws IOException {
	trainingSetAnalyser = new TaxonomyTrainingSetAnalyser(Version.valueOf(luceneVersion), null, null, 2);
	StringReader reader = new StringReader("archiveS tEst");

	TokenStream stream = trainingSetAnalyser.tokenStream("test", reader);

	assertNotNull(stream);
	assertTokenStreamContents(stream, new String[] { "archives", "archives test", "test" }, null, null, null, null,
		null, null, null, null, true);
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
