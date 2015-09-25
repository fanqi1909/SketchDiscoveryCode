package model;

import java.util.HashSet;

import datastructures.News;

/**
 * TODO:: use intervals to boost the size computation
 * @author a0048267
 *
 */
public class MaxCoverEvaluator {

	private HashSet<Integer> used;

	// enable progressive computation of submodular
	// function
	public MaxCoverEvaluator() {
		used = new HashSet<Integer>();
	}

	public void clear() {
		used.clear();
	}

	public void addNews(News n) {
		int ts = n.getTS();
		int win = n.getWin();
		for (int i = ts; i < ts + win; i++) {
			used.add(i);
		}
	}

	/**
	 * return the value if n is added do not really add n into the set
	 * 
	 * @param n
	 */
	public double dipAdd(News n) {
		int ts = n.getTS();
		int win = n.getWin();
		// count how many ts are already in the used
		int overlap = 0;
		for (int i = ts - win; i <= ts; i++) {
			if (used.contains(i)) {
				overlap++;
			}
		}
		int size = used.size() + win - overlap;
		return size;
	}

	public double computeValue() {
		return used.size();
	}
}
