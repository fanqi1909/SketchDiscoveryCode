package datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import conf.Comp;
import model.Evaluator;

/**
 * the online part of sketch computing, with loose approximation bounds
 * 
 * @author a0048267
 * 
 */
public class OnlineSketch {
	private static int num_of_sketches = conf.Constants.K;
	private int id;
	private ArrayList<News> sketch;
	// private MaxCoverEvaluator eval;
	private HashSet<News> s1; // stores the maximum k news that covers most
	private ArrayList<News> s2; // stores the maximum k news that ranked highest
	private TreeSet<News> remaining; // store the remaining news candidate
	private HashMap<News, Integer> mapping;
	private HashMap<News, Integer> mapping_remain;
	private Evaluator sketch_eval;

	public OnlineSketch(int id) {
		this.id = id;
		// moments = new HashSet<News>();
		sketch = new ArrayList<>();
		// eval = new MaxCoverEvaluator();
		sketch_eval = new Evaluator();
		s1 = new HashSet<>(); // s1 is unordered
		s2 = new ArrayList<>();
		remaining = new TreeSet<>(Comp.RANK_BIG_COMP);
		mapping_remain = new HashMap<>();
		mapping = new HashMap<>();
	}

	public void initialize(ArrayList<News> newsSet) {
		if (newsSet.size() < num_of_sketches) {
			// do not accept
			return;
		} else if (newsSet.size() == num_of_sketches) {
			// add all the set into sketch, s1 and s2
			for (News n : newsSet) {
				sketch.add(n);
				s1.add(n);
				s2.add(n);
			}
			return;
		} else {
			// find the best s1 and s2
			popS1(newsSet);
			popS2(newsSet);
			computeSketch(s1, s2);
		}
	}

	private void computeSketch(HashSet<News> s12, ArrayList<News> s22) {
		// greedily find the K sketches from 2K events
		// Evaluator eval = new Evaluator();
		sketch_eval.clear();
		sketch.clear();
		for (int i = 0; i < num_of_sketches; i++) {
			News max_n = null;
			double max_val = -1;
			for (News n : s12) {
				if (!sketch.contains(n)) {
					double val = sketch_eval.dipAdd(n);
					if (val > max_val) {
						max_val = val;
						max_n = n;
					}
				}
			}
			for (News n : s22) {
				if (!sketch.contains(n)) {
					double val = sketch_eval.dipAdd(n);
					if (val > max_val) {
						max_val = val;
						max_n = n;
					}
				}
			}
			if (max_n != null) {
				sketch_eval.addNews(max_n);
				sketch.add(max_n);
			} else {
				break;
			}
		}
//		System.out.println("Sketch:\t" + sketch);
	}

	public int getId() {
		return id;
	}

	// Maintain a tree set of newsSet, and populate s2
	private void popS2(ArrayList<News> newsSet) {
		@SuppressWarnings("unchecked")
		ArrayList<News> tmp = (ArrayList<News>) newsSet.clone();
		Collections.sort(tmp, Comp.RANK_SMALL_COMP); // such that bigger ranked
														// is at tail
		int i = 0;
		for (; i < num_of_sketches; i++) {
			News n = tmp.remove(tmp.size() - 1);
			s2.add(n);
			mapping.put(n, i);
		}
		int j = 0;
		while (!tmp.isEmpty()) {
			News n = tmp.remove(tmp.size() - 1);
			remaining.add(n);
			mapping_remain.put(n, j++);
		}
		System.out.println("s2:\t" + s2);
		System.out.println("remain:\t" + remaining);
	}

	private void popS1(ArrayList<News> newsSet) {
		// greedily find the best K set that covers most element
		Evaluator eval = new Evaluator();
		for (int i = 0; i < num_of_sketches; i++) {
			double max_val = -1;
			News max_n = null;
			for (News n : newsSet) {
				if (!s1.contains(n)) {
					double val = eval.dipAdd(n);
					if (val > max_val) {
						max_val = val;
						max_n = n;
					}
				}
			}
			s1.add(max_n);
			eval.addNews(max_n);
		}
		// ensure S1 is properly poped
		System.out.println("s1:\t" + s1);
	}
	
	/**
	 * batching passive update news, therefore no need to update each news individually
	 * toBeUpdated is inherently sorted by ascending order of rank
	 * @param toBeUpdated
	 */
	public void updateNews(ArrayList<News> toBeUpdated) {
		//reversely traverse toBeUpdate
		boolean updated = false;
		for(int i = toBeUpdated.size() -1; i >=0 ;i--) {
			News news = toBeUpdated.get(i);
			//check the position of news , if it is in remaining list,
			//then no need to recompute, otherwise recomputation is necessary
			if (mapping.get(news) == null) {
				// s2 needs not to be changed since the news is in the remaining
				// list
				news.setRank(news.getRank() + 1);
				remaining.remove(news);
				news.setRank(news.getRank() - 1);
				remaining.add(news);
			} else {
				if (s2.get(s2.size() - 1).getRank() <= news.getRank()) {
					//no need to update, since news is greater than lower bound
				} else {
					// find a better one to pop in;
					// since the search depth is not that large, binary search may
					// not help too much.
					News candi = remaining.ceiling(news);
					if (candi != null) {
						remaining.remove(candi);
						remaining.add(news);
						s2.remove(news);
						mapping.remove(news);
						s2.add(candi);
						mapping.put(candi, s2.size() - 1);
						updated = true;
					}
				}
			}
			if(updated) {
				computeSketch(s1, s2);
			}
		}
	}

	/**
	 * news's rank is decreased by 1, thus s1 and s2 needs to be updated, to
	 * maintain the appropriate bound, s2 need not to be updated
	 * 
	 * @param news
	 */
	public void updateNews(News news) {
		// the news's rank has been decreased by 1
		// Integer index = mapping.get(news);
		if (mapping.get(news) == null) {
			// s2 needs not to be changed since the news is in the remaining
			// list
			news.setRank(news.getRank() + 1);
			remaining.remove(news);
			news.setRank(news.getRank() - 1);
			remaining.add(news);
		} else {
			// s2 needs to be updated;
			// News last = s2.get(s2.size()- 1);
			if (s2.get(s2.size() - 1).getRank() <= news.getRank()) {
				// no need to change anything
			} else {
				// find a better one to pop in;
				// since the search depth is not that large, binary search may
				// not help too much.
				News candi = remaining.ceiling(news);
				if (candi == null) {
					// do nothing; since news is greater than all element in
					// remaining
				} else {
					remaining.remove(candi);
					remaining.add(news);
					s2.remove(news);
					mapping.remove(news);
					s2.add(candi);
					mapping.put(candi, s2.size() - 1);
					computeSketch(s1, s2);
				}
			}
		}
	}

	/**
	 * inserting news into the current SKetch
	 * 
	 * @param news
	 */
	public void insertNews(News news) {
//		System.out.println(news);
		if (sketch.isEmpty()) {
			s1.add(news);
			s2.add(news);
			computeSketch(s1, s2);
		} else {
			// update s2
			boolean s2changed = false;
			boolean s1changed = false;
			if (s2.size() < num_of_sketches) {
				s2.add(news);
			} else if (news.getRank() > s2.get(s2.size() - 1).getRank()) {
				// inserted into the s2;
				s2.add(news);
				Collections.sort(s2, Comp.RANK_BIG_COMP);
				remaining.add(s2.remove(s2.size()-1));
				s2changed = true;
			} else {
				// inserted into remaining
				remaining.add(news);
			}
			if (s1.size() < num_of_sketches) {
				s1.add(news);
			} else {
				// update s1
				News tmp = null;
				double max = -1;
				for (News n : s1) {
					double v = sketch_eval.replaceWith(n, news);
					if (v > max) {
						max = v;
						tmp = n;
					}
				}
				if (tmp != null) {
					// means there is a replacement
					s1.remove(tmp);
					s1.add(news);
					s1changed = true;
				}
			}
			if (s1changed || s2changed) {
				computeSketch(s1, s2);
			}
		}
	}

	public ArrayList<News> getSketch() {
		return sketch;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test initialization first
//		ArrayList<News> newsset = new ArrayList<>();
//		newsset.add(new News(24, 3, 3, 5, 5));
//		newsset.add(new News(24, 12, 4, 12, 6));
//		newsset.add(new News(24, 8, 5, 12, 4));
//		newsset.add(new News(24, 5, 4, 12, 3));
//		newsset.add(new News(24, 10, 5, 12, 3));
//		newsset.add(new News(24, 7, 7, 12, 2));
//		newsset.add(new News(24, 11, 3, 12, 1));
		int num_of_news =30000;
		ArrayList<News> newsset = io.RankedNewsGenerator.genRandomNews(num_of_news, 126);
//		 OnlineSketch os = new OnlineSketch(24);
//		 os.initialize(newsset);
//		 System.out.println(os.getSketch());
		
		long timer =0;
		long time_used = 0;
		OnlineSketch os2 = new OnlineSketch(24);
		for (News n : newsset) {
			timer = System.currentTimeMillis();
			os2.insertNews(n);
			time_used += System.currentTimeMillis() - timer;
		}
		System.out.printf("[Insertion] Queries:%d\tTime:%dms\tThroughput:%5.2fk/s\n", 
				num_of_news, time_used, num_of_news/(1.0*time_used));
		System.out.println(os2.getQuality());
//		System.out.println(os2.getSketch());
		
//		int num_of_updates = 100;
//		Random r = new Random(System.currentTimeMillis());
//		
//		timer = 0;
//		time_used = 0;
//		for(int i = 0; i < num_of_updates; i++) {
//			News picked = newsset.get(r.nextInt(num_of_news));
//			picked.setRank(picked.getRank() -1);
//			timer = System.currentTimeMillis();
//			os2.updateNews(picked);
//			time_used += System.currentTimeMillis() - timer;
//			System.out.println(os2.s2);
//			System.out.println(os2.s1);
//		}
//		System.out.printf("[Update] Updates:%d\tTime:%dms\tThroughput:%5.2fk/s\n", 
//				num_of_updates, time_used, num_of_updates/(1.0*time_used));
////		newsset.get(3); 
	}

	public double getQuality() {
		return sketch_eval.computeValue();
	}

}
