package exp;

import java.io.BufferedReader;
import java.io.FileReader;

import datastructures.Event;
import datastructures.EventsBag;
import offline.IndexCreation;
import offline.SketchMining;

public class OfflineMining {
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
			//set conf
			conf.Constants.Alpha = Alpha;
			conf.Constants.P = P;
			conf.Constants.K = K;
			System.out.println(query + "\t" + P + "\t" + K);
			OfflineMining om = new OfflineMining();
			om.runTest(query_file, query);
		}
	}

	private void runTest(String query_file, int query) {
		FileReader fr;
		BufferedReader br;
		int count = 0;
		long timer =0;
		long time_taken=0;
		EventsBag eb = new EventsBag();
		try {
			fr = new FileReader(query_file);
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
				if(count >= query) {
					break;
				}
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		//at this step, eb has popped-up
		IndexCreation ic = new IndexCreation(eb);
//		timer = System.currentTimeMillis();
		ic.buildIndex();
//		System.out.println("Indexing:\t"+time_taken+" ms\t"+(count/(1.0*time_taken)) +"K/s");
		time_taken = 0;
		timer = System.currentTimeMillis();
		new SketchMining(ic.getWi());
		time_taken += System.currentTimeMillis() - timer;
		System.out.println("Mining:\t" + time_taken +" ms\t"+(count/(1.0*time_taken)) +"K/s");
	}

	private static void printHelper() {
		System.err.println("[USAGE]: OfflineMining filename 100000 50 10 0.5");
	}
}
