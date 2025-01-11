package edu.uw.cs.lil.uwtime.eval.entities.durations;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import edu.uw.cs.lil.uwtime.eval.TemporalModifier;
import edu.uw.cs.lil.uwtime.eval.TemporalQuantifier;


public class TemporalHour extends TemporalDuration {
	public TemporalHour(int n) {
		super(n);
	}
	
	public TemporalHour(int n, TemporalDuration child) {
		super(n, child);
	}
	
	public TemporalHour(int n, TemporalDuration child, TemporalQuantifier quantifier, TemporalModifier modifier) {
		super(n, child, quantifier, modifier);
	}

	@Override
	public String getName() {
		return "hour";
	}

	@Override
	public String getLocalSequenceValue() {
		return "T" + (isFixed() ? String.format("%02d", getN() - 1) : "XX");
	}

	@Override
	public int getGranularity() {
		return 8;
	}

	@Override
	public TemporalDuration getDefaultParent() {
		return new TemporalDay(0, this);
	}

	@Override
	public TemporalDuration clone() {
		return new TemporalHour(getN(), child == null ? null : child.clone(), quantifier, modifier);
	}

	@Override
	public int getMaximumN() {
		return 24;
	}

	@Override
	public LocalDateTime getStartJodaTime(LocalDateTime jodaDate) {
		jodaDate = jodaDate.withHourOfDay(getN() - 1);
		return child == null ? jodaDate : child.getStartJodaTime(jodaDate);
	}

	@Override
	public Period getJodaUnitPeriod() {
		return new Period().withHours(1);
	}
	
	@Override
	public String getDurationValue() {
		return String.format("PT%sH",(isFixed() ? getN() : "X"));
	}

	@Override
	public int atSameGranularity(LocalDateTime jodaTime) {
		return jodaTime.getHourOfDay() + 1;
	}
}
