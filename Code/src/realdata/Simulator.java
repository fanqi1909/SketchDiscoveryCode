package realdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import online.OnlineSketchStreamer;
import online.OnlineSkylineStreamer;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;

public class Simulator {

	private static HashMap<Integer, String> id_names = new HashMap<>();
	private static HashMap<String, Integer> names_id = new HashMap<>();

	public static EventsBag readStockData(String query_file) {
		FileReader fr;
		BufferedReader br;
		EventsBag eb = new EventsBag();
		try {
			fr = new FileReader(query_file);
			br = new BufferedReader(fr);
			String line;
			int ID = 0;
			int ts = 0;
			while ((line = br.readLine()) != null) {
				ts = 0;
				String[] par = line.split(" ");
				String name = par[0];

				id_names.put(ID, name);
				names_id.put(name, ID);

				for (int i = 1; i < par.length; i++) {
					double val = Double.parseDouble(par[i]);
					Event e = new Event(ID, ts++, (int) val * 100);
					eb.insertEvent(e);
				}
				ID++;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eb;
	}

	public static void printNews(News n) {
		int id = n.getID();
		int rank = n.getRank();
		// int ts = n.getTS();
		int win = n.getWin();
		int val = n.getVal();
		System.out
				.printf("[ts] Stock %s has average price of %5.2f for the last %d days which ranks %d in the market!\n",
						id_names.get(id), val / 100000.0, win + 1, rank);

	}

	public static Collection<News> genNews(NewsGenerator ngins, int id) {
		return ngins.getNews(id);
	}

	public static void runTest(String query_file, int query, Class ng,
			int... ids) {
		FileReader fr;
		BufferedReader br;
		int count = 0;
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
				if (count >= query) {
					break;
				}
			}
			br.close();
			System.out.println(eb.getObjectCount());
			@SuppressWarnings("unchecked")
			NewsGenerator ngins = (NewsGenerator) ng.getDeclaredConstructor(
					EventsBag.class).newInstance(eb);
			for (int id : ids) {
				System.out.println(id + ":\t" + ngins.getNews(id).size() + "\t"
						+ ngins.getNews(id));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String in_file_name = args[0];
		int type = Integer.parseInt(args[1]);
		int P = Integer.parseInt(args[2]);
		int K = Integer.parseInt(args[3]);
		double alpha = Double.parseDouble(args[4]);
		conf.Constants.K = K;
		conf.Constants.P = P;
		conf.Constants.Alpha = alpha;

		EventsBag eb = readStockData(in_file_name);
		System.out.println(eb.getObjectCount() + "\t" + eb.getEventsCount()
				+ "\t" + eb.getMaxWindow());

		Class[] newsgens = new Class[] { SketchNewsGenerator.class,
				SkylineGenerator.class };

		NewsGenerator negins = (NewsGenerator) newsgens[type]
				.getDeclaredConstructor(EventsBag.class).newInstance(eb);

		System.out.println(negins.getAvailableIDs());
		for (int i : negins.getAvailableIDs()) {
			Collection<News> newss = genNews(negins, i);
			for (News n : newss) {
				printNews(n);
			}
		}
	}
}
