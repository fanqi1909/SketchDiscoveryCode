package offline;

//import io.SampleDataGen;

import io.SampleDataGen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.WindowIndex;

import model.CumulativeSum;
import model.SubjectComputation;
import conf.Comp;

public class IndexCreation implements IndexCreator {
	private static final int K = conf.Constants.P;
	private final EventsBag histories;
	private WindowIndex wi;
	// beta(i) is for window_index(i)
	ArrayList<ArrayList<ArrayList<Event>>> J;
	ArrayList<CumulativeSum> GS;

	// maximum unseen windows;
	// ArrayList<Integer> Ms;
	// ArrayList<Integer> Um;
	ArrayList<SubjectComputation> FCS;
	PriorityQueue<SubjectState> subjectQueue;

	int max_window;
	int num_of_subject;

	public IndexCreation(EventsBag input_history) {
		// max_window = 0;
		// copy the input history to histories
		histories = input_history;
		max_window = histories.getMaxWindow();
		setWi(new WindowIndex(max_window));
		num_of_subject = histories.getObjectCount();
		// create J_i(0,*);
		J = new ArrayList<>();
		for (int i = 0; i < num_of_subject; i++) {
			ArrayList<ArrayList<Event>> jss = new ArrayList<>();
			ArrayList<Event> history = histories.getEvents(i);
			ArrayList<Event> j0s = new ArrayList<>();
			for (Event e : history) {
				j0s.add(e);
			}
			// sort j0s
			Collections.sort(j0s, Comp.EVENT_VAL_BIG_COMP);
			jss.add(j0s);
			J.add(jss);
		}
		// initialize G
		// initialize FC
		// initialize Ms
		// Ms = new ArrayList<>();
		// Um = new ArrayList<>();
		GS = new ArrayList<>();
		FCS = new ArrayList<>();
		subjectQueue = new PriorityQueue<>();
		for (int i = 0; i < num_of_subject; i++) {
			ArrayList<Event> history = histories.getEvents(i);
			GS.add(new CumulativeSum(history));
			FCS.add(new SubjectComputation(history));
			subjectQueue.add(new SubjectState(i,
					history.get(0).getVal() * 1000, 0,
					history.get(0).getVal() * 1000));
		}
//		buildIndex();
	}

	public void buildIndex() {
		// get player order
		// use static order first, later change to a dynamic order
		SubjectState ss;
		while (!subjectQueue.isEmpty()) {
			ss = subjectQueue.poll();
			// parse ss for further computation
			int id = ss.id;
			int depth = K;
			int w = ss.nextwin;
			int ms = ss.ms;
//			System.out.println("Object: " + id + "\t" + w + "\t" + ms + "\t"
//					+ wi.getMinBeta(w));
			if (ms < getWi().getBeta(w)) {
				// this object is pruned;
				continue;
			}
			if (w >= histories.getEvents(id).size()) {
				continue;
			}
			int um = ss.um;
			SubjectComputation FC = FCS.get(id);
			int k1 = w / 2, k2 = w - k1;
			if (FC.eval(k1, 0).getVal() + FC.eval(k2, 0).getVal() < getWi()
					.getBeta(w)) {
				depth = 1;
			}
			for (int e = 0; e < depth; e++) {
				Event next = FC.eval(w, e);
				if (next == null) {
					// means K is large for this window,
					// we break the loop
					break;
				}
				News wrapper = new News();
				wrapper.set(next, w);
				getWi().insert(wrapper);
			}
			// wi.printIndex();
			// update Ms
			Event wm = FC.eval(w, 0);
			if (wm.getVal() > um) {
				um = wm.getVal();
			}
			// max value occurs
			int max_unseen = wm.getVal() + um / (w + 1);
			ss.ms = max_unseen;
			ss.um = um;
			ss.nextwin++;
			subjectQueue.add(ss);
		}
	}

	public static void main(String[] args) {
		EventsBag eb = new EventsBag();
		for (ArrayList<Event> e : SampleDataGen.genEventsList(50, 7)) {
			for (Event e1 : e) {
				eb.insertEvent(e1);
			}
		}
		System.out.println(eb.getObjectCount());
		IndexCreation pr = new IndexCreation(eb);
		pr.getWi().printIndex();
	}

	@Override
	public WindowIndex getIndex() {
		return getWi();
	}

	public WindowIndex getWi() {
		return wi;
	}

	public void setWi(WindowIndex wi) {
		this.wi = wi;
	}
}

class SubjectState implements Comparable<SubjectState> {
	int id;
	int ms;
	int nextwin;
	int um;

	public SubjectState(int i, int j, int k, int l) {
		id = i;
		ms = j;
		nextwin = k;
		um = l;
	}

	@Override
	public int compareTo(SubjectState o) {
		return -ms + o.ms;
	}
}
