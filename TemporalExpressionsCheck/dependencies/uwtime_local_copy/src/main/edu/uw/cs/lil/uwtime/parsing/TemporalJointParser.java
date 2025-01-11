package edu.uw.cs.lil.uwtime.parsing;

import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.situated.ISituatedDataItem;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYDerivation;
import edu.uw.cs.lil.tiny.parser.ccg.cky.CKYParserOutput;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.lil.tiny.parser.graph.IGraphDerivation;
import edu.uw.cs.lil.tiny.parser.graph.IGraphParserOutput;
import edu.uw.cs.lil.tiny.parser.joint.IEvaluation;
import edu.uw.cs.lil.tiny.parser.joint.graph.IJointGraphOutput;
import edu.uw.cs.lil.tiny.parser.joint.graph.IJointGraphParser;
import edu.uw.cs.lil.tiny.parser.joint.graph.JointGraphOutput;
import edu.uw.cs.lil.tiny.parser.joint.injective.graph.DeterministicEvalResultWrapper;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.uwtime.chunking.chunks.Chunk;
import edu.uw.cs.lil.uwtime.chunking.chunks.IChunk;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.eval.TemporalEvaluation;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalEntity;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalSequence;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.lil.uwtime.learn.temporal.TemporalExecutionHistory;
import edu.uw.cs.lil.uwtime.parsing.grammar.TemporalGrammar;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.filter.IFilter;

public class TemporalJointParser
		implements
		IJointGraphParser<ISituatedDataItem<Sentence, TemporalJointChunk>, LogicalExpression, MentionResult, MentionResult> {
	private final TemporalGrammar											grammar;
	private final LinkedList<Pair<TemporalSequence, TemporalJointChunk>>	temporalAnchors;

	public TemporalJointParser(TemporalGrammar grammar) {
		this.grammar = grammar;
		temporalAnchors = new LinkedList<>();
	}

	private static List<TemporalContext> getContexts(LogicalExpression l,
			Pair<TemporalSequence, TemporalJointChunk> nearestAnchor,
			TemporalSentence sentence) {
		final List<Pair<TemporalSequence, TemporalJointChunk>> references = new LinkedList<>();
		references.add(Pair.of((TemporalSequence) null,
				(TemporalJointChunk) null));
		references.add(Pair.of(
				new TemporalSequence(sentence.getReferenceTime()),
				(TemporalJointChunk) null));
		if (nearestAnchor != null) {
			references.add(nearestAnchor);
		}
		return TemporalContext.getPossibleContexts(l, references);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IDataItemModel<LogicalExpression> model) {
		return parse(dataItem, model, false);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, null);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return parse(dataItem, null, model, allowWordSkipping, tempLexicon,
				beamSize);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model) {
		return parse(dataItem, pruningFilter, model, false);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping) {
		return parse(dataItem, pruningFilter, model, allowWordSkipping, null);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, pruningFilter, model, allowWordSkipping,
				tempLexicon, -1);
	}

	@Override
	public IGraphParserOutput<LogicalExpression> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IFilter<LogicalExpression> pruningFilter,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return grammar.getParser().parse(dataItem.getSample(), model,
				allowWordSkipping);
	}

	@Override
	public IJointGraphOutput<LogicalExpression, MentionResult> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IJointDataItemModel<LogicalExpression, MentionResult> model) {
		return parse(dataItem, model, false);
	}

	@Override
	public IJointGraphOutput<LogicalExpression, MentionResult> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IJointDataItemModel<LogicalExpression, MentionResult> model,
			boolean allowWordSkipping) {
		return parse(dataItem, model, allowWordSkipping, null);
	}

	@Override
	public IJointGraphOutput<LogicalExpression, MentionResult> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IJointDataItemModel<LogicalExpression, MentionResult> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon) {
		return parse(dataItem, model, allowWordSkipping, tempLexicon, -1);
	}

	@Override
	public IJointGraphOutput<LogicalExpression, MentionResult> parse(
			ISituatedDataItem<Sentence, TemporalJointChunk> dataItem,
			IJointDataItemModel<LogicalExpression, MentionResult> model,
			boolean allowWordSkipping, ILexicon<LogicalExpression> tempLexicon,
			Integer beamSize) {

		final TemporalJointChunk chunk = dataItem.getState();
		final TemporalSentence sentence = chunk.getSentence();
		Pair<TemporalSequence, TemporalJointChunk> nearestAnchor = null;
		for (final Pair<TemporalSequence, TemporalJointChunk> anchor : temporalAnchors) {
			if (!sentence.getDocID().equals(
					anchor.second().getSentence().getDocID())) {
				temporalAnchors.clear();
				break;
			} else if (anchor.second().getSentence() != sentence
					|| !anchor.second().overlapsWith(chunk)) {
				nearestAnchor = anchor;
				break;
			}
		}

		final CKYParserOutput<LogicalExpression> baseParserOutput = grammar
				.getParser().parse(dataItem.getSample().getSample(), model,
						allowWordSkipping);

		final JointGraphOutput.Builder<LogicalExpression, MentionResult> jointOutputBuilder = new JointGraphOutput.Builder<LogicalExpression, MentionResult>(
				baseParserOutput, 0).setExactEvaluation(true);

		for (final CKYDerivation<LogicalExpression> baseParse : baseParserOutput
				.getAllParses()) {
			if (grammar.getFilter().isValid(baseParse.getSemantics())) {
				final IChunk<LogicalExpression> newBaseChunk = new Chunk<>(
						chunk.getBaseChunk().getStart(), chunk.getBaseChunk()
								.getEnd(), baseParse);
				for (final MentionResult result : resolveChunk(sentence,
						newBaseChunk, chunk, nearestAnchor)) {
					jointOutputBuilder
							.addInferencePair(Pair
									.of((IGraphDerivation<LogicalExpression>) baseParse,
											(IEvaluation<MentionResult>) new DeterministicEvalResultWrapper<>(
													model, result)));
				}
			}
		}
		final JointGraphOutput<LogicalExpression, MentionResult> jointOutput = jointOutputBuilder
				.build();
		if (!jointOutput.getMaxDerivations().isEmpty()) {
			final MentionResult bestResult = jointOutput.getMaxDerivations()
					.get(0).getResult();
			if (bestResult.getEntity() instanceof TemporalSequence) {
				final TemporalSequence sequence = (TemporalSequence) bestResult
						.getEntity();
				if (sequence.isRange()) {
					temporalAnchors.push(Pair.of(sequence, chunk));
				}
			}
		}
		return jointOutput;
	}

	public void resetTemporalAnchors() {
		temporalAnchors.clear();
	}

	private MentionResult resolveChunk(IChunk<LogicalExpression> baseChunk,
			TemporalJointChunk originalChunk, TemporalContext context) {
		final TemporalExecutionHistory executionHistory = new TemporalExecutionHistory();
		final Object evaluationOutput = TemporalEvaluation.of(baseChunk
				.getDerivation().getSemantics(), context, executionHistory,
				grammar.getConstants(), grammar.getCategoryServices());

		if (evaluationOutput == null
				|| !(evaluationOutput instanceof TemporalEntity)) {
			return null;
		}

		final TemporalEntity groundedEntity = (TemporalEntity) evaluationOutput;
		final String type = groundedEntity.getType();
		final String value = groundedEntity.getValue();
		final String mod = groundedEntity.getMod();

		if (value == null) {
			return null;
		}

		return new MentionResult(originalChunk, type, value, mod,
				baseChunk.getDerivation(), context, executionHistory,
				groundedEntity);
	}

	private List<MentionResult> resolveChunk(TemporalSentence sentence,
			IChunk<LogicalExpression> baseChunk,
			TemporalJointChunk originalChunk,
			Pair<TemporalSequence, TemporalJointChunk> nearestAnchor) {
		final List<MentionResult> results = new LinkedList<>();
		for (final TemporalContext context : getContexts(baseChunk
				.getDerivation().getSemantics(), nearestAnchor, sentence)) {
			final MentionResult result = resolveChunk(baseChunk, originalChunk,
					context);
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}
}
