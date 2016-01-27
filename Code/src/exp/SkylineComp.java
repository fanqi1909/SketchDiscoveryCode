package exp;

import java.io.BufferedReader;
import java.io.FileReader;

import datastructures.Event;
import datastructures.EventsBag;
import offline.BruteIndexCreation;
import offline.IndexCreation;
import offline.SketchMining;
import offline.SkylineMining;

public class SkylineComp {
	public void runTests(String file_name, int query_num) {
		FileReader fr;
		BufferedReader br;
		int count = 0;
		long timer = 0;
		long time_taken = 0;
		EventsBag eb = new EventsBag();
		try {
			fr = new FileReader(file_name);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] par = line.split("\t");
				int id = Integer.parseInt(par[0]);
				int val = Integer.parseInt(par[1]);
				int ts = Integer.parseInt(par[2]);
				Event e = new Event(id, ts, val);
				eb.insertEvent(e);
				count++;
				if (count >= query_num) {
					break;
				}
			}
			br.close();
			
			//start testing
			timer = System.currentTimeMillis();
			SketchMethod(eb);
			time_taken = System.currentTimeMillis() - timer;
			System.out.println("Sketch: " + (time_taken) + " ms");
			
			timer = System.currentTimeMillis();
			SkylineMethod(eb);
			time_taken = System.currentTimeMillis() - timer;
			System.out.println("Skyline: " + (time_taken) + " ms");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void SketchMethod(EventsBag eb) {
		IndexCreation ic = new IndexCreation(eb);
		ic.buildIndex();
		new SketchMining(ic.getWi());
		return;
	}

	public void SkylineMethod(EventsBag eb) {
		BruteIndexCreation ic = new BruteIndexCreation(eb);
		ic.buildIndex();
		new SkylineMining(ic.getIndex());
	}

	public static void main(String[] args) {
		if(args.length < 5) {
			printHelper();
		} else {
			String query_file = args[0];
			int query = Integer.parseInt(args[1]);
			int P = Integer.parseInt(args[2]);
			int K = Integer.parseInt(args[3]);
			double Alpha = Double.parseDouble(args[4]);
			//set conf
			conf.Constants.Alpha = Alpha;
			conf.Constants.P = P;
			conf.Constants.K = K; 
			System.out.println(query + "\t" + P + "\t" + K);
			SkylineComp sc = new SkylineComp();
			sc.runTests(query_file, query);
		}
	}

	private static void printHelper() {
		System.err.println("[USAGE]: exp.SkylineComp filename numofquery P K 0.5");
	}
}
