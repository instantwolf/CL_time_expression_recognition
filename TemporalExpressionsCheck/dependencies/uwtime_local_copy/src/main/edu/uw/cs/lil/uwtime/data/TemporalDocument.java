package edu.uw.cs.lil.uwtime.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDocument implements Serializable,
		IDataCollection<TemporalSentence> {
	final private static String													DOCUMENT_FORMAT		= "<TimeML>\n"
																											+ "<DOCID>%s</DOCID>\n"
																											+ "<DCT><TIMEX3 tid=\"t0\" type=\"DATE\" value=\"%s\" functionInDocument=\"CREATION_TIME\">%s</TIMEX3></DCT>\n"
																											+ "<TEXT>%s</TEXT>"
																											+ "\n</TimeML>";
	private static final long													serialVersionUID	= -3478876003961945902L;
	private final List<TemporalJointChunk>										bodyMentions;
	private List<TemporalSentence>												bodySentences;

	private String																bodyText;
	private String																docID;

	private TemporalJointChunk													documentCreationMention;
	private String																documentCreationText;
	private Map<Integer, Pair<TemporalSentence, Integer>>						startCharToToken,
			endCharToToken;

	private final Map<Pair<TemporalSentence, Integer>, Pair<Integer, Integer>>	tokenToChar;

	public TemporalDocument() {
		this.bodyMentions = new LinkedList<>();
		this.tokenToChar = new HashMap<>();
	}

	public TemporalDocument(TemporalDocument other) {
		this.docID = other.docID;
		this.bodyText = other.bodyText;
		this.documentCreationText = other.documentCreationText;

		this.tokenToChar = other.tokenToChar;
		this.startCharToToken = other.startCharToToken;
		this.endCharToToken = other.endCharToToken;

		this.bodyMentions = other.bodyMentions;
		this.bodySentences = other.bodySentences;
		this.documentCreationMention = other.documentCreationMention;
	}

	private static String postfilterText(String s) {
		return s.replace("\\/", "/");
	}

	private static String prefilterText(String s) {
		return s.replace("-", " ").replace("/", " ").replace("&", " ");
	}

	public String annotate(String text) {
		return String.format(DOCUMENT_FORMAT, docID, getDocumentCreationTime()
				.getResult().getValue(), getDocumentCreationTime().getPhrase(),
				text.replace("&", "&amp;"));
	}

	public String annotateData() {
		final char[] originalDocumentText = bodyText.toCharArray();
		final Map<Integer, List<String>> annotationInsertionMap = new HashMap<>();
		int tidCount = 1;
		for (final TemporalSentence ts : this) {
			final List<TemporalJointChunk> annotatedChunks = new LinkedList<>();
			for (final TemporalJointChunk goldChunk : ts.getLabel()) {
				boolean hasOverlap = false;
				for (final TemporalJointChunk possibleOverlap : annotatedChunks) {
					if (possibleOverlap.overlapsWith(goldChunk)) {
						hasOverlap = true;
						break;
					}
				}
				if (hasOverlap || goldChunk.getEnd() < goldChunk.getStart()) {
					continue;
				}
				final MentionResult goldResult = goldChunk.getResult();
				final String tid = "t"
						+ (goldChunk.getTID() != -1 ? goldChunk.getTID()
								: tidCount);
				final Pair<Integer, Integer> startTokenSpan = tokenToChar
						.get(Pair.of(ts, goldChunk.getStart()));
				final Pair<Integer, Integer> endTokenSpan = tokenToChar
						.get(Pair.of(ts, goldChunk.getEnd()));

				if (!annotationInsertionMap.containsKey(startTokenSpan.first())) {
					annotationInsertionMap.put(startTokenSpan.first(),
							new LinkedList<String>());
				}
				annotationInsertionMap.get(startTokenSpan.first()).add(
						goldResult.beginAnnotation(tid));

				if (!annotationInsertionMap.containsKey(endTokenSpan.second())) {
					annotationInsertionMap.put(endTokenSpan.second(),
							new LinkedList<String>());
				}
				annotationInsertionMap.get(endTokenSpan.second()).add(
						goldResult.endAnnotation());
				tidCount++;
				annotatedChunks.add(goldChunk);
			}
		}
		final StringBuilder annotatedDocument = new StringBuilder();
		for (int i = 0; i < originalDocumentText.length; i++) {
			if (annotationInsertionMap.containsKey(i)) {
				for (final String s : annotationInsertionMap.get(i)) {
					annotatedDocument.append(s);
				}
			}
			annotatedDocument.append(originalDocumentText[i]);
		}
		return annotate(annotatedDocument.toString());
	}

	public String annotatePredictions(
			List<Pair<TemporalSentence, TemporalJointChunk>> allPredictions) {
		final char[] originalDocumentText = bodyText.toCharArray();
		final Map<Integer, String> annotationInsertionMap = new HashMap<>();
		int tidCount = 1;
		for (final Pair<TemporalSentence, TemporalJointChunk> prediction : allPredictions) {
			final Pair<Integer, Integer> startTokenSpan = tokenToChar.get(Pair
					.of(prediction.first(), prediction.second().getStart()));
			final Pair<Integer, Integer> endTokenSpan = tokenToChar.get(Pair
					.of(prediction.first(), prediction.second().getEnd()));
			annotationInsertionMap.put(startTokenSpan.first(), prediction
					.second().getResult().beginAnnotation("t" + tidCount));
			annotationInsertionMap.put(endTokenSpan.second(), prediction
					.second().getResult().endAnnotation());
			tidCount++;
		}

		final StringBuilder annotatedDocument = new StringBuilder();
		for (int i = 0; i < originalDocumentText.length; i++) {
			if (annotationInsertionMap.containsKey(i)) {
				annotatedDocument.append(annotationInsertionMap.get(i));
			}
			annotatedDocument.append(originalDocumentText[i]);
		}
		return annotate(annotatedDocument.toString());
	}

	public void doPreprocessing(StanfordCoreNLP pipeline,
			GrammaticalStructureFactory gsf) {
		bodySentences = getPreprocessedSentences(documentCreationText,
				Collections.singletonList(documentCreationMention), pipeline,
				gsf);
		bodySentences.addAll(getPreprocessedSentences(prefilterText(bodyText),
				bodyMentions, pipeline, gsf));
	}

	public Pair<Integer, Integer> getCharRange(TemporalSentence sentence, int i) {
		return tokenToChar.get(Pair.of(sentence, i));
	}

	public String getDocID() {
		return docID;
	}

	public String getDocumentCreationText() {
		return documentCreationText;
	}

	public TemporalJointChunk getDocumentCreationTime() {
		return documentCreationMention;
	}

	public String getOriginalText() {
		return bodyText;
	}

	public String getOriginalText(int charStart, int charEnd) {
		return bodyText.substring(charStart, charEnd);
	}

	public List<TemporalSentence> getSentences() {
		return bodySentences;
	}

	public Pair<TemporalSentence, Integer> getTokenFromEndChar(int endChar) {
		return endCharToToken.get(endChar);
	}

	public Pair<TemporalSentence, Integer> getTokenFromStartChar(int startChar) {
		return startCharToToken.get(startChar);
	}

	public Map<Pair<TemporalSentence, Integer>, Pair<Integer, Integer>> getTokenToChar() {
		return tokenToChar;
	}

	public void insertDCTMention(String type, String value, String mod,
			int offset, int tid) {
		insertDCTMention(new TemporalJointChunk(type, value, mod, offset, -1,
				tid));
	}

	public void insertDCTMention(TemporalJointChunk chunk) {
		documentCreationMention = chunk;
	}

	// For timeml with embedded mentions
	public void insertMention(String type, String value, String mod,
			int offset, int tid) {
		insertMention(new TemporalJointChunk(type, value, mod, offset, -1, tid));
	}

	// For clinical narratives with separate mentions
	public void insertMention(String type, String value, String mod,
			int startChar, int endChar, int tid) {
		insertMention(new TemporalJointChunk(type, value, mod, startChar,
				endChar, tid));
	}

	public void insertMention(TemporalJointChunk chunk) {
		bodyMentions.add(chunk);
	}

	@Override
	public Iterator<TemporalSentence> iterator() {
		return bodySentences.iterator();
	}

	public void setDCTMentionText(String text) {
		documentCreationMention.setCharEnd(text);
	}

	public void setDCTText(String text) {
		documentCreationText = text;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	public void setLastMentionText(String text) {
		bodyMentions.get(bodyMentions.size() - 1).setCharEnd(text);
	}

	public void setText(String text) {
		this.bodyText = text;
	}

	@Override
	public int size() {
		return bodySentences.size();
	}

	@Override
	public String toString() {
		return bodyText;
	}

	public TemporalDocument withoutDCT() {
		final TemporalDocument newDoc = new TemporalDocument(this);
		newDoc.bodySentences.remove(documentCreationMention.getSentence());
		newDoc.bodyMentions.remove(documentCreationMention);
		return newDoc;
	}

	private List<TemporalSentence> getPreprocessedSentences(String text,
			List<TemporalJointChunk> mentions, StanfordCoreNLP pipeline,
			GrammaticalStructureFactory gsf) {
		final Annotation a = new Annotation(text);
		pipeline.annotate(a);

		startCharToToken = new HashMap<>();
		endCharToToken = new HashMap<>();

		final List<TemporalSentence> sentences = new LinkedList<>();

		for (final CoreMap sentence : a.get(SentencesAnnotation.class)) {
			final TemporalSentence newSentence = new TemporalSentence(this,
					getDocumentCreationTime().getResult().getValue());
			for (final CoreLabel token : sentence.get(TokensAnnotation.class)) {
				final Pair<TemporalSentence, Integer> tokenIndex = Pair.of(
						newSentence, newSentence.getNumTokens());
				startCharToToken.put(token.beginPosition(), tokenIndex);
				endCharToToken.put(token.endPosition(), tokenIndex);
				tokenToChar.put(tokenIndex,
						Pair.of(token.beginPosition(), token.endPosition()));
				newSentence.insertToken(postfilterText(token.originalText()));
			}
			final Tree tree = sentence
					.get(TreeCoreAnnotations.TreeAnnotation.class);
			final GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			final String dp = GrammaticalStructure.dependenciesToString(gs,
					gs.typedDependencies(false), tree, true, false);
			newSentence.saveDependencyParse(dp);
			sentences.add(newSentence);
		}

		for (final TemporalJointChunk t : mentions) {
			t.alignTokens(startCharToToken, endCharToToken);
		}
		return sentences;
	}
}
