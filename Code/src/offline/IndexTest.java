package offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;

import datastructures.Event;
import datastructures.EventsBag;


/**
 * compare and contrast with different Index methods
 * 
 * @author a0048267
 * 
 */
public class IndexTest {

	public Class<?> myIndex;

	public IndexTest(Class<?> index) {
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
			int[] queries = new int[] {80000,100000, 120000, 140000, 160000,180000};
			int[] Ps = new int[]{120};
			for(int num_of_queries : queries) {
				for(int P : Ps) {
					System.out.println(num_of_queries + "\t" + P);
					conf.Constants.P = P;
					String file_name ="nba-fullquery.txt";
					IndexTest it2 = new IndexTest(BruteIndexCreation.class);
					it2.runTest(file_name, num_of_queries);
					IndexTest it4 = new IndexTest(IndexCurrentPrune.class);
					it4.runTest(file_name, num_of_queries);
					IndexTest it3 = new IndexTest(IndexFuturePrune.class);
					it3.runTest(file_name, num_of_queries);
					IndexTest it = new IndexTest(IndexCreation.class);
					it.runTest(file_name, num_of_queries);
				}
			}
	}
}
