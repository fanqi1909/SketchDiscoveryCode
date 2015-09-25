package online;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import datastructures.Event;


public class StreamingTest {
	private Streamer s;

	public void setStreamer(Class<?> streamer) {
		// long timer = 0;
		try {
			// timer = System.currentTimeMillis();
			s = (Streamer) streamer.newInstance();
			// System.out.println("Initialize:\t" + (System.currentTimeMillis()
			// - timer) +"\tms");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void performanceTest(String queryFile) {
		FileReader fr;
		BufferedReader br;
		int count = 0;
		long timer = 0;
		long time_taken = 0;
		try {
			fr = new FileReader(queryFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] par = line.split("\t");
				int id = Integer.parseInt(par[0]);
				int val = Integer.parseInt(par[1]);
				int ts = Integer.parseInt(par[2]);
				Event e = new Event(id, ts, val);
				timer = System.currentTimeMillis();
				s.query(e);
				time_taken += System.currentTimeMillis() - timer;
				count++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// s.getWindowIndex().printIndex();
		s.printStats();
		System.out.println("Query Processed:\t" + count);
		System.out.println("Query Time:\t" + time_taken + "\tms");
		System.out.println("Query Throughput:\t" + count / (double) time_taken
				+ "\tk/s");
	}

	public void performanceTest(String queryFile, int query_num) {
		FileReader fr;
		BufferedReader br;
		int count = 0;
		long timer = 0;
		long time_taken = 0;
		try {
			fr = new FileReader(queryFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] par = line.split("\t");
				int id = Integer.parseInt(par[0]);
				int val = Integer.parseInt(par[1]);
				// int ts = Integer.parseInt( par[2]);
				Event e = new Event(id, -1, val);
				timer = System.currentTimeMillis();
				s.query(e);
				time_taken += System.currentTimeMillis() - timer;
				count++;
				if (count >= query_num) {
					break;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// s.getWindowIndex().printIndex();
		// s.printStats();
		// HashMap<Integer, OnlineSketch> sketches = s.getSketches();
		// for(Integer key : sketches.keySet()) {
		// System.out.println(key +"\t"+ sketches.get(key).getSketch());
		// }
		// System.out.println("Query Processed:\t" + count);
		// System.out.println("Query Time:\t"+time_taken+"\tms");
		// System.out.println("Throughput:\t" + count/(double)time_taken
		// +"\tk/s");
		System.out.printf("%s:\t%dms\t%5.2fk/s\t%5.2f\n", s.getClass().getName(),
				time_taken, count / (double) time_taken, s.getQuality());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String query_file = "nba-fullquery-10000-shuffle.txt";
		// int query_num = 1000;
		int[] query_nums = new int[] {100000};
		int[] Ps = new int[]{20};
		for(int P : Ps) {
			conf.Constants.P = P;
			for (int query_num : query_nums) {
				System.out.println(query_num+"\t"+conf.Constants.P);
//				StreamingTest st1 = new StreamingTest();
//				st1.setStreamer(SketchStreamerWithoutPrune.class);
//				st1.performanceTest(query_file, query_num);
//
//				StreamingTest st2 = new StreamingTest();
//				st2.setStreamer(SketchStreamer.class);
//				st2.performanceTest(query_file, query_num);
				//
//				StreamingTest st1 = new StreamingTest();
//				st1.setStreamer(OnlineIndex.class);
//				st1.performanceTest(query_file, query_num);
//
//				StreamingTest st2 = new StreamingTest();
//				st2.setStreamer(OnlineIndexWithoutPrune.class);
//				st2.performanceTest(query_file, query_num);
//				StreamingTest st3 = new StreamingTest();
//				st3.setStreamer(OnlineSketchStreamerWithoutPrune.class);
//				st3.performanceTest(query_file, query_num);
				//
				StreamingTest st4 = new StreamingTest();
				st4.setStreamer(OnlineSketchStreamer.class);
				st4.performanceTest(query_file, query_num);
			}
		}
	}
}
