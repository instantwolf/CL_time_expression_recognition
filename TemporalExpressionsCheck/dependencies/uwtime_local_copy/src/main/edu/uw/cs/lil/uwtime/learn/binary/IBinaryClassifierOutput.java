package edu.uw.cs.lil.uwtime.learn.binary;

import edu.uw.cs.lil.tiny.base.hashvector.IHashVectorImmutable;

public interface IBinaryClassifierOutput {
	boolean getBinaryClass();

	IHashVectorImmutable getFeatures();

	double getProbability(boolean label);

	double getScore();
}
