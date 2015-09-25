package offline;

//import io.SampleDataGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

//import datastructures.Event;
//import datastructures.EventsBag;
import datastructures.News;
import datastructures.Sketch;
import datastructures.WindowIndex;


public class SketchMining {
	private static final int P = conf.Constants.P;
	/**
	 * each object contains a set of news
	 * use submodular optimization to find best k news;
	 */
	private HashMap<Integer, Sketch> sketches;
	
	
	public SketchMining (WindowIndex wi) {
		sketches = new HashMap<>();
		//scan the window index to get the news candidates for each object
		//during the scan, needs to fill up the rank information
		int total_win_size = wi.getCurrentWindowLength();
		for(int i = 0; i < total_win_size; i++) {
			TreeSet<News> index = wi.getIndexAt(i);
			if(index == null || index.isEmpty()) {
				//remaining indexes are all empty
				break;
			}
			//scan the index in ascending manner
			int next_rank = 0;
			Iterator<News> itr = index.descendingIterator();
			while(itr.hasNext()) {
				News n = itr.next();
				n.setRank(P-(next_rank++));
				int id = n.getID();
//				objects[id].add(n);
				if(!sketches.containsKey(id)) {
					sketches.put(id, new Sketch(id));
				}
				sketches.get(id).addNews(n);
			}
		}
		//at this point, every object has its sketches candidate
		for(Integer id : sketches.keySet()) {
			sketches.get(id).computeSketch();
		}
	}
	
	public ArrayList<News> getSketch(int id) {
		return sketches.get(id).getSketch();
	}
	
	public Set<Integer> getAvailIDs() {
		return sketches.keySet();
	}
	
	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		EventsBag eb = new EventsBag();
//		for (ArrayList<Event> e : SampleDataGen.genEventsList(1000, 20)) {
//			for (Event e1 : e) {
//				eb.insertEvent(e1);
//			}
//		}
//		System.out.println(eb.getObjectCount());
//		IndexCreation pr = new IndexCreation(eb);
//		pr.buildIndex();
////		pr.wi.printIndex();
//		SketchMining sm = new SketchMining(pr.wi);
//		for(Integer id : sm.getAvailIDs()) {
//			System.out.println(sm.getSketch(id));
//		}
//	}
}
