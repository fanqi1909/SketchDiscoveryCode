package realdata;

import java.util.Collection;

import offline.IndexCreation;
import offline.SkylineMining;

import datastructures.EventsBag;
import datastructures.News;

public class SkylineGenerator implements NewsGenerator{

	private SkylineMining sm ;
	public SkylineGenerator(EventsBag eb) {
		IndexCreation ic = new IndexCreation(eb);
		ic.buildIndex();
		sm = new SkylineMining(ic.getIndex());
		System.out.println(sm.getAvailIDs());
	}
	
	
	@Override
	public Collection<News> getNews(int id) {
		
		return sm.getSkyline(id); 
	}


	@Override
	public Collection<Integer> getAvailableIDs() {
		return sm.getAvailIDs();
	}
}
