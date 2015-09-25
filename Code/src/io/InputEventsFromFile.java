package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import datastructures.Event;


public class InputEventsFromFile {
	
	public static ArrayList<Event>[] readingEvents(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			int num_of_object = Integer.parseInt(br.readLine());
			@SuppressWarnings("unchecked")
			ArrayList<Event>[] allEvents = new ArrayList[num_of_object];
			for(int i = 0; i < num_of_object; i++) {
				allEvents[i] = new ArrayList<>();
			}
			String line =null;
			String[] par;
			while((line = br.readLine()) != null) {
				par = line.split("\t");
				int id = Integer.parseInt(par[0]);
				int val = Integer.parseInt(par[1]);
				int ts = Integer.parseInt(par[2]);
				allEvents[id].add(new Event(id,ts,val));
			}
			br.close();
			return allEvents;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Event>[] events = readingEvents("smallEvents.txt");
		for(ArrayList<Event> event : events) {
			System.out.println(event);
		}
	}

}
