package conf;

import java.util.Comparator;

import datastructures.Event;
import datastructures.News;

public class Comp {
	public static Comparator<News> VAL_SMALL_COMP = new Comparator<News>() {
		@Override
		public int compare(News o1, News o2) {
			return o1.getVal() - o2.getVal();
		}
	};
	
	public static Comparator<News> VAL_BIG_COMP = new Comparator<News>() {
		@Override
		public int compare(News o1, News o2) {
			return o2.getVal() - o1.getVal();
		}
	};
	
	public static Comparator<News> RANK_BIG_COMP = new Comparator<News>() {
		@Override
		public int compare(News o1, News o2) {
			return o2.getRank() - o1.getRank();
		}
	};
	
	public static Comparator<News> RANK_SMALL_COMP = new Comparator<News>() {
		@Override
		public int compare(News o1, News o2) {
			return o1.getRank() - o2.getRank();
		}
	};
	
	/**
	 * compare events based on time stamp, ordered by smallest first
	 */
	public static Comparator<Event> TS_SMALL_COMP = new Comparator<Event>() {
		@Override
		public int compare(Event o1, Event o2) {
			return o1.getTS() - o2.getTS();
		}
	};
	/**
	 * compare events based on time stamp, ordered by biggest first
	 */
	public static Comparator<Event> TS_BIG_COMP = new Comparator<Event>() {
		@Override
		public int compare(Event o1, Event o2) {
			return o2.getTS() - o1.getTS();
		}
	};
	/**
	 * compare events based on event value, ordered by smallest first
	 */
	public static Comparator<Event> EVENT_VAL_SMALL_COMP = new Comparator<Event>() {
		@Override
		public int compare(Event o1, Event o2) {
			return  o1.getVal() - o2.getVal();
		}
	};
	/**
	 * compare events based on event value, ordered by biggest first
	 */
	public static Comparator<Event> EVENT_VAL_BIG_COMP = new Comparator<Event>() {
		@Override
		public int compare(Event o1, Event o2) {
			return o2.getVal() - o1.getVal();
		}
	};
}
