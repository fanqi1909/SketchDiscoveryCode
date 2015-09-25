package datastructures;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineRankResult {
	public HashMap<Integer, ArrayList<News>> betterNews;
	public int total_count; // total_count = all sizes from betterNews;
	public OnlineRankResult(HashMap<Integer, ArrayList<News>> BetterNews, int count) {
		betterNews = BetterNews;
		total_count = count;
	}
}
