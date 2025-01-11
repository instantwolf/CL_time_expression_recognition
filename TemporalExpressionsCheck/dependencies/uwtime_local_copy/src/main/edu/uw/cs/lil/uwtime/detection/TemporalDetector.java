package edu.uw.cs.lil.uwtime.detection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import edu.uw.cs.lil.tiny.base.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.base.hashvector.KeyArgs;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.uwtime.chunking.AbstractChunkerOutput;
import edu.uw.cs.lil.uwtime.chunking.TemporalChunker;
import edu.uw.cs.lil.uwtime.chunking.TemporalChunkerOutput;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.learn.binary.IBinaryModel;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.AgeReferenceFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.DeterminerFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.EntityStructureDetectionFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.LexicalPOSFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.QuotationFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.TemporalLexicalOriginFeatureSet;
import edu.uw.cs.lil.uwtime.learn.featuresets.detection.TimePrepositionsFeatureSet;
import edu.uw.cs.lil.uwtime.learn.temporal.TemporalBinaryModel;
import edu.uw.cs.lil.uwtime.learn.temporal.TemporalClassifier;
import edu.uw.cs.lil.uwtime.parsing.grammar.TemporalGrammar;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDetector
		extends
		AbstractDetector<TemporalSentence, AbstractChunkerOutput<TemporalJointChunk>> {
	private static KeyArgs								THRESHOLD_KEY	= new KeyArgs(
																				"detection_threshold");
	private final TemporalChunker						chunker;
	private final TemporalClassifier					classifier;
	private final IBinaryModel<TemporalChunkerOutput>	filteringModel;

	public TemporalDetector(TemporalGrammar grammar) {
		this.classifier = new TemporalClassifier(0.5);

		this.filteringModel = new TemporalBinaryModel.Builder()
				.addFeatureSet(new LexicalPOSFeatureSet())
				.addFeatureSet(new QuotationFeatureSet(2))
				.addFeatureSet(new TemporalLexicalOriginFeatureSet())
				.addFeatureSet(new TimePrepositionsFeatureSet())
				.addFeatureSet(new AgeReferenceFeatureSet())
				.addFeatureSet(new DeterminerFeatureSet())
				.addFeatureSet(new EntityStructureDetectionFeatureSet())
				.build();

		this.chunker = new TemporalChunker(grammar, classifier, filteringModel);
	}

	@Override
	public List<Pair<TemporalSentence, List<AbstractChunkerOutput<TemporalJointChunk>>>> detectMentions(
			IDataCollection<TemporalSentence> testData) {
		final List<Pair<TemporalSentence, List<AbstractChunkerOutput<TemporalJointChunk>>>> allOutputs = new LinkedList<>();
		for (final TemporalSentence sentence : testData) {
			final List<AbstractChunkerOutput<TemporalJointChunk>> sentenceOutputs = new LinkedList<>();
			final Pair<List<TemporalChunkerOutput>, List<TemporalChunkerOutput>> detectionOutput = chunker
					.chunk(sentence);
			for (final TemporalChunkerOutput filteredOutput : detectionOutput
					.first()) {
				sentenceOutputs.add(filteredOutput);
			}
			allOutputs.add(Pair.of(sentence, sentenceOutputs));
		}
		return allOutputs;
	}

	@Override
	public IHashVector getModel() {
		return filteringModel.getTheta();
	}

	@Override
	public void loadModel(InputStream is) throws IOException,
			ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(is)) {
			filteringModel.getTheta().clear();
			((IHashVector) ois.readObject()).addTimesInto(1.0,
					filteringModel.getTheta());
			classifier.setThreshold(filteringModel.getTheta()
					.get(THRESHOLD_KEY));
			ois.close();
		}
	}

	@Override
	public void saveModel(String filename) throws IOException {
		filteringModel.getTheta().set(THRESHOLD_KEY, classifier.getThreshold());
		try (ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(filename))) {
			out.writeObject(filteringModel.getTheta());
		}
	}
}
