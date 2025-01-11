package edu.uw.cs.lil.uwtime.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.LocalDateTime;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.util.Filters;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.lil.uwtime.data.TemporalDataset;
import edu.uw.cs.lil.uwtime.data.TemporalDocument;
import edu.uw.cs.lil.uwtime.detection.TemporalDetector;
import edu.uw.cs.lil.uwtime.parsing.grammar.TemporalGrammar;
import edu.uw.cs.lil.uwtime.resolution.TemporalResolver;
import edu.uw.cs.lil.uwtime.utils.FileUtils;
import edu.uw.cs.lil.uwtime.utils.TemporalDomain;
import edu.uw.cs.lil.uwtime.utils.TemporalLog;

public class TemporalAnnotator {
	private final Map<TemporalDomain, TemporalDetector>	detectors;
	private final GrammaticalStructureFactory			gsf;
	private final StanfordCoreNLP						pipeline;
	private final Properties							props;
	private final Map<TemporalDomain, TemporalResolver>	resolvers;

	public TemporalAnnotator() throws ClassNotFoundException, IOException {
		initLogs();
		initServices();

		final TemporalGrammar grammar = new TemporalGrammar();
		detectors = new HashMap<>();
		detectors.put(TemporalDomain.NEWSWIRE, new TemporalDetector(grammar));
		detectors.get(TemporalDomain.NEWSWIRE).loadModel(
				this.getClass().getResourceAsStream(
						"/models/newswire/detection.ser"));
		detectors.put(TemporalDomain.NARRATIVE, new TemporalDetector(grammar));
		detectors.get(TemporalDomain.NARRATIVE).loadModel(
				this.getClass().getResourceAsStream(
						"/models/narrative/detection.ser"));
		detectors.put(TemporalDomain.OTHER, new TemporalDetector(grammar));
		detectors.get(TemporalDomain.OTHER).loadModel(
				this.getClass().getResourceAsStream(
						"/models/unknown/detection.ser"));

		resolvers = new HashMap<>();
		resolvers.put(TemporalDomain.NEWSWIRE, new TemporalResolver(grammar));
		resolvers.get(TemporalDomain.NEWSWIRE).loadModel(
				this.getClass().getResourceAsStream(
						"/models/newswire/resolution.ser"));
		resolvers.put(TemporalDomain.NARRATIVE, new TemporalResolver(grammar));
		resolvers.get(TemporalDomain.NARRATIVE).loadModel(
				this.getClass().getResourceAsStream(
						"/models/narrative/resolution.ser"));
		resolvers.put(TemporalDomain.OTHER, new TemporalResolver(grammar));
		resolvers.get(TemporalDomain.OTHER).loadModel(
				this.getClass().getResourceAsStream(
						"/models/unknown/resolution.ser"));

		props = new Properties();
		props.put("annotators", "tokenize, ssplit, parse");
		props.put("tokenize.class", "PTBTokenizer");
		pipeline = new StanfordCoreNLP(props);
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory(
				Filters.<String> acceptFilter(), new SemanticHeadFinder(false));
	}

	public static void initServices() throws IOException {
		LogicLanguageServices
				.setInstance(new LogicLanguageServices.Builder(
						new TypeRepository(
								FileUtils.streamToFile(
										TemporalAnnotator.class
												.getResourceAsStream("/lexicon/temporal.types"),
										"types")), new FlexibleTypeComparator())
						.setNumeralTypeName("n").build());
	}

	private static void initLogs() {
		TemporalLog.suppressLabel("debug");
	}

	public DocumentAnnotation extract(String text) {
		return extract(text, LocalDateTime.now().toString("yyyy-MM-dd"),
				TemporalDomain.OTHER);
	}

	public DocumentAnnotation extract(String text, String dct,
			TemporalDomain domain) {
		TemporalDocument doc = new TemporalDocument();
		doc.setText(text);
		doc.setDocID("temp_doc");
		doc.insertDCTMention("DATE", dct, null, 0, 0);
		doc.setDCTMentionText(dct);
		doc.setDCTText(dct);
		doc.doPreprocessing(pipeline, gsf);
		doc = doc.withoutDCT();
		final TemporalDataset d = new TemporalDataset("temp_dataset");
		d.addDocument(doc);
		return new DocumentAnnotation(doc, resolvers.get(domain)
				.resolveMentions(detectors.get(domain).detectMentions(d)));
	}
}
