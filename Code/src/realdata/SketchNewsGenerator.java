package realdata;

import java.util.Collection;

import offline.IndexCreation;
import offline.SketchMining;

import datastructures.EventsBag;
import datastructures.News;

public class SketchNewsGenerator implements NewsGenerator{
	
	private final EventsBag eb;
	private IndexCreation ic;
	private SketchMining sm;
	
	public SketchNewsGenerator(EventsBag ieb) {
		eb = ieb;
		ic = new IndexCreation(eb);
		ic.buildIndex();
		sm = new SketchMining(ic.getIndex());
	}
	
	@Override
	public Collection<News> getNews(int id) {
		if(sm.getAvailIDs().contains(id)) {
			return sm.getSketch(id);
		}
		return null;
	}

	@Override
	public Collection<Integer> getAvailableIDs() {
		return sm.getAvailIDs();
	}
}
