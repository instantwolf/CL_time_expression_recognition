package edu.uw.cs.lil.uwtime.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.utils.composites.Pair;

public class DependencyUtils {
	private DependencyUtils() {
	}

	public static List<DependencyParseToken> getDependencyChildren(
			TemporalJointChunk chunk) {
		final List<DependencyParseToken> tokens = chunk.getSentence()
				.getDependencyParse();
		final List<DependencyParseToken> children = new LinkedList<>();
		for (final DependencyParseToken t : tokens) {
			if (t.parent >= chunk.getStart() && t.parent <= chunk.getEnd()) {
				children.add(t);
			}
		}
		return children;
	}

	public static DependencyParseToken getDependencyParent(
			TemporalJointChunk chunk) {
		final List<DependencyParseToken> tokens = chunk.getSentence()
				.getDependencyParse();
		return tokens.get(tokens.get(chunk.getStart()).parent);
	}

	public static int getFirstDeterminerParent(TemporalJointChunk chunk) {
		final List<DependencyParseToken> tokens = chunk.getSentence()
				.getDependencyParse();
		for (int i = chunk.getStart(); i <= chunk.getEnd(); i++) {
			final DependencyParseToken t = tokens.get(i);
			if (t.partOfSpeech.equals("DT")) {
				return t.parent;
			}
		}
		return -1;
	}

	public static String getGovernorVerb(TemporalJointChunk chunk) {
		final List<DependencyParseToken> tokens = chunk.getSentence()
				.getDependencyParse();
		for (DependencyParseToken t = tokens.get(chunk.getStart()); t != null; t = t.parent >= 0 ? tokens
				.get(t.parent) : null) {
			if (t.partOfSpeech.startsWith("VB")) {
				return t.word;
			}
		}
		return null;
	}

	public static String getGovernorVerbFeature(TemporalJointChunk chunk) {
		final List<DependencyParseToken> tokens = chunk.getSentence()
				.getDependencyParse();
		Pair<DependencyParseToken, Integer> nearestToken = null;
		for (int i = chunk.getStart(); i <= chunk.getEnd(); i++) {
			final Pair<DependencyParseToken, Integer> candidateToken = getGovernorVerb(
					tokens, i);
			if (nearestToken == null || candidateToken != null
					&& candidateToken.second() < nearestToken.second()) {
				nearestToken = candidateToken;
			}
		}

		if (nearestToken == null) {
			return "null";
		}

		final DependencyParseToken governorVerb = nearestToken.first();

		final List<DependencyParseToken> children = new LinkedList<>();
		for (final DependencyParseToken t : tokens) {
			if (t.parent == governorVerb.index && t.type.startsWith("aux")) {
				children.add(t);
			}
		}
		Collections.sort(children);
		final StringBuilder sb = new StringBuilder();
		for (final DependencyParseToken t : children) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(t.getWord());
		}
		return String
				.format("%s[%s]", governorVerb.partOfSpeech, sb.toString());
	}

	public static String getPOSOf(String token, TemporalSentence sentence) {
		final List<DependencyParseToken> tokens = sentence.getDependencyParse();
		for (final DependencyParseToken t : tokens) {
			if (t.word.equals(token)) {
				return t.partOfSpeech;
			}
		}
		return null;
	}

	public static List<DependencyParseToken> parseDependencyParse(String s) {
		final String[] lines = s.split("\n");
		final List<DependencyParseToken> tokens = new ArrayList<>();
		for (int i = 0; i < lines.length; i++) {
			tokens.add(new DependencyParseToken(lines[i]));
		}
		return tokens;
	}

	private static Pair<DependencyParseToken, Integer> getGovernorVerb(
			List<DependencyParseToken> tokens, int index) {
		int distance = 0;
		for (DependencyParseToken t = tokens.get(index); t != null; t = t.parent >= 0 ? tokens
				.get(t.parent) : null) {
			if (t.partOfSpeech.startsWith("VB")) {
				return Pair.of(t, distance);
			}
			distance++;
		}
		return null;
	}

	public static class DependencyParseToken implements Serializable,
			Comparable<DependencyParseToken> {
		private static final long	serialVersionUID	= -2495751561981553382L;
		private final int			index;
		private final int			parent;
		private final String		partOfSpeech;
		private final String		type;
		private final String		word;

		private DependencyParseToken(String line) {
			final String[] columns = line.split("\t");
			index = Integer.parseInt(columns[0]) - 1;
			parent = Integer.parseInt(columns[6]) - 1;
			word = columns[1];
			partOfSpeech = columns[3];
			type = columns[7];
		}

		@Override
		public int compareTo(DependencyParseToken other) {
			return Integer.valueOf(this.index).compareTo(other.index);
		}

		public String getPOS() {
			return partOfSpeech;
		}

		public String getWord() {
			return word.toLowerCase();
		}

		@Override
		public String toString() {
			return String.format("%d:%s[%s](%s->%d)", index, word,
					partOfSpeech, type, parent);
		}
	}
}
