package edu.uw.cs.lil.uwtime.data;

import java.util.Iterator;
import java.util.LinkedList;

import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;

public class TemporalChunkDataset implements
		IDataCollection<TemporalJointChunk> {
	private final LinkedList<TemporalJointChunk>	chunks;

	public TemporalChunkDataset() {
		chunks = new LinkedList<>();
	}

	public static TemporalChunkDataset getGoldChunkDataset(
			IDataCollection<TemporalSentence> dataset) {
		final TemporalChunkDataset chunkDataset = new TemporalChunkDataset();
		for (final TemporalSentence sentence : dataset) {
			for (final TemporalJointChunk chunk : sentence.getLabel()) {
				chunkDataset.chunks.add(chunk);
			}
		}
		return chunkDataset;
	}

	@Override
	public Iterator<TemporalJointChunk> iterator() {
		return chunks.iterator();
	}

	@Override
	public int size() {
		return chunks.size();
	}

	@Override
	public String toString() {
		return chunks.toString();
	}
}
