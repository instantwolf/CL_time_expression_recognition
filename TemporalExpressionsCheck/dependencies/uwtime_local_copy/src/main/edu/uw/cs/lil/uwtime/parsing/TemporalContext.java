package edu.uw.cs.lil.uwtime.parsing;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalSequence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalContext implements Serializable {
	private static final long			serialVersionUID	= -6490191095086318792L;

	final private TemporalDirection		direction;
	final private ShiftGranularity		granularity;
	final private TemporalSequence		reference;
	final private TemporalJointChunk	referenceSource;

	public TemporalContext() {
		this(TemporalDirection.NONE, null, null, ShiftGranularity.NONE);
	}

	public TemporalContext(TemporalDirection direction,
			TemporalSequence reference, TemporalJointChunk referenceSource,
			ShiftGranularity granularity) {
		this.direction = direction;
		this.reference = reference;
		this.referenceSource = referenceSource;
		this.granularity = granularity;
	}

	public static List<TemporalContext> getPossibleContexts(
			LogicalExpression l,
			List<Pair<TemporalSequence, TemporalJointChunk>> references) {
		final List<TemporalContext> contexts = new LinkedList<>();
		final boolean hasShift = l.toString().contains("shift:<");
		final boolean isSequence = l.getType().isExtending(
				TemporalSequence.LOGICAL_TYPE);
		final boolean usesReference = l.toString().contains("ref_time:r");

		for (final Pair<TemporalSequence, TemporalJointChunk> r : references) {
			if (!(usesReference && r.first() == null)) {
				addContexts(r, contexts, hasShift, isSequence);
			}
		}
		return contexts;
	}

	private static void addContexts(
			Pair<TemporalSequence, TemporalJointChunk> r,
			List<TemporalContext> contexts, boolean hasShift, boolean isSequence) {
		if (hasShift) {
			addContexts(r, contexts, isSequence, ShiftGranularity.ANCHOR);
			addContexts(r, contexts, isSequence, ShiftGranularity.DELTA);
		} else {
			addContexts(r, contexts, isSequence, ShiftGranularity.NONE);
		}
	}

	private static void addContexts(
			Pair<TemporalSequence, TemporalJointChunk> r,
			List<TemporalContext> contexts, boolean isSequence,
			ShiftGranularity sg) {
		contexts.add(new TemporalContext(TemporalDirection.NONE, r.first(), r
				.second(), sg));
		if (isSequence && r.first() != null) {
			contexts.add(new TemporalContext(TemporalDirection.PRECEDING, r
					.first(), r.second(), sg));
			contexts.add(new TemporalContext(TemporalDirection.FOLLOWING, r
					.first(), r.second(), sg));
		}
	}

	public LogicalExpression applyTemporalDirection(LogicalExpression l,
			LogicalExpressionCategoryServices categoryServices) {
		String semantics = null;
		switch (direction) {
			case PRECEDING:
				semantics = "(lambda $0:s (preceding:<s,<r,r>> $0 ref_time:r))";
				break;
			case FOLLOWING:
				semantics = "(lambda $0:s (following:<s,<r,r>> $0 ref_time:r))";
				break;
			case NONE:
				return l;
		}
		return categoryServices.apply(
				categoryServices.parseSemantics(semantics), l);
	}

	public TemporalDirection getDirection() {
		return direction;
	}

	public ShiftGranularity getGranularity() {
		return granularity;
	}

	public TemporalSequence getReference() {
		return reference;
	}

	public TemporalJointChunk getReferenceSource() {
		return referenceSource;
	}

	public String getReferenceType() {
		if (referenceSource != null) {
			return "LAST_RANGE";
		} else if (reference != null) {
			return "DCT";
		} else {
			return "NONE";
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (reference != null) {
			sb.append("Reference: "
					+ reference.getValue()
					+ "("
					+ (referenceSource == null ? "DCT" : referenceSource
							.getOriginalText()) + ") ");
		}
		if (direction != TemporalDirection.NONE) {
			sb.append("Direction: " + direction + " ");
		}
		if (granularity != ShiftGranularity.NONE) {
			sb.append("Shift granularity: " + granularity + " ");
		}
		return sb.toString();
	}

	public static enum ShiftGranularity {
		ANCHOR, DELTA, NONE
	}

	public static enum TemporalDirection {
		FOLLOWING, NONE, PRECEDING
	}
}
