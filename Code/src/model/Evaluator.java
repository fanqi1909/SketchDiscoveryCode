package model;

import java.util.HashMap;

import datastructures.News;

public class Evaluator {
	private static double alpha = conf.Constants.Alpha;
	private static double beta = 1-alpha;
//	private HashSet<Integer> used;
	private HashMap<Integer, Integer> used_count;
	private int cur_rank;

	// enable progressive computation of submodular
	// function
	public Evaluator() {
//		used = new HashSet<Integer>();
		used_count = new HashMap<Integer, Integer>();
		cur_rank = 0;
	}

	public void clear() {
//		used.clear();
		used_count.clear();
		cur_rank = 0;
	}

	public void addNews(News n) {
		int ts = n.getTS();
		int win = n.getWin();
		int r = n.getRank();
		for (int i = ts; i < ts + win; i++) {
			if(used_count.containsKey(i)) {
				used_count.put(i,used_count.get(i)+ 1);
			}
		}
		cur_rank += r;
	}

	/**
	 * return the value if n is added do not really add n into the set
	 * 
	 * @param n
	 */
	public double dipAdd(News n) {
		int ts = n.getTS();
		int win = n.getWin();
		int r = n.getRank();
		// count how many ts are already in the used
		int overlap = 0;
		for (int i = ts; i < ts + win; i++) {
			if (used_count.containsKey(i)) {
				overlap++;
			}
		}
		int size = used_count.size()+ win - overlap;
		return alpha * size + beta * (cur_rank + r);
	}

	public double computeValue() {
		return alpha * used_count.size() + beta * cur_rank;
	}
	
	/**
	 * compute the updated score of replace n1 with n2
	 * i.e. remove n1 from current set and insert n2
	 * but in the end, n1 is kept
	 * @param n1
	 * @param n2
	 * @return
	 */
	public double replaceWith(News n1, News n2) {
		//rank part is easy to calculate
		//needs to calculate the set cover part
		
		//first remove n1
		int ts1 = n1.getTS();
		int win1 = n1.getWin();
		for(int i = ts1; i< ts1+ win1; i++) {
			if(used_count.containsKey(i)) {
				if(used_count.get(i) <= 1) {
					used_count.remove(i);
				} else {
					used_count.put(i, used_count.get(i)-1) ;
				}
			}
		}
		
		//then add n2;
		int ts2 = n2.getTS();
		int win2 = n2.getWin();
		for(int i = ts2; i < ts2+win2; i++) {
			if(!used_count.containsKey(i)) {
				used_count.put(i, 0);
			}
			used_count.put(i, used_count.get(i)+1);
		}
		
		int new_rank = cur_rank - n1.getRank() + n2.getRank();
		double rtn = alpha * used_count.size() + beta* new_rank;
		
		//revoke, i.e. remove n2, add n1
		for(int i = ts2; i < ts2+win2; i++) {
			if(used_count.containsKey(i)) {
				if(used_count.get(i) <= 1) {
					used_count.remove(i);
				} else {
					used_count.put(i, used_count.get(i) - 1);
				}
			}
		}
		
		for(int i = ts1; i < ts1 + win1; i++) {
			if(!used_count.containsKey(i)) {
				used_count.put(i, 0);
			}
			used_count.put(i, used_count.get(i) + 1);
		}
		
		return rtn;
	}
}
