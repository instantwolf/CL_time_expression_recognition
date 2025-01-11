package edu.uw.cs.lil.uwtime.data;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDateTime;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.uwtime.chunking.ChunkSequence;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.utils.DependencyUtils;
import edu.uw.cs.lil.uwtime.utils.DependencyUtils.DependencyParseToken;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

public class TemporalSentence
		implements
		ILabeledDataItem<Pair<Sentence, TemporalSentence>, ChunkSequence<TemporalJointChunk, LogicalExpression>> {
	private static final long											serialVersionUID	= 2013931525176952047L;
	private List<DependencyParseToken>									dependencyParse;
	private final TemporalDocument										document;
	private final ChunkSequence<TemporalJointChunk, LogicalExpression>	goldChunkSequence;
	private LocalDateTime												referenceTime;
	private Pair<Sentence, TemporalSentence>							sample				= null;
	private final List<String>											tokens;

	public TemporalSentence(TemporalDocument document, String referenceTime) {
		this.document = document;
		this.tokens = new LinkedList<>();
		try {
			this.referenceTime = new LocalDateTime(referenceTime);
		} catch (final IllegalArgumentException e) {
			this.referenceTime = null;
		}
		goldChunkSequence = new ChunkSequence<>();
	}

	@Override
	public double calculateLoss(
			ChunkSequence<TemporalJointChunk, LogicalExpression> label) {
		throw new UnsupportedOperationException();
	}

	public List<DependencyParseToken> getDependencyParse() {
		return dependencyParse;
	}

	public String getDocID() {
		return document.getDocID();
	}

	public TemporalDocument getDocument() {
		return document;
	}

	@Override
	public ChunkSequence<TemporalJointChunk, LogicalExpression> getLabel() {
		return goldChunkSequence;
	}

	public int getNumTokens() {
		return tokens.size();
	}

	public LocalDateTime getReferenceTime() {
		return referenceTime;
	}

	@Override
	public Pair<Sentence, TemporalSentence> getSample() {
		if (sample == null) {
			final List<String> lowerCasedTokens = new LinkedList<>();
			for (final String s : tokens) {
				lowerCasedTokens.add(s.toLowerCase());
			}
			sample = Pair.of(new Sentence(lowerCasedTokens), this);
		}
		return sample;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void insertGoldChunk(TemporalJointChunk chunk) {
		goldChunkSequence.insertChunk(chunk);
	}

	public void insertToken(String token) {
		tokens.add(token);
	}

	@Override
	public boolean isCorrect(
			ChunkSequence<TemporalJointChunk, LogicalExpression> label) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean prune(ChunkSequence<TemporalJointChunk, LogicalExpression> y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double quality() {
		throw new UnsupportedOperationException();
	}

	public void saveDependencyParse(String dpString) {
		this.dependencyParse = DependencyUtils.parseDependencyParse(dpString);
	}

	@Override
	public String toString() {
		return toString(0, tokens.size());
	}

	public String toString(int start, int end) {
		return ListUtils.join(tokens.subList(start, end), " ");
	}
}
