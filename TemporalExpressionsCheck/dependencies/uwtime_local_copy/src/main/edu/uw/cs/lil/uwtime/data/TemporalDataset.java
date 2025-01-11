package edu.uw.cs.lil.uwtime.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.uw.cs.lil.tiny.data.collection.IDataCollection;

public class TemporalDataset implements IDataCollection<TemporalSentence>,
		java.io.Serializable {
	private static final long				serialVersionUID	= -9138434230690313768L;
	private final List<TemporalDocument>	documents;										// Should
																							// refer
																							// to
																							// the
																							// same
																							// sentences.
	private final String					name;

	private final List<TemporalSentence>	sentences;

	public TemporalDataset(String name) {
		this.name = name;
		documents = new LinkedList<>();
		sentences = new LinkedList<>();
	}

	public TemporalDataset(String name, List<TemporalDocument> documents) {
		this(name);
		for (final TemporalDocument doc : documents) {
			addDocument(doc);
		}
	}

	public void addDocument(TemporalDocument document) {
		addDocument(document, false);
	}

	public void addDocument(TemporalDocument document, boolean ignoreDCT) {
		documents.add(ignoreDCT ? document.withoutDCT() : document);
		sentences.addAll(document.getSentences());
	}

	public List<TemporalDocument> getDocuments() {
		return documents;
	}

	public String getName() {
		return name;
	}

	@Override
	public Iterator<TemporalSentence> iterator() {
		return sentences.iterator();
	}

	public List<List<TemporalDocument>> partition(int k) {
		final List<List<TemporalDocument>> partitions = new ArrayList<>(k);
		for (int i = 0; i < k; i++) {
			partitions.add(new LinkedList<TemporalDocument>());
		}

		final List<TemporalDocument> shuffledDocuments = new LinkedList<>(
				documents);
		final Random seed = new Random(0);
		Collections.shuffle(shuffledDocuments, seed);

		int count = 0;
		for (final TemporalDocument s : shuffledDocuments) {
			partitions.get(count % k).add(s);
			count++;
		}
		return partitions;
	}

	@Override
	public int size() {
		return sentences.size();
	}

	public void sort() {
		Collections.sort(documents,
				(d1, d2) -> d1.getDocID().compareTo(d2.getDocID()));
	}

	@Override
	public String toString() {
		String s = "";
		for (final TemporalSentence sentence : sentences) {
			s += sentence + "\n";
		}
		return s;
	}

	public TemporalDataset withoutDocumentCreationTimes() {
		final TemporalDataset newDataset = new TemporalDataset(name);
		for (final TemporalDocument d : documents) {
			newDataset.addDocument(d, true);
		}
		return newDataset;
	}
}
