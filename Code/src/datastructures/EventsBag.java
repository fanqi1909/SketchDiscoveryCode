package datastructures;

import io.SampleDataGen;

import java.util.ArrayList;

/**
 * is the global events store contains all the events for each object updated
 * when a single query is inserted
 * 
 * @author a0048267
 */
public class EventsBag {
	private ArrayList<ArrayList<Event>> bag;
	private int n_of_objects;
	private int totalevents;
	private ArrayList<ArrayList<Event>> accumulator; // for each event, keep the
														// accumulator for fast
														// compute aggregate
	private int longest_window;
	/**
	 * create an empty EventsBag
	 */
	public EventsBag() {
		bag = new ArrayList<>();
		n_of_objects = 0;
		totalevents = 0;
		accumulator = new ArrayList<>();
		longest_window = 0;
	}

	/**
	 * insert the event into the event bag, notice that we do not perform
	 * duplicate testing, as we assume the events comes in ascending order of
	 * time sequence we also assumes the objects numbers are consecutive, i.e.,
	 * if we receive an event of id $d$, the events of id $d-1, d-2,...,1$ must
	 * exists
	 * 
	 * @param e the event to be inserted
	 */
	public void insertEvent(Event e) {
		int id = e.getID();
		// expand the bag when necessary
		while (n_of_objects <= id) {
			bag.add(new ArrayList<Event>());
			ArrayList<Event> init = new ArrayList<>();
			init.add(new Event(id, -1, 0));
			accumulator.add(init);
			n_of_objects++;
		}
		bag.get(id).add(e);
		if(longest_window < bag.get(id).size()) {
			longest_window = bag.get(id).size();
		}
		ArrayList<Event> es = accumulator.get(id);
		es.add(new Event(e.getID(), e.getTS(), e.getVal()
				+ es.get(es.size() - 1).getVal()));
		totalevents++;
//		System.out.println(es);
	}
	
	/**
	 * in case the event do not have time sequence, manually attach one
	 * @param e
	 * @return the time sequence attached to e
	 */
	public int insertEventWithoutTS(Event e) {
		int id = e.getID();
		while (n_of_objects <= id) {
			bag.add(new ArrayList<Event>());
			ArrayList<Event> init = new ArrayList<>();
			init.add(new Event(id, -1, 0));
			accumulator.add(init);
			n_of_objects++;
		}
		int ts = bag.get(id).size();
		e.setTS(ts);
		bag.get(id).add(e);
		if(longest_window < bag.get(id).size()) {
			longest_window = bag.get(id).size();
		}
		ArrayList<Event> es = accumulator.get(id);
		es.add(new Event(e.getID(), e.getTS(), e.getVal()
				+ es.get(es.size() - 1).getVal()));
		totalevents++;
		return ts;
	}
	
	
	

	/**
	 * win_size starts from 0, ts starts from 1 compute the aggregate event of
	 * an object's event ending at endint_ts, with length win_size
	 * 
	 * @param id
	 * @param ending_ts
	 * @param win_size
	 * @return
	 */
	public News genNewsFrom(Event e, int win_size) {
		return genNewsFrom(e.getID(), e.getTS(), win_size);
	}
	
	/**
	 * win_size starts from 0, ts starts from 1 compute the aggregate event of
	 * an object's event ending at endint_ts, with length win_size
	 * 
	 * @param id
	 * @param ending_ts
	 * @param win_size
	 * @return
	 */
	public News genNewsFrom(int id, int end_ts, int win_size) {
		ArrayList<Event> G = accumulator.get(id);
		int val = G.get(end_ts).getVal()
				- G.get(end_ts - win_size).getVal();
		return new News(id, end_ts, win_size-1, val, -1);
	}

	/**
	 * retrieve the specific event
	 * @param id
	 * @param sequence
	 * @return
	 */
	public Event getEvent(int id, int sequence) {
		if (id >= n_of_objects) {
			return null;
		} else {
			ArrayList<Event> events = bag.get(id);
			if (events.size() <= sequence) {
				return null;
			} else {
				return events.get(sequence);
			}
		}
	}

	public ArrayList<Event> getEvents(int id) {
		if (id >= n_of_objects) {
			return null;
		} else {
			return bag.get(id);
		}
	}

	public int getObjectCount() {
		return n_of_objects;
	}

	public static void main(String[] args) {
		// EventsBag eb = new EventsBag(SampleDataGen.genEventsList(20, 150));
		// System.out.println(eb.n_of_objects);
		// System.out.println(eb.getEvents(1));
		// System.out.println(eb.getEvent(1, 5));
		// System.out.println(eb.getAggregateEvent(1, 7,6));
		EventsBag eb = new EventsBag();
		for (ArrayList<Event> e : SampleDataGen.genEventsList(20, 150)) {
			for (Event e1 : e) {
				eb.insertEvent(e1);
			}
		}
		eb.printOnly(new int[]{0,3,4});
		System.out.println(eb.genNewsFrom(new Event(0, 9, 2) ,1));
	}
	
	public void print() {
		System.out.println("Objects:\t" + n_of_objects);
		System.out.println("Events:\t" + totalevents);
		for(int i = 0; i < n_of_objects; i ++) {
			System.out.println("[" + i+"]:\t" + bag.get(i));
		}
	}
	
	public void printOnly(int[] indexes) {
		for(int i = 0; i < indexes.length; i ++) {
			System.out.println("[" + indexes[i]+"]:\t" + bag.get(indexes[i]));
		}
	}

	public boolean contains(Event e) {
		int id = e.getID();
		if (bag.size() <= id) {
			return false;
		}
		ArrayList<Event> all = bag.get(id);
		return all.get(all.size() - 1).getTS() >= e.getTS();
	}

	public int getEventsCount() {
		return totalevents;
	}
	
	public int getMaxWindow() {
		return longest_window;
	}

	public void clear() {
		bag.clear();
		n_of_objects = 0;
		totalevents = 0;
		longest_window = 0;
		accumulator.clear();
	}
}
