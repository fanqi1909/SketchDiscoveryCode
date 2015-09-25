package io;

import java.util.ArrayList;
import java.util.Random;

import datastructures.Event;


public class EventsGenerator {

	private static Random seed = new Random(System.currentTimeMillis());
	/**
	 * @param id, object id 
	 * @param num_of_events 
	 * @return
	 */
	public static ArrayList<Event> genEventsFor(int id, int num_of_events) {
		ArrayList<Event> events = new ArrayList<>();
		int ts = 0;
		for(int i = 0; i < num_of_events; i++) {
			events.add(new Event(id, ts++, seed.nextInt(100)));
		}
		return events;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(genEventsFor(13,80));
	}
}
