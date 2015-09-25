package offline;

import datastructures.WindowIndex;

public interface IndexCreator {
	public void buildIndex();
	public WindowIndex getIndex();
}
