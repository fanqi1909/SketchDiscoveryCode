package io;

import java.util.ArrayList;
import java.util.Random;

import datastructures.News;


public class RankedNewsGenerator {

	static Random r = new Random();

	public static ArrayList<News> genRandomNews(int num_of_news, int id) {
		ArrayList<News> res = new ArrayList<>();
		int max_events = num_of_news * 2;
		int max_length = 25;
		for (int i = 0; i < num_of_news; i++) {
			res.add(new News(id, r.nextInt(max_events), r.nextInt(max_length),
					r.nextInt(50), r.nextInt(num_of_news)));
		}
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(genRandomNews(15,24));
	}
}
