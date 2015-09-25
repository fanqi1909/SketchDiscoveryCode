package exp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import online.OnlineSketchStreamer;
import online.OnlineSketchStreamerWithoutPrune;
import online.SketchStreamer;
import online.SketchStreamerWithoutPrune;
import online.Streamer;
import online.StreamingTest;
import datastructures.Event;

public class OnlineTest {

	private Streamer s;

	public void setStreamer(Class<?> streamer) {
		// long timer = 0;
		try {
			s = (Streamer) streamer.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
		System.out.printf("%s:\t%dms\t%5.2fk/s\t%5.2f\n", s.getClass().getName(),
				time_taken, count / (double) time_taken, s.getQuality());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 5) {
			printHelper();
		} else {
			String query_file = args[0];
			int query = Integer.parseInt(args[1]);
			int P = Integer.parseInt(args[2]);
			int K = Integer.parseInt(args[3]);
			double Alpha = Double.parseDouble(args[4]);
			int t = Integer.parseInt(args[5]);
			int S = 4;
			if(args.length >= 7) {
				S = Integer.parseInt(args[6]);
			}
			
			//set conf
			conf.Constants.Alpha = Alpha;
			conf.Constants.P = P;
			conf.Constants.K = K;
			conf.Constants.S = S;
			Class<?>[] streamers = new Class<?>[]{SketchStreamerWithoutPrune.class,
					SketchStreamer.class, OnlineSketchStreamerWithoutPrune.class,
					OnlineSketchStreamer.class};
			
			StreamingTest st = new StreamingTest();
			st.setStreamer(streamers[t]);
			st.performanceTest(query_file, query);
		}
	}
	
	private static void printHelper() {
		System.err.println("[USAGE]: OnlineTest filename 100000 50 10 0.5 2");
	}
}
