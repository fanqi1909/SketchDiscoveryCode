package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import datastructures.Event;

public class SampleDataGen {

	private static Random seed = new Random(System.currentTimeMillis());

	/**
	 * generate k events for m subject randomly;
	 * 
	 * @param k
	 *            : number of events per subject
	 * @param m
	 *            : number of subjects
	 * @return
	 */
	public static ArrayList<Event>[] genEventsList(int k, int m) {
		int[] timestamp = new int[m];
		@SuppressWarnings("unchecked")
		ArrayList<Event>[] news = new ArrayList[m];
		for (int i = 0; i < m; i++) {
			news[i] = new ArrayList<>();
			for (int j = 0; j < k; j++) {
				int score = seed.nextInt(35);
				news[i].add(new Event(i, ++timestamp[i], score));
			}
		}
		return news;
	}

	/**
	 * generate a sampled file from raw data,
	 * 
	 * @param filename
	 * @param num_of_samples
	 *            , the num of samples from each subject
	 */
	public static void GenSampleFile(String filename, int num_of_samples) {
		FileReader fr;
		BufferedReader br;
		FileWriter fw;
		BufferedWriter bw;
		HashMap<Integer, ArrayList<Integer>> idVal = new HashMap<>(); 
		Queue<Integer> ids = new LinkedList<>();
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			fw = new FileWriter(filename.substring(0, filename.length() - 4)
					+ "-" + num_of_samples + "-shuffle.txt");
			bw = new BufferedWriter(fw);
			String line = null;
			String [] pars;
			while((line = br.readLine()) != null) {
				pars = line.split("\t");
				int id = Integer.parseInt(pars[0]);
				int val = Integer.parseInt(pars[1]);
				if(!idVal.containsKey(id)) {
					idVal.put(id, new ArrayList<Integer>());
					ids.add(id);
				}
				idVal.get(id).add(val);
			}
			//write
			while(!ids.isEmpty()) {
				int id = ids.poll();
				if(idVal.get(id).isEmpty()) {
					continue;
				}
				int val = idVal.get(id).remove(idVal.get(id).size()-1);
				bw.write(id+"\t"+val+"\n");
				ids.add(id);
			}
			
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenSampleFile("nba-fullquery.txt", 10000);
	}

}
