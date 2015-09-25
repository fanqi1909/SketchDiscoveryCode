package online;

import java.util.HashMap;
import java.util.PriorityQueue;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.OnlineRankResult;
import datastructures.OnlineSketch;
import datastructures.WindowIndex;
import conf.Comp;


public class OnlineIndexWithoutPrune implements Streamer{

	private int total_pruned;
	private int total_inserted;
	private WindowIndex wi;
	private EventsBag eb;
	private HashMap<Integer, PriorityQueue<Event>> OSS;
	private HashMap<Integer, Integer> ssums;
	public OnlineIndexWithoutPrune() {
		wi = new WindowIndex(128);
		OSS = new HashMap<>();
		ssums = new HashMap<>();
		eb = new EventsBag();
		total_pruned = 0;
		total_inserted = 0;
	}
	
	@Override
	public void query(Event e) {
		total_inserted++;
//		eb.insertEvent(e);
		eb.insertEventWithoutTS(e);
		int id = e.getID();
		if(!OSS.containsKey(id)) {
			OSS.put(id, new PriorityQueue<Event>(conf.Constants.S, Comp.EVENT_VAL_SMALL_COMP));
			ssums.put(id, 0);
		}
		OSS.get(id).add(e);
		ssums.put(id, ssums.get(id) + e.getVal());
		if(OSS.get(id).size() >= conf.Constants.S) {
			ssums.put(id, ssums.get(id) -OSS.get(id).poll().getVal());
		}
		//for each query, generate its extensions
		//derive all news from e
		int ts = e.getTS();
		for(int i = 0; i < ts; i++) {
			News n = eb.genNewsFrom(id, ts, i+1);
			n.setVal(n.getVal()*1000/(i+1)); 
			wi.insert(n);
			OnlineRankResult r = wi.getRankOnline(n);
//			n.setRank(conf.Constants.P - r.total_count);
			n.setRank(r.total_count);
			int fm = n.getVal();
			int fo = ssums.get(id);
			int maxfuture = (fm*i + (ts-i)*fo)/(i+1);
			int current_size = wi.getUsedWindowLength();
			int bound = 0;
			if(current_size >=ts) {
				bound = wi.getMinBeta(i, ts);
				if(bound > maxfuture) {
//					total_pruned++;
//					break;
				}
			} else {
				if(i < current_size) {
					bound = wi.getMinBeta(i+1, current_size);
					if(bound > maxfuture) {
//						i = current_size;
//						total_pruned++;
					}
				}
			}
		}
	}
	
	@Override
	public WindowIndex getWindowIndex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printStats() {
		System.out.printf("Inserted: %d\tPruned: %d\n", total_inserted, total_pruned);
	}

	@Override
	public HashMap<Integer, OnlineSketch> getSketches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getQuality() {
		// TODO Auto-generated method stub
		return 0;
	}

}
