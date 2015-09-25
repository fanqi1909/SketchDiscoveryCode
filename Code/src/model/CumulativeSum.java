package model;

import java.util.ArrayList;

import datastructures.Event;

/**
 * a data structure stores the cumulative sum from
 * sequence 1 of an object
 * it is has an building time of O(n)
 * and query time of O(1)
 * @author a0048267
 */

public class CumulativeSum {
	private ArrayList<Integer> sums;
	public CumulativeSum(ArrayList<Event> history) {
		int len = history.size();
		sums = new ArrayList<>(len);
		sums.add(0);
		for(int i = 1; i < len+1; i++) {
			sums.add(sums.get(i-1) + history.get(i-1).getVal());
		}
	}
	public int getWindowAggregate(int ts, int w_size) {
		//compute bound
//		if(ts - w_size == -1) {
//			return sums.get(ts + 1);
//		} else {
//			return sums.get(ts + 1) - sums.get(ts - w_size);	
//		}
		int win_len = w_size + 1;
		return sums.get(ts + 1) - sums.get(ts +1 - win_len);
		
	}
	
	//notice that w_size is the window index,
	//which means the window length = w_siz + 1
	public boolean isValid(int ts, int w_size) {
		if(ts < 0 || w_size < 0) {
			return false;
		} else if (ts >= sums.size() - 1) {
			return false;
		} else {
			ts++;
			return ts - w_size >= 0;
		}
	}
	
	public void updateWith(Event e) {
		int len = sums.size();
		sums.add(sums.get(len-1) + e.getVal());
	}
	public static void main(String[] args) {
		ArrayList<Event> input_history = new ArrayList<Event>();
		input_history.add(new Event(1, 28, 0));
		input_history.add(new Event(1, 20, 1));
		input_history.add(new Event(1, 25, 2));
		input_history.add(new Event(1, 30, 3));
		input_history.add(new Event(1, 22, 4));
		input_history.add(new Event(1, 21, 5));
		input_history.add(new Event(1, 27, 6));
		input_history.add(new Event(1, 24, 7));
		input_history.add(new Event(1, 23, 8));
		CumulativeSum cs = new CumulativeSum(input_history);
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j <= i; j++) {
				System.out.print(cs.getWindowAggregate(i, j) +"\t");
			}
			System.out.println();
		}
	}
	public int getSize() {
		return sums.size() -1;
	}
}	
