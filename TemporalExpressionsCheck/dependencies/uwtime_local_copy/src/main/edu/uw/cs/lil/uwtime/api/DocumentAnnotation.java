package edu.uw.cs.lil.uwtime.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.graph.IJointGraphOutput;
import edu.uw.cs.lil.uwtime.chunking.AbstractChunkerOutput;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalDocument;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.eval.TemporalModifier;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.utils.composites.Pair;

public class DocumentAnnotation implements Iterable<SentenceAnnotation> {
	private final TemporalDocument			document;
	private final List<SentenceAnnotation>	sentenceAnnotations;

	public DocumentAnnotation(
			TemporalDocument document,
			List<Pair<TemporalSentence, List<Pair<AbstractChunkerOutput<TemporalJointChunk>, IJointGraphOutput<LogicalExpression, MentionResult>>>>> documentOutput) {
		this.document = document;
		this.sentenceAnnotations = new LinkedList<>();
		for (final Pair<TemporalSentence, List<Pair<AbstractChunkerOutput<TemporalJointChunk>, IJointGraphOutput<LogicalExpression, MentionResult>>>> sentenceOutput : documentOutput) {
			sentenceAnnotations.add(new SentenceAnnotation(sentenceOutput));
		}
	}

	public TemporalDocument getDocument() {
		return document;
	}

	public List<SentenceAnnotation> getSentenceAnnotations() {
		return sentenceAnnotations;
	}

	public String getTimeML() {
		final List<Pair<TemporalSentence, TemporalJointChunk>> allPredictions = new LinkedList<>();
		for (final SentenceAnnotation sa : sentenceAnnotations) {
			for (final MentionAnnotation ma : sa.getMentionAnnotations()) {
				allPredictions.add(Pair.of(sa.getSentence(), ma.getMention()));
			}
		}
		return document.annotatePredictions(allPredictions);
	}

	@Override
	public Iterator<SentenceAnnotation> iterator() {
		return sentenceAnnotations.iterator();
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		final JSONObject output = new JSONObject();
		output.put("config", getConfigJSON());
		output.put("brat", getBratJSON());
		output.put("timeml", getTimeML());
		return output;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final SentenceAnnotation sa : sentenceAnnotations) {
			sb.append(sa + "\n");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private JSONObject getBratJSON() {
		final JSONObject output = new JSONObject();
		output.put("text", document.getOriginalText());

		final JSONArray entities = new JSONArray();
		final JSONArray attributes = new JSONArray();
		final JSONArray relations = new JSONArray();
		final JSONArray normalizations = new JSONArray();
		int tidCount = 0;
		int aidCount = 0;
		int ridCount = 0;
		int nidCount = 0;
		for (final SentenceAnnotation sa : sentenceAnnotations) {
			for (final MentionAnnotation ma : sa.getMentionAnnotations()) {
				entities.add(ma.toTypeJSON(tidCount));
				tidCount++;

				attributes.add(ma.toValueJSON(aidCount));
				aidCount++;

				normalizations.add(ma.toSemJSON(nidCount));
				nidCount++;

				normalizations.add(ma.toDetectionConfidenceJSON(nidCount));
				nidCount++;

				normalizations.add(ma.toResolutionConfidenceJSON(nidCount));
				nidCount++;

				JSONArray mentionOutput;
				mentionOutput = ma.toModJSON(aidCount);
				if (mentionOutput != null) {
					attributes.add(mentionOutput);
					aidCount++;
				}
				mentionOutput = ma.toRefJSON(ridCount);
				if (mentionOutput != null) {
					relations.add(mentionOutput);
					ridCount++;
				}
			}
		}

		output.put("entities", entities);
		output.put("attributes", attributes);
		output.put("relations", relations);
		output.put("normalizations", normalizations);
		return output;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getConfigJSON() {
		final String entityColor = "cyan";
		final String borderColor = "darken";

		final JSONObject dateType = new JSONObject();
		dateType.put("type", "DATE");
		dateType.put("bgColor", entityColor);
		dateType.put("borderColor", borderColor);

		final JSONObject durationType = new JSONObject();
		durationType.put("type", "DURATION");
		durationType.put("bgColor", entityColor);
		durationType.put("borderColor", borderColor);

		final JSONObject timeType = new JSONObject();
		timeType.put("type", "TIME");
		timeType.put("bgColor", entityColor);
		timeType.put("borderColor", borderColor);

		final JSONObject setType = new JSONObject();
		setType.put("type", "SET");
		setType.put("bgColor", entityColor);
		setType.put("borderColor", borderColor);

		final JSONArray entityTypes = new JSONArray();
		entityTypes.add(dateType);
		entityTypes.add(durationType);
		entityTypes.add(timeType);
		entityTypes.add(setType);

		final JSONObject valType = new JSONObject();
		valType.put("type", "VAL");
		final JSONObject valValues = new JSONObject();
		for (final SentenceAnnotation sa : sentenceAnnotations) {
			for (final MentionAnnotation ma : sa.getMentionAnnotations()) {
				final JSONObject valProperties = new JSONObject();
				valProperties.put("glyph", "[" + ma.getValue() + "]");
				valValues.put(ma.getValue(), valProperties);
			}
		}
		valType.put("values", valValues);

		final JSONObject modType = new JSONObject();
		modType.put("type", "MOD");
		final JSONObject modValues = new JSONObject();
		for (final TemporalModifier m : TemporalModifier.values()) {
			final JSONObject modProperties = new JSONObject();
			modProperties.put("glyph", "(" + m.toString() + ")");
			modValues.put(m.toString(), modProperties);
		}
		modType.put("values", modValues);

		final JSONArray entityAttributeTypes = new JSONArray();
		entityAttributeTypes.add(modType);
		entityAttributeTypes.add(valType);

		final JSONArray targetTargets = new JSONArray();
		targetTargets.add("DATE|DURATION|TIME|SET");
		final JSONObject targetArg = new JSONObject();
		targetArg.put("role", "target");
		targetArg.put("targets", targetTargets);

		final JSONArray referenceTargets = new JSONArray();
		referenceTargets.add("DATE|DURATION|TIME|SET");
		final JSONObject referenceArg = new JSONObject();
		referenceArg.put("role", "reference");
		referenceArg.put("targets", referenceTargets);

		final JSONArray referenceTypeArgs = new JSONArray();
		referenceTypeArgs.add(targetArg);
		referenceTypeArgs.add(referenceArg);

		final JSONObject referenceType = new JSONObject();
		referenceType.put("type", "REF");
		referenceType.put("dashArray", "3,3");
		referenceType.put("color", "purple");
		referenceType.put("args", referenceTypeArgs);
		final JSONArray relationTypes = new JSONArray();
		relationTypes.add(referenceType);

		final JSONObject output = new JSONObject();
		output.put("entity_types", entityTypes);
		output.put("entity_attribute_types", entityAttributeTypes);
		output.put("relation_types", relationTypes);
		return output;
	}
}
