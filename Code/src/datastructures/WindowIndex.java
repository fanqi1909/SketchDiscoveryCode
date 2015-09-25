package datastructures;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import conf.Comp;
/**
 * window index is also for the streaming version, where news are inserted
 * sequentially, independently
 * 
 * @author a0048267
 * 
 */
public class WindowIndex {
	private int K = conf.Constants.P;
	private static Comparator<News> cmp = Comp.VAL_SMALL_COMP;
	private TreeSet<News>[] indexes;
	private int window_length;
	private MinSegmentTree betas;
	private int used_window_size;

	@SuppressWarnings("unchecked")
	public WindowIndex(int init_size) {
		window_length = init_size;
		used_window_size = 0;
		indexes = new TreeSet[window_length];
		betas = new MinSegmentTree();
		for (int i = 0; i < window_length; i++) {
			indexes[i] = new TreeSet<>(cmp);
			betas.append(-1);
		}
	}

	/**
	 * insert a news to the index of winsize
	 * @param e
	 * @param winsize
	 */
	@SuppressWarnings("unchecked")
	public boolean insert(News e) {
		int winsize = e.getWin();
		// do double when possible
		while (winsize >= indexes.length) {
			window_length *= 2;
			TreeSet<News>[] newindex = new TreeSet[window_length];
			System.arraycopy(indexes, 0, newindex, 0, indexes.length);
			for (int i = indexes.length; i < window_length; i++) {
				newindex[i] = new TreeSet<News>(cmp);
				betas.append(-1);
			}
			indexes = newindex;
		}
		TreeSet<News> indexw = indexes[winsize];
		if (e.getVal() < betas.getMinAt(winsize) && indexw.size() >= K) {
			return false;
		}
		if (indexw.size() >= K) {
			indexw.add(e);
			indexw.remove(indexw.first());
			betas.updateAt(winsize, indexw.first().getVal());
		} else {
			indexw.add(e);
			if(indexw.size() == K) {
				betas.updateAt(winsize, indexw.first().getVal());
			}
		}
		if(used_window_size < winsize) {
			used_window_size = winsize;
		}
		return true;
	}
	
	

	public int getBeta(int winsize) {
		return betas.getMinAt(winsize);
	}

	public void printIndex() {
		System.out.printf("current top-%d-index\n", K);
		for (int i = 0; i < used_window_size; i++) {
			System.out.printf("Index %3d, lower bound %3d:\t", i,betas.getMinAt(i));
			System.out.println(indexes[i]);
		}
	}
	
	public void printIndex(int max_len) {
		System.out.printf("current top-%d-index\n", K);
		for (int i = 0; i < max_len; i++) {
			System.out.printf("Index %3d, lower bound %3d:\t", i,betas.getMinAt(i));
			System.out.println(indexes[i]);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WindowIndex wi = new WindowIndex(3);
		wi.insert(new News(1, 0, 0, 4, -1));
		wi.insert(new News(2, 0, 1, 5, -1));
		wi.insert(new News(3, 0, 2, 6, -1));
		wi.insert(new News(4, 0, 3, 6, -1));
		wi.insert(new News(5, 0, 1, 3, -1));
		wi.insert(new News(6, 0, 2, 7, -1));
		wi.insert(new News(7, 0, 0, 3, -1));
		wi.insert(new News(8, 0, 1, 6, -1));
		wi.insert(new News(9, 0, 2, 8, -1));
		wi.insert(new News(10, 0, 4, 1, -1));
		wi.insert(new News(11, 0, 3, 4, -1));
		wi.insert(new News(12, 0, 5, 8, -1));
		wi.insert(new News(13, 0, 2, 1, -1));
		wi.insert(new News(14, 0, 1, 4, -1));
		wi.insert(new News(15, 0, 5, 7, -1));
		wi.insert(new News(16, 0, 3, 3, -1));
		wi.insert(new News(17, 0, 4, 2, -1));
		wi.insert(new News(18, 0, 5, 8, -1));
		wi.insert(new News(19, 0, 3, 9, -1));
		wi.insert(new News(20, 0, 4, 6, -1));
		wi.insert(new News(21, 0, 2, 5, -1));
		wi.insert(new News(22, 0, 3, 2, -1));
		wi.insert(new News(23, 0, 1, 7, -1));
		wi.insert(new News(24, 0, 4, 4, -1));
		wi.insert(new News(25, 0, 3, 3, -1));
		wi.insert(new News(26, 0, 5, 1, -1));
		wi.printIndex();
		News candidate = new News(0, 0, 3, 9, -1);
		System.out.println(wi.getRank(candidate));
	}


	public int getRank(News candidate) {
		int w = candidate.getWin();
		double v = candidate.getVal();
//		if(v < betas[w]) {
		if(v < betas.getMinAt(w)) {
			// v is too small,
			return -1; 
		}
		TreeSet<News> tree = indexes[w];
		int rank = 0;
		for (News e : tree) {
			if (e.getVal() < v) {
				rank++;
			} else {
				break;
			}
		}
		return tree.size() - rank;
	}
	
	
	/**
	 * 
	 * @param candidate
	 * @return OnlineRankResult, result is the clustered news to be passive update by ID, count 
	 * is the number of elements less than {@linkplain candidate}
	 */
	public OnlineRankResult getRankOnline(News candidate) {
		int w = candidate.getWin();
		int v = candidate.getVal();
		HashMap<Integer, ArrayList<News>> result = new HashMap<>();
		int count = 0; // retains the number of news less than candidate
		if(v  < betas.getMinAt(w)) {
			return new OnlineRankResult(result, 0);
		} else {
			TreeSet<News> tree = indexes[w];
			for(News e : tree) {
				if(e.getVal() < v) {
					count++;
					if(!result.containsKey(e.getID()) ) {
						result.put(e.getID(), new ArrayList<News>());
					}
					result.get(e.getID()).add(e);
				} else {
					break;
				}
			}
			return new OnlineRankResult(result, count);
		}
	}
	
//	public ArrayList<News> getRankOnline(News candidate) {
//		int w = candidate.getWin();
//		ArrayList<News> result = new ArrayList<>();
//		int v = candidate.getVal();
//		if(v  < betas.getMinAt(w)) {
//			return result;
//		} else {
//			TreeSet<News> tree = indexes[w];
//			for(News e : tree) {
//				if(e.getVal() < v) {
//					result.add(e);
//				} else {
//					break;
//				}
//			}
//			return result;
//		}
//	}

	public int getMinBeta(int w) {
//		//find the min value of betas after w;
		return betas.getMin(w, window_length-1);
	}
	
	public int getMinBeta(int l, int r) {
		return betas.getMin(l, r);
	}
	
	public int getCurrentWindowLength() {
		return window_length;
	}
	
	public int getUsedWindowLength() {
		return used_window_size;
	}
	
	public TreeSet<News> getIndexAt(int win_size) {
		return indexes[win_size];
	}
}
