package edu.uw.cs.lil.uwtime.eval.entities.durations;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import edu.uw.cs.lil.uwtime.eval.TemporalModifier;
import edu.uw.cs.lil.uwtime.eval.TemporalQuantifier;
import edu.uw.cs.lil.uwtime.eval.entities.TemporalEntity;

public abstract class TemporalDuration extends TemporalEntity {
	private int						n;
	protected TemporalModifier		modifier;
	protected TemporalDuration		parent, child;
	protected TemporalQuantifier	quantifier;

	public TemporalDuration(int n) {
		this(n, null);
	}

	public TemporalDuration(int n, TemporalDuration child) {
		this(n, child, TemporalQuantifier.CARDINAL, TemporalModifier.NONE);
	}

	public TemporalDuration(int n, TemporalDuration child,
			TemporalQuantifier quantifier, TemporalModifier modifier) {
		this.n = n;
		if (child != null) {
			this.child = child;
			child.parent = this;
		}
		this.quantifier = quantifier;
		this.modifier = modifier;
	}

	public abstract int atSameGranularity(LocalDateTime jodaTime);

	public boolean canDecrement() {
		return n - 1 > 0;
	}

	public boolean canIncrement() {
		return n + 1 <= getMaximumN();
	}

	@Override
	public abstract TemporalDuration clone();

	public void decrement() {
		// Skip 0
		if (n - 1 == 0) {
			setN(-1);
		} else {
			setN(n - 1);
		}
	}

	public TemporalDuration emptyClone() {
		final TemporalDuration emptyClone = clone();
		emptyClone.child = null;
		emptyClone.parent = null;
		emptyClone.n = 0;
		return emptyClone;
	}

	public void fixCommonAncestors(TemporalDuration other) {
		if (other != null && this.getGranularity() == other.getGranularity()
				&& !this.isFixed()) {
			this.n = other.n;
			if (child != null) {
				child.fixCommonAncestors(other.child);
			}
		}
	}

	public boolean followsPath(String[] path, int i) {
		if (!path[i].equals(getName())) {
			return false;
		} else if (i >= path.length - 1) {
			return true;
		} else if (child == null) {
			return false;
		} else {
			return child.followsPath(path, i + 1); // This node is fine, but the
													// children can fail
		}
	}

	public TemporalDuration getChild() {
		return child;
	}

	public TemporalDuration getDeepestFreeVariable() {
		if (child == null) {
			return isFixed() ? null : this;
		} else {
			final TemporalDuration deepest = child.getDeepestFreeVariable();
			if (deepest != null) {
				return deepest;
			} else {
				return isFixed() ? null : this;
			}
		}
	}

	public TemporalDuration getDeepestNode() {
		return child == null ? this : child.getDeepestNode();
	}

	public abstract TemporalDuration getDefaultParent();

	public abstract String getDurationValue();

	public abstract int getGranularity();

	public TemporalDuration getHead() {
		if (parent != null) {
			return parent.getHead();
		} else {
			return this;
		}
	}

	public Period getJodaFullPeriod() {
		return getJodaUnitPeriod().multipliedBy(n);
	}

	public abstract Period getJodaUnitPeriod();

	public abstract String getLocalSequenceValue();

	public abstract int getMaximumN();

	@Override
	public String getMod() {
		return modifier.toString();
	}

	public int getN() {
		return n;
	}

	public abstract String getName();

	public TemporalDuration getNodeWithGranularity(TemporalDuration duration) {
		if (this.getGranularity() == duration.getGranularity()) {
			return this;
		} else {
			return child == null ? null : child
					.getNodeWithGranularity(duration);
		}
	}

	public TemporalDuration getParent() {
		return parent;
	}

	public String getSequenceValue() {
		final String localValue = getLocalSequenceValue();
		if (localValue == null) {
			return null;
		} else if (child == null) {
			return localValue;
		} else {
			final String childValue = child.getSequenceValue();
			if (childValue == null) {
				return null;
			} else {
				return localValue + childValue;
			}
		}
	}

	public abstract LocalDateTime getStartJodaTime(LocalDateTime jodaDate);

	public TemporalDuration getStructuralCopy() {
		final TemporalDuration copy = clone();
		if (copy.isFixed()) {
			copy.setN(1);
		}
		return copy;
	}

	@Override
	public String getStructure() {
		return getStructuralCopy().toString();
	}

	@Override
	public String getType() {
		return "DURATION";
	}

	@Override
	public String getValue() {
		if (quantifier == TemporalQuantifier.SOME) {
			setN(0);
		}
		return getDurationValue();
	}

	public boolean hasModifier(TemporalModifier otherModifier) {
		return this.modifier == otherModifier;
	}

	public boolean hasQuantifier(TemporalQuantifier otherQuantifier) {
		return this.quantifier == otherQuantifier;
	}

	public void increment() {
		setN(n + 1);
	}

	public boolean intersect(TemporalDuration other) {
		if (other == null) {
			return true;
		} else if (other.getGranularity() > this.getGranularity()) {
			if (this.child == null) {
				this.setChild(other);
				return true;
			} else {
				return this.child.intersect(other);
			}
		} else if (other.getGranularity() == this.getGranularity()) {
			if (this.isFixed() && other.isFixed()) {
				if (this.n == other.n) {
					if (this.child != null) {
						return this.child.intersect(other.child);
					} else {
						return true;
					}
				} else {
					return false; // Empty intersection
				}
			} else if (this.isFixed() && !other.isFixed()) {
				this.child = other.child;
				return true;
			} else if (!this.isFixed() && !other.isFixed()) {
				if (this.child != null) {
					return this.child.intersect(other.child);
				} else {
					return true;
				}
			} else {
				this.n = other.n;
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isFixed() {
		return n > 0;
	}

	public boolean isFullyFixed() {
		return isFixed() && (child == null || child.isFullyFixed());
	}

	public void pruneGranularityTo(int granularity) {
		if (getGranularity() >= granularity) {
			child = null;
		} else if (child != null) {
			child.pruneGranularityTo(granularity);
		}
	}

	public void setChild(TemporalDuration child) {
		this.child = child;
		if (child != null) {
			child.parent = this;
		}
	}

	public void setModifier(TemporalModifier modifier) {
		this.modifier = modifier;
	}

	public void setN(int n) {
		this.n = n;
	}

	public void setQuantifier(TemporalQuantifier quantifier) {
		this.quantifier = quantifier;
	}

	@Override
	public String toString() {
		return getName() + "(" + n + ")" + (child == null ? "" : "-->" + child);
	}

	public TemporalDuration withModifier(TemporalModifier otherModifier) {
		final TemporalDuration newNode = this.clone();
		newNode.setModifier(otherModifier);
		return newNode;
	}

	public TemporalDuration withQuantifier(TemporalQuantifier otherQuantifier) {
		final TemporalDuration newNode = this.clone();
		newNode.setQuantifier(otherQuantifier);
		return newNode;
	}
}
