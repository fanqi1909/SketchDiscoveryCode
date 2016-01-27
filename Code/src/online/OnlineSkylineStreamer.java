package online;

import java.util.HashMap;
import java.util.HashSet;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.OnlineRankResult;
import datastructures.WindowIndex;

public class OnlineSkylineStreamer implements Streamer {
	private WindowIndex wi;
	private HashMap<Integer, HashSet<News>> skylines;
	private EventsBag eb;

	public OnlineSkylineStreamer() {
		skylines = new HashMap<>();
		wi = new WindowIndex(128);
		eb = new EventsBag();
	}

	@Override
	public void query(Event e) {
		eb.insertEventWithoutTS(e);
		int id = e.getID();
		if (!skylines.containsKey(id)) {
			skylines.put(id, new HashSet<News>());
		}
		// for each query, generate its extensions
		// derive all news from e
		int ts = e.getTS();
		for (int i = 0; i < ts; i++) {
			News n = eb.genNewsFrom(id, ts, i + 1);
			n.setVal(n.getVal() * 1000 / (i + 1));
			wi.insert(n);
			OnlineRankResult r = wi.getRankOnline(n);
			n.setRank(r.total_count);
			if (r.total_count != 0) {
				// compute the Skyline
				HashSet<News> ex_skys = skylines.get(id);
				boolean dominant = false;
				HashSet<News> toberemoved = new HashSet<>();
				for (News n2 : ex_skys) {
					if (n2.getWin() > n.getWin() && n2.getVal() > n.getVal()) {
						dominant = true;
						break;
					}
					if (n2.getWin() < n.getWin() && n2.getVal() < n.getVal()) {
						toberemoved.add(n2);
					}
				}
				if (!dominant) {
					ex_skys.add(n);
				}
				ex_skys.remove(toberemoved);
				// update affecting news
				for (Integer key : r.betterNews.keySet()) {
					for (News passive : r.betterNews.get(key)) {
						passive.setRank(passive.getRank() - 1);
					}
				}
			}
		}
	}

	@Override
	public WindowIndex getWindowIndex() {
		return null;
	}

	@Override
	public void printStats() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public HashMap getSketches() {
		return skylines;
	}

	public HashMap<Integer, HashSet<News>> getSketchSet() {
		return skylines;
	}

	@Override
	public double getQuality() {
		return 0;
	}

}
