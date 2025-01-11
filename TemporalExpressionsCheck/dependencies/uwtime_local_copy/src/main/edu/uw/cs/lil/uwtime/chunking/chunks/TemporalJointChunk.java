package edu.uw.cs.lil.uwtime.chunking.chunks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.situated.ISituatedDataItem;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.IDerivation;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.lil.uwtime.utils.FormattingUtils;
import edu.uw.cs.lil.uwtime.utils.TemporalLog;
import edu.uw.cs.utils.composites.Pair;

public class TemporalJointChunk extends
		AbstractJointChunk<LogicalExpression, MentionResult>
		implements
		ILabeledDataItem<ISituatedDataItem<Sentence, TemporalJointChunk>, String> {
	private static String									INDENTATION			= "\t";
	private static final long								serialVersionUID	= -5859852309847402300L;

	private IChunk<LogicalExpression>						baseChunk;
	private int												charEnd;
	// Temporary variables used only during preprocessing
	private int												charStart;										// character
																											// offset
	private Sentence										phrase;
	private MentionResult									result;
	private ISituatedDataItem<Sentence, TemporalJointChunk>	sample;

	private TemporalSentence								sentence;
	private int												tid;

	// For gold chunks
	public TemporalJointChunk(String type, String value, String mod,
			int charStart, int charEnd, int tid) {
		this.result = new MentionResult(this, type, value, mod);
		this.charStart = charStart;
		this.charEnd = charEnd;
		this.tid = tid;
	}

	// For predicted chunks
	public TemporalJointChunk(TemporalSentence sentence,
			IChunk<LogicalExpression> baseChunk, MentionResult result) {
		this.sentence = sentence;
		this.result = result;
		this.baseChunk = baseChunk;
		this.tid = -1; // no tid assigned to predicted chunks
	}

	public void alignTokens(
			Map<Integer, Pair<TemporalSentence, Integer>> startCharToToken,
			Map<Integer, Pair<TemporalSentence, Integer>> endCharToToken) {
		if (charStart != -1) {
			if (alignHelper(startCharToToken, endCharToToken, charStart,
					charEnd)) {
				return;
			} else if (alignHelper(startCharToToken, endCharToToken, charStart,
					charEnd + 1)) {
				return;
			} else if (alignHelper(startCharToToken, endCharToToken,
					charStart + 1, charEnd)) {
				return;
			} else if (alignHelper(startCharToToken, endCharToToken, charStart,
					charEnd - 1)) {
				return;
			} else {
				TemporalLog.printf("error",
						"Unable to find offset for mention [#%d -> #%d](%s)\n",
						charStart, charEnd, result.getValue());
			}
		}
	}

	@Override
	public double calculateLoss(String label) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IChunk<LogicalExpression> getBaseChunk() {
		return baseChunk;
	}

	public int getCharEnd() {
		return sentence.getDocument().getCharRange(sentence, getEnd()).second();
	}

	public int getCharStart() {
		return sentence.getDocument().getCharRange(sentence, getStart())
				.first();
	}

	@Override
	public IDerivation<LogicalExpression> getDerivation() {
		return baseChunk.getDerivation();
	}

	@Override
	public int getEnd() {
		return baseChunk.getEnd();
	}

	@Override
	public String getLabel() {
		throw new UnsupportedOperationException();
	}

	public String getOriginalText() {
		if (this == sentence.getDocument().getDocumentCreationTime()) {
			return sentence.getDocument().getDocumentCreationText();
		} else {
			return sentence.getDocument().getOriginalText(getCharStart(),
					getCharEnd());
		}
	}

	public Sentence getPhrase() {
		if (phrase == null) {
			final List<String> tokenList = new LinkedList<>();
			if (getEnd() < getStart()) {
				TemporalLog
						.printf("error",
								"Multi-sentential phrase (%d->%d). Using end of sentence (%s)\n",
								getStart(), getEnd(), sentence.toString());
				setTokenRange(getStart(), sentence.getNumTokens() - 1);
			}
			for (final String s : sentence.getTokens().subList(getStart(),
					getEnd() + 1)) {
				tokenList.add(s.toLowerCase());
			}
			phrase = new Sentence(tokenList);
		}
		return phrase;
	}

	@Override
	public MentionResult getResult() {
		return result;
	}

	@Override
	public ISituatedDataItem<Sentence, TemporalJointChunk> getSample() {
		if (sample == null) {
			sample = new ISituatedDataItem<Sentence, TemporalJointChunk>() {
				private static final long	serialVersionUID	= 1L;

				@Override
				public Sentence getSample() {
					return getPhrase();
				}

				@Override
				public TemporalJointChunk getState() {
					return TemporalJointChunk.this;
				}
			};
		}
		return sample;
	}

	public TemporalSentence getSentence() {
		return sentence;
	}

	@Override
	public int getStart() {
		return baseChunk.getStart();
	}

	public int getTID() {
		return tid;
	}

	@Override
	public boolean isCorrect(String label) {
		throw new UnsupportedOperationException();
	}

	public boolean isRelaxedMatch() {
		for (final TemporalJointChunk chunk : sentence.getLabel()) {
			if (this.overlapsWith(chunk)) {
				return true;
			}
		}
		return false;
	}

	public boolean isStrictMatch() {
		for (final TemporalJointChunk chunk : sentence.getLabel()) {
			if (this.strictlyMatches(chunk)) {
				return true;
			}
		}
		return false;
	}

	public boolean isStrictSubmentionMatch() {
		for (final TemporalJointChunk chunk : sentence.getLabel()) {
			if (chunk.contains(this) && !chunk.strictlyMatches(this)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSubmentionMatch() {
		for (final TemporalJointChunk chunk : sentence.getLabel()) {
			if (chunk.contains(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean prune(String y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double quality() {
		throw new UnsupportedOperationException();
	}

	public void setCharEnd(String text) {
		charEnd = charStart + text.length();
	}

	public void setResult(MentionResult result) {
		this.result = result;
	}

	public void setTID(int tid) {
		this.tid = tid;
	}

	public int spanSize() {
		return getEnd() - getStart();
	}

	@Override
	public String toString() {
		String s = "\n";
		s += FormattingUtils.formatContents(INDENTATION, "Phrase",
				getOriginalText());
		if (tid != -1) {
			s += FormattingUtils.formatContents(INDENTATION, "TID", "t" + tid);
		}
		if (result != null) {
			if (getDerivation() != null) {
				s += FormattingUtils.formatContents(INDENTATION,
						"Base derivation", getDerivation());
			}
			s += FormattingUtils.formatContents(INDENTATION, "Result", result);
		}
		return s;
	}

	private boolean alignHelper(
			Map<Integer, Pair<TemporalSentence, Integer>> startCharToToken,
			Map<Integer, Pair<TemporalSentence, Integer>> endCharToToken,
			int tempCharStart, int tempCharEnd) {
		// Wrapper around core alignment code to accommodate mistakes in
		// annotation
		// where "10 p.m", rather than "10 p.m." is labeled as the mention
		// Performs two-way linking between mention and sentence. Return whether
		// it was successful or not
		if (startCharToToken.containsKey(tempCharStart)
				&& endCharToToken.containsKey(tempCharEnd)) {
			final Pair<TemporalSentence, Integer> startIndexes = startCharToToken
					.get(tempCharStart);
			final Pair<TemporalSentence, Integer> endIndexes = endCharToToken
					.get(tempCharEnd);
			this.setTokenRange(startIndexes.second(), endIndexes.second());
			// Assume start and end occur in the same sentence
			final TemporalSentence alignedSentence = startIndexes.first();
			sentence = alignedSentence;
			alignedSentence.insertGoldChunk(this);
			return true;
		} else {
			return false;
		}
	}

	private void setTokenRange(int start, int end) {
		baseChunk = new Chunk<>(start, end, null);
	}
}