package online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.OnlineRankResult;
import datastructures.OnlineSketch;
import datastructures.WindowIndex;

import conf.Comp;


public class OnlineSketchStreamer implements Streamer {
	//private parameters
	private int total_pruned;
	private int total_inserted;
	private int total_updated;
	
	private WindowIndex wi;
	private HashMap<Integer, OnlineSketch> sketches;
	private HashMap<Integer, PriorityQueue<Event>> OSS;
	private HashMap<Integer, Integer> ssums;
	private EventsBag eb;
	public OnlineSketchStreamer() {
		sketches = new  HashMap<>();
		wi = new WindowIndex(128);
		OSS = new HashMap<>();
		ssums = new HashMap<>();
		eb = new EventsBag();
		total_pruned = 0;
		total_inserted = 0;
		total_updated = 0;
	}
	
	
	public void query(Event e) {
		total_inserted++;
//		eb.insertEvent(e);
		eb.insertEventWithoutTS(e);
		int id = e.getID();
		if(!OSS.containsKey(id)) {
			OSS.put(id, new PriorityQueue<Event>(conf.Constants.S, Comp.EVENT_VAL_SMALL_COMP));
			ssums.put(id, 0);
			sketches.put(id, new OnlineSketch(id));
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
			n.setRank(r.total_count);
//			System.out.println("Ranked:" + r+"\t"+n);
			if(r.total_count != 0) {
				sketches.get(id).insertNews(n);
//				System.out.println("Before Updated:\t" +r);
				for(Integer key : r.betterNews.keySet()) {
					ArrayList<News> toBeUpdated= r.betterNews.get(key);
					for(News passive : toBeUpdated) {
						passive.setRank(passive.getRank() -1);
					}
//					sketches.get(key).updateNews(toBeUpdated);
				}
//				total_updated += r.total_count;
//				System.out.println("After Updated:\t" +r);
			}
			//estimate the future maximum, in order to early stop
			int fm = n.getVal();
			int fo = ssums.get(id);
			int maxfuture = (fm*i + (ts-i)*fo)/(i+1);
			int current_size = wi.getUsedWindowLength();
			int bound = 0;
			if(current_size >=ts) {
				bound = wi.getMinBeta(i, ts);
				if(bound > maxfuture) {
					total_pruned++;
					break;
				}
			} else {
				if(i < current_size) {
					bound = wi.getMinBeta(i+1, current_size);
					if(bound > maxfuture) {
						i = current_size;
						total_pruned++;
					}
				}
			}
		}
	}
	
	public double getTotalQuality() {
		double result = 0;
		for(Entry<Integer, OnlineSketch> entry : sketches.entrySet()) {
			result += entry.getValue().getQuality();
		}
		return result;
	}
	
	public WindowIndex getWindowIndex() {
		return wi;
	}
	
	public void printStats() {
		System.out.printf("Inserted: %d\tPruned: %d\t Updated: %d\n", total_inserted, total_pruned, total_updated);
	}
	
	public HashMap<Integer, OnlineSketch> getSketches() {
		return sketches;
	}


	@Override
	public double getQuality() {
		double res = 0.0;
		for(Entry<Integer, OnlineSketch> entry : sketches.entrySet()) {
			res += entry.getValue().getQuality();
		}
		return res;
	}
}
