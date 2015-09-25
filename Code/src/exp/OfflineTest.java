package exp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;

import offline.BruteIndexCreation;
import offline.IndexCreation;
import offline.IndexCreator;
import offline.IndexCurrentPrune;
import offline.IndexFuturePrune;
import offline.IndexTest;
import datastructures.Event;
import datastructures.EventsBag;

public class OfflineTest {
	private Class<?> myIndex;

	public void setIndexAlgo(Class<?> index) {
		myIndex = index;
	}
	
	public void runTest(String file_name, int query_num) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		// at this step, eb has popped-up
		IndexCreator ic;
		try {
			ic = (IndexCreator) myIndex.getDeclaredConstructor(EventsBag.class)
					.newInstance(eb);
			timer = System.currentTimeMillis();
			ic.buildIndex();
			time_taken += System.currentTimeMillis() - timer;
			System.out.println(myIndex.getName()+":\t" + time_taken + "ms\t"
					+ (count / (1.0 * time_taken)) + "K/s");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
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
			//set conf
			conf.Constants.Alpha = Alpha;
			conf.Constants.P = P;
			conf.Constants.K = K;
			System.out.println(query + "\t" + P + "\t" + K);
			IndexTest it2 = new IndexTest(BruteIndexCreation.class);
			it2.runTest(query_file, query);
			IndexTest it4 = new IndexTest(IndexCurrentPrune.class);
			it4.runTest(query_file, query);
			IndexTest it3 = new IndexTest(IndexFuturePrune.class);
			it3.runTest(query_file, query);
			IndexTest it = new IndexTest(IndexCreation.class);
			it.runTest(query_file, query);
		}
		
	}

	private static void printHelper() {
		System.err.println("[USAGE]: OfflineTest filename 100000 50 10 0.5");
	}
}
