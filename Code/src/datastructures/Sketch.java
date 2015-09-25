package datastructures;

import java.util.ArrayList;
import java.util.HashSet;

import model.Evaluator;

/**
 * sketch maintains the news for an object, the news should have "good" ranks
 * the final sketch is
 * 
 * the recompute feature is disabled
 * 
 * @author a0048267
 * 
 */
public class Sketch {
	private static int num_of_sketches = conf.Constants.K;
	private int id;
	private HashSet<News> moments;
	private ArrayList<News> sketch;
	private Evaluator eval;

	public Sketch(int id) {
		this.id = id;
		moments = new HashSet<>();
		sketch = new ArrayList<News>();
		eval = new Evaluator();
	}

	public void addNews(News n) {
		moments.add(n);
	}
	
	public void update(News n) {
		if(!moments.contains(n)) {
			moments.add(n);
		}
		computeSketch();
	}
	

	public void updateBatch(ArrayList<News> arrayList) {
		for(News n : arrayList) {
			if(!moments.contains(n)) {
				moments.add(n);
			}
		}
		computeSketch();
	}
	
	public void insert(News n) {
		moments.add(n);
		computeSketch();
	}

	// compute the top-K sketches using submodular function
	// greedily find the k sketch
	public void computeSketch() {
		// sketch.clear();
		while (!sketch.isEmpty()) {
			moments.add(sketch.remove(sketch.size() - 1));
		}
		eval.clear();
		for (int i = 0; i < num_of_sketches; i++) {
			News tobeadd = null;
			double maximum_gain = -1;
			for (News moment : moments) {
				double delta = eval.dipAdd(moment);
				if (delta > maximum_gain) {
					maximum_gain = delta;
					tobeadd = moment;
				}
			}
			if (tobeadd == null) {
				break;
			} else {
				moments.remove(tobeadd);
				sketch.add(tobeadd);
				eval.addNews(tobeadd);
			}
		}
	}

	public double getScore() {
		return eval.computeValue();
	}

	public ArrayList<News> getSketch() {
		return sketch;
	}

	public int getID() {
		return id;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Sketch kobe = new Sketch(24);
		News[] news = new News[] { new News(24, 2, 5, 12, 17),
				new News(24, 3, 8, 12, 18), new News(24, 4, 6, 12, 19),
				new News(24, 7, 10, 12, 17), new News(24, 2, 9, 12, 20),
				new News(24, 3, 7, 12, 21), };
		kobe.addNews(news[0]);
		kobe.addNews(news[1]);
		kobe.addNews(news[2]);
		kobe.addNews(news[3]);
		kobe.addNews(news[4]);
		kobe.addNews(news[5]);
		kobe.computeSketch();
		System.out.println(kobe.getSketch() + "\t" + kobe.getScore());
		news[4].updateRank(-1);
	}

}
