package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import datastructures.Event;
import conf.Comp;


/**
 * compute the window entry J_i(*,*) for each subject $i$
 * 
 * @author a0048267
 * 
 */
public class SubjectComputation {
	private ArrayList<Event> player_history;
	// private ArrayList<ArrayList<Event>> window_info;
	// private PriorityQueue<Event>[] pqw;
	private PriorityQueue<Event> window_q; // for only current window size
	private ArrayList<Event> window_s; // for only current window size
	private ArrayList<Event> top1s; // for each window size
	int max_depth;
	int total_events;
	int id;
	CumulativeSum G;
	int entry_accessed = 0;
	private int current_window;
	Comparator<Event> event_max_cmp = Comp.EVENT_VAL_BIG_COMP;

	public SubjectComputation(ArrayList<Event> input_history) {
		id = input_history.get(0).getID();
		max_depth = input_history.size();
		total_events = input_history.size();
		player_history = new ArrayList<>();
		G = new CumulativeSum(input_history);
		top1s = new ArrayList<>();
		// pqw = new PriorityQueue[max_depth];
		for (int i = 0; i < max_depth; i++) {
			Event e = input_history.get(i);
			player_history.add(e);
		}
		// window ranking for window size 1 is ready to compute
		// ArrayList<Event> win0 = new ArrayList<>();
		window_q = new PriorityQueue<Event>(10000, event_max_cmp);
		for (Event e : player_history) {
			// win0.add(e.clone());
			Event wrapper = new Event();
			wrapper.setID(e.getID());
			wrapper.setTS(e.getTS());
			wrapper.setVal(e.getVal()*1000);
			window_q.add(wrapper);
		}

		window_s = new ArrayList<>();
		Event top = window_q.peek().clone();
		top.setVal(top.getVal());
		top1s.add(top);
		current_window = 0;
	}

	public Event eval(int w, int k) {
//		System.out.println(w+"\t"+k+"\t"+ current_window);
		if (w == current_window + 1) {
			// clear window_q and window_s
			window_s.clear();
			window_q.clear();
			current_window++;
			for (int i = current_window; i < max_depth; i++) {
				window_q.add(new Event(id, i, G.getWindowAggregate(i, w) * 1000
						/ (w + 1)));
			}
			top1s.add(window_q.peek());
//			System.out.println(top1s);
		}

		if (w < current_window) {
			if (k == 0) {
				return top1s.get(w);
			} else {
				return null;
			}
		} else if (w == current_window) {
			while (window_s.size() <= k) {
				window_s.add(window_q.poll());
			}
			return window_s.get(k);
		} else {
			return null;
		}
		//
		// if( k ==0) {
		// if(top1s.size() <= w) {
		// System.out.println("here?");
		// return null;
		// } else {
		// return top1s.get(w);
		// }
		// }
		// if (w != current_window && w != current_window + 1) {
		// return null;
		// }
		// // we are querying a new window size
		// if (w == current_window + 1) {
		//
		// }
		//
		// while (window_s.size() <= k) {
		// window_s.add(window_q.poll());
		// }
		// return window_s.get(k);
	}

	public void resetEntry() {
		entry_accessed = 0;
	}

	public int getEntry() {
		return entry_accessed;
	}

	public static void main(String[] args) {
		// testing for the correctness of incremental computing
		ArrayList<Event> input_history = new ArrayList<Event>();
		input_history.add(new Event(1, 0, 28));
		input_history.add(new Event(1, 1, 20));
		input_history.add(new Event(1, 2, 25));
		input_history.add(new Event(1, 3, 30));
		input_history.add(new Event(1, 4, 22));
		input_history.add(new Event(1, 5, 21));
		input_history.add(new Event(1, 6, 27));
		input_history.add(new Event(1, 7, 24));
		input_history.add(new Event(1, 8, 23));
		SubjectComputation ic = new SubjectComputation(input_history);
		System.out.println(ic.eval(0, 0));
		System.out.println(ic.eval(0, 1));
		System.out.println(ic.eval(0, 2));
		System.out.println(ic.eval(0, 3));
		System.out.println(ic.eval(0, 4));
		System.out.println(ic.eval(0, 5));
		System.out.println(ic.eval(0, 6));
		System.out.println(ic.eval(0, 7));
		System.out.println(ic.eval(0, 8));
		System.out.println(ic.eval(1, 0));
		System.out.println(ic.eval(1, 1));
		System.out.println(ic.eval(1, 2));
		System.out.println(ic.eval(1, 3));
		System.out.println(ic.eval(1, 4));
		System.out.println(ic.eval(1, 5));
		System.out.println(ic.eval(1, 6));
		System.out.println(ic.eval(1, 7));
		System.out.println(ic.eval(2, 0));
		System.out.println(ic.eval(2, 1));
		System.out.println(ic.eval(2, 2));
		System.out.println(ic.eval(2, 3));
		System.out.println(ic.eval(2, 4));
		System.out.println(ic.eval(2, 5));
		System.out.println(ic.eval(2, 6));
		System.out.println(ic.eval(3, 0));
		System.out.println(ic.eval(3, 1));
		System.out.println(ic.eval(3, 2));
		System.out.println(ic.eval(3, 3));
		System.out.println(ic.eval(3, 4));
		System.out.println(ic.eval(3, 5));
		System.out.println(ic.eval(4, 0));
		System.out.println(ic.eval(4, 1));
		System.out.println(ic.eval(4, 2));
		System.out.println(ic.eval(4, 3));
		System.out.println(ic.eval(4, 4));
		System.out.println(ic.eval(5, 0));
		System.out.println(ic.eval(5, 1));
		System.out.println(ic.eval(5, 2));
		System.out.println(ic.eval(5, 3));
		System.out.println(ic.eval(6, 0));
		System.out.println(ic.eval(6, 1));
		System.out.println(ic.eval(6, 2));
		System.out.println(ic.eval(7, 0));
		System.out.println(ic.eval(7, 1));
		System.out.println(ic.eval(8, 0));
		System.out.println(ic.entry_accessed);
	}

}
