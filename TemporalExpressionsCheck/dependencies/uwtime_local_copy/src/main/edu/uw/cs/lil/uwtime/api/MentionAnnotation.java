package edu.uw.cs.lil.uwtime.api;

import java.util.Collections;

import org.json.simple.JSONArray;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.graph.IJointGraphOutput;
import edu.uw.cs.lil.uwtime.chunking.AbstractChunkerOutput;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.lil.uwtime.utils.LogicalExpressionToIndentedLatexString;
import edu.uw.cs.lil.uwtime.utils.TemporalLogicalExpressionReplacement;
import edu.uw.cs.utils.composites.Pair;

public class MentionAnnotation {
	private final TemporalJointChunk	mention;

	public MentionAnnotation(
			Pair<AbstractChunkerOutput<TemporalJointChunk>, IJointGraphOutput<LogicalExpression, MentionResult>> mentionOutput) {
		mention = mentionOutput.first().getChunk();
	}

	public TemporalJointChunk getMention() {
		return mention;
	}

	public String getMod() {
		return mention.getResult().getMod();
	}

	public LogicalExpression getSemantics() {
		return TemporalLogicalExpressionReplacement.of(mention.getResult()
				.getSemantics(), mention.getResult().getContext());
	}

	public String getType() {
		return mention.getResult().getType();
	}

	public String getValue() {
		return mention.getResult().getValue();
	}

	public JSONArray toDetectionConfidenceJSON(int nid) {
		return toNormalization(nid, "Detection confidence", String.format(
				"%.2f%%", mention.getResult().getDetectionProbability() * 100));
	}

	@SuppressWarnings("unchecked")
	public JSONArray toModJSON(int aid) {
		if (getMod() == null) {
			return null;
		}
		final JSONArray output = new JSONArray();
		output.add("A" + aid);
		output.add("MOD");
		output.add("T" + mention.getTID());
		output.add(getMod());
		return output;
	}

	@SuppressWarnings("unchecked")
	public JSONArray toRefJSON(int rid) {
		if (mention.getResult().getContext().getReferenceSource() == null) {
			return null;
		}

		final JSONArray reference = new JSONArray();
		reference.add("reference");
		reference.add("T"
				+ mention.getResult().getContext().getReferenceSource()
						.getTID());

		final JSONArray target = new JSONArray();
		target.add("target");
		target.add("T" + mention.getTID());

		final JSONArray args = new JSONArray();
		args.add(reference);
		args.add(target);

		final JSONArray output = new JSONArray();
		output.add("R" + rid);
		output.add("REF");
		output.add(args);
		return output;
	}

	public JSONArray toResolutionConfidenceJSON(int nid) {
		return toNormalization(nid, "Resolution confidence", String.format(
				"%.2f%%", mention.getResult().getResolutionProbability() * 100));
	}

	public JSONArray toSemJSON(int nid) {
		return toNormalization(
				nid,
				"Semantics",
				"$$"
						+ LogicalExpressionToIndentedLatexString
								.of(getSemantics(),
										Collections
												.<LogicalExpression, String> emptyMap())
								.replace("\\_", "_") + "$$");
	}

	@Override
	public String toString() {
		return "\n\t"
				+ TemporalLogicalExpressionReplacement.of(mention.getResult()
						.getSemantics(), mention.getResult().getContext());
	}

	@SuppressWarnings("unchecked")
	public JSONArray toTypeJSON(int tid) {
		mention.setTID(tid);
		final JSONArray output = new JSONArray();
		output.add("T" + mention.getTID());
		output.add(getType());
		final JSONArray allSpans = new JSONArray();
		final JSONArray span = new JSONArray();
		span.add(mention.getCharStart());
		span.add(mention.getCharEnd());
		allSpans.add(span);
		output.add(allSpans);
		return output;
	}

	@SuppressWarnings("unchecked")
	public JSONArray toValueJSON(int aid) {
		final JSONArray output = new JSONArray();
		output.add("A" + aid);
		output.add("VAL");
		output.add("T" + mention.getTID());
		output.add(getValue());
		return output;
	}

	@SuppressWarnings("unchecked")
	private JSONArray toNormalization(int nid, String key, String value) {
		final JSONArray output = new JSONArray();
		output.add("N" + nid);
		output.add("dummy");
		output.add("T" + mention.getTID());
		output.add(key);
		output.add("dummy");
		output.add(value);
		return output;
	}

}
