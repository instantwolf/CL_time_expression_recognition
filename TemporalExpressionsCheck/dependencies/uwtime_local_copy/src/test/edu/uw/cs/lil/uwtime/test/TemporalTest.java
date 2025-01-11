package edu.uw.cs.lil.uwtime.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import edu.uw.cs.lil.uwtime.api.DocumentAnnotation;
import edu.uw.cs.lil.uwtime.api.TemporalAnnotator;
import edu.uw.cs.lil.uwtime.utils.TemporalDomain;

public class TemporalTest {
	TemporalAnnotator	annotator;

	public TemporalTest() throws ClassNotFoundException, IOException {
		annotator = new TemporalAnnotator();
	}

	@Test
	public void test() {
		testTIMEX3("A document will be written tomorrow", "1970-01-01",
				"1970-01-02", TemporalDomain.OTHER);
		testTIMEX3("The president is going to arrive on Monday.", "1970-01-01",
				"1970-01-05", TemporalDomain.OTHER);
		testTIMEX3("The president arrived on Monday.", "1970-01-01",
				"1969-12-29", TemporalDomain.OTHER);
	}

	private void testTIMEX3(String input, String dct, String output,
			TemporalDomain domain) {
		final DocumentAnnotation da = annotator.extract(input, dct, domain);
		assertEquals("Testing '" + input + "'", output, da
				.getSentenceAnnotations().get(0).getMentionAnnotations().get(0)
				.getValue());
	}
}
