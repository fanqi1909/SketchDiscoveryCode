package realdata;

import java.util.Collection;

import online.OnlineSkylineStreamer;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;

public class OnlineSkylineGenerator implements NewsGenerator{
	
	private final EventsBag eb;
	private OnlineSkylineStreamer oss;
	public OnlineSkylineGenerator(EventsBag ieb) {
		eb = ieb;
		oss = new OnlineSkylineStreamer();
		for(int i= 0; i < eb.getEventsCount(); i++) {
			for(Event e : eb.getEvents(i)) {
				oss.query(e);
			}
		}
	}
	
	
	
	@Override
	public Collection<News> getNews(int id) {
		if(oss.getSketches().containsKey(id)) {
			return oss.getSketchSet().get(id);
		}
		return null;
	}



	@Override
	public Collection<Integer> getAvailableIDs() {
		return null;
	}
	
}
