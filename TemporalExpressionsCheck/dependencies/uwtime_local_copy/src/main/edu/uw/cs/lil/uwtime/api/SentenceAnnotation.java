package edu.uw.cs.lil.uwtime.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.graph.IJointGraphOutput;
import edu.uw.cs.lil.uwtime.chunking.AbstractChunkerOutput;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.utils.composites.Pair;

public class SentenceAnnotation implements Iterable<MentionAnnotation>{
	private TemporalSentence sentence;
	private List<MentionAnnotation> mentionAnnotations;
	public SentenceAnnotation(Pair<TemporalSentence, List<Pair<AbstractChunkerOutput<TemporalJointChunk>, IJointGraphOutput<LogicalExpression, MentionResult>>>> sentenceOutput) {
		sentence = sentenceOutput.first();
		setMentionAnnotations(new LinkedList<MentionAnnotation>());
		for (Pair<AbstractChunkerOutput<TemporalJointChunk>, IJointGraphOutput<LogicalExpression, MentionResult>> mentionOutput : sentenceOutput.second())
			getMentionAnnotations().add(new MentionAnnotation(mentionOutput));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sentence: " + sentence);
		for (MentionAnnotation ma : getMentionAnnotations())
			sb.append(ma);
		return sb.toString();
	}

	public void setMentionAnnotations(List<MentionAnnotation> mentionAnnotations) {
		this.mentionAnnotations = mentionAnnotations;
	}

	public TemporalSentence getSentence() {
		return sentence;
	}
	
	@Override
	public Iterator<MentionAnnotation> iterator() {
		return mentionAnnotations.iterator();
	}
	
	public List<MentionAnnotation> getMentionAnnotations() {
		return mentionAnnotations;
	}
}
