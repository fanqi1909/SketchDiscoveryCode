package algo;

import java.util.*;

import datastructures.News;


public class SubmodularOpt {
	private final static int  K = 3;
	
	/**
	 * a value oracle that evaluate a set of a news
	 * @param news
	 * @return
	 */
	public static int valueOracle(ArrayList<News> news) {
		//summation on ranks
		int ranks = 0;
		//summation on coverage
		BitSet bs  = new BitSet();
		for(News n : news) {
			ranks += n.getRank();
			bs.set(n.getTS(), n.getTS() + n.getWin(), true);
		}
		return bs.cardinality() - ranks;
	}

	/**
	 * given a set of news, find the maximum value with respect to a submodular
	 * function
	 * 
	 * @param news
	 *            , with id, ts, value, rank readily computed
	 * @return
	 */
	public static int maxOne(ArrayList<News> news) {
		if(news.size() < K) {
			return valueOracle(news);
		}
//		ArrayList<News> copy = (ArrayList<News>)news.clone();
		//sort the news based on the objective function
		ArrayList<News> selected = new ArrayList<>();
		for(int i = 0; i <  K; i++ ) {
			//select one of the best
			int max = -1;
			News mn = null;
			for(int j = 0; j < news.size(); j++) {
				News c = news.get(j);
				selected.add(c);
				int eval = valueOracle(selected);
				if(eval > max) {
					max = eval;
					mn = c;
				}
				selected.remove(selected.size()-1);
			}
			selected.add(mn);
		}
		System.out.println(selected);
		return valueOracle(selected);
	}

	/**  
	 * a branch and bound method for exact max
	 * 
	 * @param news
	 * @return
	 */
	public static int maxTwo(ArrayList<News> news) {

		return 0;
	}

	/**
	 * a greedy approximation with pruning access on F
	 * @param news
	 * @return
	 */ 
	public static int maxThree(ArrayList<News> news) {

		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<News> input = new ArrayList<News>();
		input.add(new News(0,0,2,1,3));
		input.add(new News(0,1,5,2,2));
		input.add(new News(0,4,4,3,4));
		input.add(new News(0,6,4,4,1));
		System.out.println(maxOne(input));
	}

}
