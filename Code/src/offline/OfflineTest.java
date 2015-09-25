package offline;

import java.io.BufferedReader;
import java.io.FileReader;

import datastructures.Event;
import datastructures.EventsBag;


public class OfflineTest {
	private EventsBag eb;
	private SketchMining sm; 
	private IndexCreation ic;
	public OfflineTest() {
		eb = new EventsBag();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OfflineTest ot = new OfflineTest();
		int[] num_of_queries = new int[]{100000,120000,140000,180000};
//		int[] num_of_queries = new int[]{120000};
		int[] Ks = new int[]{10,20,30,40};
//		int[] Ks = new int[]{20};
		int[] Ps = new int[]{100, 400};
		for(int P : Ps) {
			conf.Constants.P = P;
			for(int query : num_of_queries) {
				for(int K : Ks) {
					System.out.println(query + "\t" + K+"\t"+P);
					conf.Constants.K = K;
					ot.performanceTest("nba-fullquery.txt",query);
				}
			}
		}
	}
	
	public SketchMining getSM() {
		return sm;
	}

	private void performanceTest(String queryFile, int num_of_queries) {
		eb.clear();
		FileReader fr;
		BufferedReader br;
		int count = 0;
		long timer =0;
		long time_taken=0;
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
				eb.insertEvent(e);
				count++;
				if(count >= num_of_queries) {
					break;
				}
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		//at this step, eb has popped-up
		ic = new IndexCreation(eb);
//		timer = System.currentTimeMillis();
		ic.buildIndex();
//		time_taken += System.currentTimeMillis() - timer;
//		System.out.println("Indexing:\t"+time_taken+" ms\t"+(count/(1.0*time_taken)) +"K/s");
		time_taken = 0;
		timer = System.currentTimeMillis();
		sm = new SketchMining(ic.getWi());
		time_taken += System.currentTimeMillis() - timer;
		System.out.println("Mining:\t" + time_taken +" ms\t"+(count/(1.0*time_taken)) +"K/s");
//		System.out.println(sm.getAvailIDs());
	}
}
