package realdata;

import java.util.Collection;
import java.util.HashMap;

import online.OnlineSketchStreamer;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.OnlineSketch;

public class OnlineSketchNewsGenerator implements NewsGenerator {

	private OnlineSketchStreamer oss;
	public OnlineSketchNewsGenerator(EventsBag ieb) {
		oss = new OnlineSketchStreamer();
		for(int i = 0 ;i < ieb.getObjectCount(); i++) {
			for(Event e : ieb.getEvents(i)) {
				oss.query(e);
			}
		}
	}
	
	@Override
	public Collection<News> getNews(int id) {
		HashMap<Integer, OnlineSketch> result = oss.getSketches();
		if(result.containsKey(id)) {
			return result.get(id).getSketch();
		}
		return null;
	}

	@Override
	public Collection<Integer> getAvailableIDs() {
		return null;
	}

}
