package online;

import java.util.HashMap;

import datastructures.Event;
import datastructures.OnlineSketch;
import datastructures.WindowIndex;


public interface Streamer {
	public void query(Event e);
	public WindowIndex getWindowIndex();
	public void printStats();
	public HashMap<Integer, OnlineSketch> getSketches();
	public double getQuality();
}
