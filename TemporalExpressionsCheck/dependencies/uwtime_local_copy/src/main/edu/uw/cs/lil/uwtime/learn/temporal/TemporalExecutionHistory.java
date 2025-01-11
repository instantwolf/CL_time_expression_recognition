package edu.uw.cs.lil.uwtime.learn.temporal;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.uwtime.eval.entities.TemporalSequence;
import edu.uw.cs.lil.uwtime.eval.entities.durations.TemporalDuration;
import edu.uw.cs.utils.composites.Pair;

public class TemporalExecutionHistory implements Serializable {
	private static final long										serialVersionUID	= 4345548231624414895L;

	private final List<Pair<TemporalSequence, TemporalSequence>>	intersectionArguments;
	private final List<Pair<TemporalSequence, TemporalDuration>>	shiftArguments;

	public TemporalExecutionHistory() {
		shiftArguments = new LinkedList<>();
		intersectionArguments = new LinkedList<>();
	}

	public void addIntersectionArguments(TemporalSequence s1,
			TemporalSequence s2) {
		intersectionArguments.add(Pair.of(s1, s2));
	}

	public void addShiftArguments(TemporalSequence sequence,
			TemporalDuration duration) {
		shiftArguments.add(Pair.of(sequence, duration));
	}

	public List<Pair<TemporalSequence, TemporalSequence>> getIntersectionArguments() {
		return intersectionArguments;
	}

	public List<Pair<TemporalSequence, TemporalDuration>> getShiftArguments() {
		return shiftArguments;
	}
}
