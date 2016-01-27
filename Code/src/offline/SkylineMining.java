package offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import conf.Comp;

import datastructures.News;
import datastructures.WindowIndex;

/**
 * Used as an comparison
 * @author a0048267
 *
 */
public class SkylineMining {
	private final WindowIndex windex;
	private HashMap<Integer, ArrayList<News>> candidates;
	private HashMap<Integer, ArrayList<News>> results; //id -> skyline mapping
	private static final int P = conf.Constants.P;
	public SkylineMining(WindowIndex windowIndex) {
		this.windex = windowIndex;
		candidates = new HashMap<>();
		results = new HashMap<>();
		int total_win_size = windex.getCurrentWindowLength();
		for(int i = 0; i < total_win_size; i++) {
			TreeSet<News> index = windex.getIndexAt(i) ;
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
				if(!candidates.containsKey(id)) {
					candidates.put(id, new ArrayList<News>());
					results.put(id, new ArrayList<News>());
				}
				candidates.get(id).add(n);
			}
			// then for each candidate, find its skyline
			for(Map.Entry<Integer, ArrayList<News>> cand : candidates.entrySet()) {
				computeSkyline(cand.getValue(), results.get(cand.getKey()) );
			}
		}
	}
	/**
	 * compute skyline based on the given candidate set , write
	 * the result to results2
	 * @param candidate
	 * @param results2
	 */
	public void computeSkyline(ArrayList<News> candidate,
			ArrayList<News> results2) {
		//using line-sweeping to compute candidate
		//sort the candidate based on one dimension
		Collections.sort(candidate, Comp.VAL_BIG_COMP);
		//scan candidate for the for the length dimension
		results2.add(candidate.get(0));
		int min_len = candidate.get(0).getWin();
		for(int i = 1, size = candidate.size();  i < size; i++) {
			News next = candidate.get(i);
			if(next.getWin() <= min_len) {
				continue;
			} else {
				//it is a skyline point
				results2.add(next);
				min_len = next.getWin();
			}
		}
	}
	
	public ArrayList<News> getSkyline(int id) {
		if(results.containsKey(id)) {
			return results.get(id);
		}
		return null;
	}
	
	public Set<Integer> getAvailIDs() {
		return results.keySet();
	}
}
