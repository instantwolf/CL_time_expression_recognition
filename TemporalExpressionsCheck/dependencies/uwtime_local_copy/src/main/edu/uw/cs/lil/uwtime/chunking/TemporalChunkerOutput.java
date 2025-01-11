package edu.uw.cs.lil.uwtime.chunking;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.tiny.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IDerivation;
import edu.uw.cs.lil.uwtime.chunking.chunks.Chunk;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalEntity;
import edu.uw.cs.lil.uwtime.learn.binary.IBinaryClassifier;
import edu.uw.cs.lil.uwtime.learn.binary.IBinaryClassifierOutput;
import edu.uw.cs.lil.uwtime.learn.binary.IBinaryModel;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.utils.composites.Pair;

public class TemporalChunkerOutput extends
		AbstractChunkerOutput<TemporalJointChunk> {
	private static final long								serialVersionUID	= 8043987391186554781L;
	final private TemporalJointChunk						chunk;
	private final IBinaryClassifier<TemporalChunkerOutput>	classifier;
	final private List<IDerivation<LogicalExpression>>		derivations;
	private final IBinaryModel<TemporalChunkerOutput>		model;
	final private TemporalSentence							sentence;

	public TemporalChunkerOutput(TemporalSentence sentence,
			Pair<Integer, Integer> span, TemporalEntity entity,
			IBinaryClassifier<TemporalChunkerOutput> classifier,
			IBinaryModel<TemporalChunkerOutput> model) {
		this.sentence = sentence;

		this.chunk = new TemporalJointChunk(
				sentence,
				new Chunk<LogicalExpression>(span.first(), span.second(), null),
				null);
		this.chunk.setResult(new MentionResult(this.chunk, entity));
		this.derivations = new LinkedList<>();
		this.model = model;
		this.classifier = classifier;
	}

	public void addDerivation(IDerivation<LogicalExpression> derivation) {
		derivations.add(derivation);
	}

	@Override
	public TemporalJointChunk getChunk() {
		return chunk;
	}

	@Override
	public IBinaryClassifierOutput getClassifierOutput() {
		return classifier.classify(this, model);
	}

	public Set<LexicalEntry<LogicalExpression>> getCollapsedMaxLexicalEntries() {
		final Set<LexicalEntry<LogicalExpression>> entries = new HashSet<>();
		for (final IDerivation<LogicalExpression> p : derivations) {
			entries.addAll(p.getMaxLexicalEntries());
		}
		return entries;
	}

	@Override
	public TemporalChunkerOutput getSample() {
		return this;
	}

	public TemporalSentence getSentence() {
		return sentence;
	}

	@Override
	public String toString() {
		return "Output=" + chunk;
	}
}
