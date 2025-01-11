package edu.uw.cs.lil.uwtime.eval.entities;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.joda.time.LocalDateTime;

import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.uwtime.eval.TemporalModifier;
import edu.uw.cs.lil.uwtime.eval.TemporalQuantifier;
import edu.uw.cs.lil.uwtime.eval.entities.durations.TemporalDay;
import edu.uw.cs.lil.uwtime.eval.entities.durations.TemporalDuration;
import edu.uw.cs.lil.uwtime.eval.entities.durations.TemporalMonth;
import edu.uw.cs.lil.uwtime.eval.entities.durations.TemporalYear;

public class TemporalSequence extends TemporalEntity implements
		Iterable<TemporalSequence> {
	public static final Type			LOGICAL_TYPE	= LogicLanguageServices
																.getTypeRepository()
																.getType("s");

	final private TemporalDuration		head;
	final private TemporalModifier		modifier;
	final private TemporalQuantifier	quantifier;

	public TemporalSequence() {
		this((TemporalDuration) null);
	}

	public TemporalSequence(LocalDateTime joda) {
		this(new TemporalYear(joda.getYear(), new TemporalMonth(
				joda.getMonthOfYear(), new TemporalDay(joda.getDayOfMonth()))));
	}

	public TemporalSequence(TemporalDuration head) {
		this(head, TemporalQuantifier.ONE, TemporalModifier.NONE);
	}

	public TemporalSequence(TemporalSequence other) {
		this(other, other.quantifier, other.modifier);
	}

	public TemporalSequence(TemporalSequence other, TemporalModifier modifier) {
		this(other, other.quantifier, modifier);
	}

	public TemporalSequence(TemporalSequence other,
			TemporalQuantifier quantifier) {
		this(other, quantifier, other.modifier);
	}

	public TemporalSequence(TemporalSequence other,
			TemporalQuantifier quantifier, TemporalModifier modifier) {
		this(other.head.clone(), quantifier, modifier);
	}

	private TemporalSequence(TemporalDuration head,
			TemporalQuantifier quantifier, TemporalModifier modifier) {
		this.head = head;
		this.quantifier = quantifier;
		this.modifier = modifier;
	}

	public TemporalSequence fixMostGranular(int n) {
		final TemporalSequence fixed = new TemporalSequence(this);
		final TemporalDuration deepest = fixed.head.getDeepestFreeVariable();

		if (deepest == null) {
			return null;
		} else {
			deepest.setN(n);
			return fixed;
		}
	}

	public TemporalDuration getDeepestNode() {
		return head.getDeepestNode();
	}

	public LocalDateTime getEndJodaTime() {
		final LocalDateTime start = getStartJodaTime();
		return start == null ? null : start.plus(
				getDeepestNode().getJodaUnitPeriod()).minusMillis(1); // Subtract
																		// epsilon
																		// time
																		// for
																		// correct
																		// comparison
	}

	public TemporalSequence getFixedInstance(TemporalSequence referenceTime,
			int offset) {
		referenceTime = referenceTime.normalize();
		if (!referenceTime.isRange()
				|| referenceTime.getStartJodaTime() == null) {
			return null;
		}
		if (isRange()) {
			if (offset == 0) {
				return new TemporalSequence(this);
			} else {
				return null;
			}
		}
		final TemporalIterator iterator = new TemporalIterator(this,
				referenceTime);
		TemporalSequence fixed, containingUnit;
		do {
			fixed = iterator.next();
			containingUnit = iterator.containingUnitOfResult();
			if (containingUnit.startsAfter(referenceTime)) {
				return null;
			}
		} while (!containingUnit.contains(referenceTime) && iterator.hasNext());

		if (fixed != null && offset != 0) {
			if (offset > 0) {
				for (int i = 0; i < offset && iterator.hasNext(); i++) {
					fixed = iterator.next();
				}
			} else {
				for (int i = offset; i < 0 && iterator.hasPrevious(); i++) {
					fixed = iterator.previous();
				}
			}
		}
		return fixed;
	}

	public TemporalDuration getHead() {
		return head;
	}

	@Override
	public String getMod() {
		return modifier.toString();
	}

	public TemporalSequence getNearestInstance(TemporalSequence referenceTime,
			boolean backwards) {
		if (isRange()) {
			return null;
		}

		final TemporalIterator iterator = new TemporalIterator(this,
				referenceTime);
		TemporalSequence fixed;
		do {
			fixed = iterator.next();
		} while (fixed.endsBefore(referenceTime) && iterator.hasNext());

		if (backwards) {
			fixed = iterator.previous();
		}

		return fixed;
	}

	public TemporalDuration getNodeWithGranularity(TemporalDuration duration) {
		return head.getNodeWithGranularity(duration);
	}

	public LocalDateTime getStartJodaTime() {
		return head.getStartJodaTime(new LocalDateTime(0, 1, 1, 0, 0));
	}

	public TemporalSequence getStructuralCopy() {
		final TemporalSequence unfixed = new TemporalSequence(this);
		for (TemporalDuration current = unfixed.head; current != null; current = current
				.getChild()) {
			if (current.isFixed()) {
				current.setN(1);
			}
		}
		return unfixed;
	}

	@Override
	public String getStructure() {
		return getStructuralCopy().toString();
	}

	@Override
	public String getType() {
		if (quantifier == TemporalQuantifier.EVERY) {
			return "SET";
		} else if (this.getDeepestNode().getGranularity() > new TemporalDay(0)
				.getGranularity()) {
			return "TIME";
		} else {
			return "DATE";
		}
	}

	@Override
	public String getValue() {
		if (modifier == TemporalModifier.FUTURE) {
			return "FUTURE_REF";
		}
		if (modifier == TemporalModifier.PRESENT) {
			return "PRESENT_REF";
		}
		if (modifier == TemporalModifier.PAST) {
			return "PAST_REF";
		}
		if (quantifier == TemporalQuantifier.EVERY) {
			final TemporalDuration deepestNodeClone = getDeepestNode().clone();
			if (!deepestNodeClone.isFixed()) {
				deepestNodeClone.setN(1);
				return deepestNodeClone.getValue();
			}
		}
		return (modifier == TemporalModifier.BC ? "BC" : "")
				+ head.getSequenceValue();
	}

	public boolean hasModifier(TemporalModifier otherModifier) {
		return this.modifier == otherModifier;
	}

	public boolean hasQuantifier(TemporalQuantifier otherQuantifier) {
		return this.quantifier == otherQuantifier;
	}

	public TemporalSequence intersect(TemporalSequence other) {
		if (other.followsPath("week", "day")
				&& this.followsPath("month", "day")) {
			return new TemporalSequence(this);
		}
		if (this.followsPath("week", "day")
				&& other.followsPath("month", "day")) {
			return new TemporalSequence(other);
		}
		TemporalSequence lessGranular, moreGranular;
		TemporalQuantifier newQuantifier = TemporalQuantifier.ONE;
		if (this.hasQuantifier(TemporalQuantifier.EVERY)
				|| other.hasQuantifier(TemporalQuantifier.EVERY)) {
			newQuantifier = TemporalQuantifier.EVERY;
		}
		if (this.getDeepestNode().getGranularity() < other.getDeepestNode()
				.getGranularity()) {
			lessGranular = new TemporalSequence(this, newQuantifier);
			moreGranular = new TemporalSequence(other, newQuantifier);
		} else {
			lessGranular = new TemporalSequence(other, newQuantifier);
			moreGranular = new TemporalSequence(this, newQuantifier);
		}
		final boolean hasSucceeded = lessGranular.head
				.intersect(moreGranular.head);
		if (hasSucceeded) {
			return lessGranular;
		} else {
			return null;
		}
	}

	public boolean isRange() {
		return head.isFullyFixed();
	}

	@Override
	public Iterator<TemporalSequence> iterator() {
		return new TemporalIterator(this);
	}

	public TemporalSequence normalize() {
		TemporalDuration normalizedHead = head.clone();
		while (normalizedHead.getDefaultParent() != null) {
			normalizedHead = normalizedHead.getDefaultParent();
		}
		TemporalSequence normalizedSequence = new TemporalSequence(
				normalizedHead, quantifier, modifier);
		if (quantifier != TemporalQuantifier.EVERY) {
			if (normalizedSequence.isRange()
					&& (normalizedSequence.followsPath("year", "week", "day") || normalizedSequence
							.followsPath("year", "month", "week", "day"))) {
				final TemporalDuration dayChild = normalizedSequence.getNode(
						"day").getChild();
				normalizedSequence = new TemporalSequence(
						normalizedSequence.getStartJodaTime());
				normalizedSequence.getNode("day").setChild(dayChild);
			}
		}
		return normalizedSequence;
	}

	public TemporalSequence sequenceOfAll(TemporalDuration duration) {
		return new TemporalSequence(duration.emptyClone()).normalize();
	}

	public TemporalSequence shift(TemporalDuration duration) {
		TemporalSequence shifted = new TemporalSequence(this);
		final TemporalDuration nodeToShift = shifted
				.getNodeWithGranularity(duration);
		if (nodeToShift == null) {
			shifted = new TemporalSequence(getStartJodaTime().plus(
					duration.getJodaFullPeriod()));
		} else {
			final TemporalIterator iterator = new TemporalIterator(shifted,
					duration.getGranularity());
			for (int i = 0; i < Math.abs(duration.getN()) + 1
					&& iterator.hasNext(); i++) {
				if (duration.getN() > 0) {
					shifted = iterator.next();
				} else {
					shifted = iterator.previous();
				}
			}
		}
		return shifted.normalize();
	}

	@Override
	public String toString() {
		return head.toString()
				+ (quantifier == TemporalQuantifier.EVERY ? "[EVERY]" : "");
	}

	public TemporalSequence withGranularity(TemporalDuration duration) {
		if (this.getDeepestNode().getGranularity() == duration.getGranularity()) {
			return new TemporalSequence(this);
		}
		final TemporalSequence sequenceWithGranularity = sequenceOfAll(duration)
				.getFixedInstance(this, 0);
		return sequenceWithGranularity;
	}

	public TemporalSequence withModifier(TemporalModifier otherModifier) {
		return new TemporalSequence(this, otherModifier);
	}

	public TemporalSequence withQuantifier(TemporalQuantifier otherQuantifier) {
		return new TemporalSequence(this, otherQuantifier);
	}

	private boolean contains(TemporalSequence other) {
		try {
			return this.getStartJodaTime().compareTo(other.getStartJodaTime()) <= 0
					&& this.getEndJodaTime().compareTo(other.getEndJodaTime()) >= 0;
		} catch (final NullPointerException e) {
			return false;
		}
	}

	private boolean endsBefore(TemporalSequence other) {
		try {
			return this.getEndJodaTime().compareTo(other.getStartJodaTime()) < 0;
		} catch (final NullPointerException e) {
			return false;
		}
	}

	private boolean followsPath(String... path) {
		return head.followsPath(path, 0);
	}

	private TemporalDuration getNode(String name) {
		for (TemporalDuration node = head; node != null; node = node.getChild()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	private boolean startsAfter(TemporalSequence other) {
		try {
			return this.getStartJodaTime().compareTo(other.getEndJodaTime()) > 0;
		} catch (final NullPointerException e) {
			return false;
		}
	}

	private class TemporalIterator implements ListIterator<TemporalSequence> {
		private final List<TemporalDuration>	freeNodes;
		private boolean							hasStarted	= false;
		private final TemporalSequence			sequence;

		public TemporalIterator(TemporalSequence sequence) {
			this.sequence = new TemporalSequence(sequence);
			freeNodes = new LinkedList<>();
		}

		public TemporalIterator(TemporalSequence sequence, int granularity) {
			// Everything at the given granularity and above will be freeNodes
			this(sequence);
			for (TemporalDuration node = this.sequence.head; node != null
					&& node.getGranularity() <= granularity; node = node
					.getChild()) {
				freeNodes.add(node);
				// Initialize any unfixed nodes
				if (!node.isFixed()) {
					node.setN(1);
				}
			}
			// Deepest nodes are incremented first
			Collections.reverse(freeNodes);
		}

		public TemporalIterator(TemporalSequence sequence,
				TemporalSequence start) {
			this(sequence);
			for (TemporalDuration node = this.sequence.getHead(); node != null; node = node
					.getChild()) {
				if (!node.isFixed()) {
					freeNodes.add(node);
				}
			}
			// Deepest nodes are incremented first
			Collections.reverse(freeNodes);

			if (start != null) {
				// Now that we know which were the free variables. Set the
				// starting point
				// to be as close as possible
				this.sequence.getHead().fixCommonAncestors(start.getHead());
			}
			for (final TemporalDuration node : freeNodes) {
				// Initialize any free nodes that weren't fixed by the starting
				// point
				if (!node.isFixed()) {
					node.setN(1);
				}
			}
		}

		@Override
		public void add(TemporalSequence otherSequence) {
			throw new UnsupportedOperationException();
		}

		public TemporalSequence containingUnitOfResult() {
			// e.g. if iterator just returned the summer of 2013, this should
			// return 2013
			final TemporalSequence containingUnit = new TemporalSequence(
					sequence);
			containingUnit.head.pruneGranularityTo(freeNodes.get(0)
					.getGranularity());
			return containingUnit;
		}

		@Override
		public boolean hasNext() {
			if (!hasStarted) {
				return true;
			}
			for (final TemporalDuration node : freeNodes) {
				if (node.canIncrement()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasPrevious() {
			if (!hasStarted) {
				return true;
			}
			for (final TemporalDuration node : freeNodes) {
				if (node.canDecrement()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public TemporalSequence next() {
			if (!hasStarted) {
				hasStarted = true;
			} else {
				for (final TemporalDuration node : freeNodes) {
					if (node.canIncrement()) {
						node.increment();
						break;
					} else {
						node.setN(1);
					}
				}
			}
			return new TemporalSequence(sequence);
		}

		@Override
		public int nextIndex() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TemporalSequence previous() {
			if (!hasStarted) {
				hasStarted = true;
			} else {
				for (final TemporalDuration node : freeNodes) {
					if (node.canDecrement()) {
						node.decrement();
						break;
					} else {
						node.setN(node.getMaximumN());
					}
				}
			}
			return new TemporalSequence(sequence);
		}

		@Override
		public int previousIndex() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(TemporalSequence sequence) {
			throw new UnsupportedOperationException();
		}
	}
}
