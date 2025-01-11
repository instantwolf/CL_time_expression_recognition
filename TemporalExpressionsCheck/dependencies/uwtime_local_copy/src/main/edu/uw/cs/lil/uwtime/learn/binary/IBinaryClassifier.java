package edu.uw.cs.lil.uwtime.learn.binary;

import edu.uw.cs.lil.tiny.data.IDataItem;

public interface IBinaryClassifier<DI extends IDataItem<?>> {
	IBinaryClassifierOutput classify(DI dataItem, IBinaryModel<DI> model);
}
