package offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.WindowIndex;

import model.CumulativeSum;
import model.SubjectComputation;
import conf.Comp;

/**
 * Brute compute Index
 * @author a0048267
 *
 */
public class BruteIndexCreation implements IndexCreator {
	private static final int K = conf.Constants.P;
	private final EventsBag histories;
	WindowIndex wi;
	// beta(i) is for window_index(i)
	ArrayList<ArrayList<ArrayList<Event>>> J;
	ArrayList<CumulativeSum> GS;
	ArrayList<SubjectComputation> FCS;
	Stack<SubjectState> subjectQueue;

	int max_window;
	int num_of_subject;

	public BruteIndexCreation(EventsBag input_history) {
		// max_window = 0;
		// copy the input history to histories
		histories = input_history;
		max_window = histories.getMaxWindow();
		wi = new WindowIndex(max_window);
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
		GS = new ArrayList<>();
		FCS = new ArrayList<>();
		subjectQueue = new Stack<>();
		for (int i = 0; i < num_of_subject; i++) {
			ArrayList<Event> history = histories.getEvents(i);
			GS.add(new CumulativeSum(history));
			FCS.add(new SubjectComputation(history));
			subjectQueue.push(new SubjectState(i,
					history.get(0).getVal() * 1000, 0,
					history.get(0).getVal() * 1000));
		}
//		buildIndex();
	}

	@Override
	public void buildIndex() {
		// get player order
		// use static order first, later change to a dynamic order
		SubjectState ss;
		while (!subjectQueue.isEmpty()) {
			ss = subjectQueue.pop();
			SubjectComputation FC = FCS.get(ss.id);
			int id = ss.id;
			int w = ss.nextwin;
			if (w >= histories.getEvents(id).size()) {
				continue;
			}
			for (int e = 0; e < K; e++) {
				Event next = FC.eval(w, e);
				if (next == null) {
					// means K is large for this window,
					// we break the loop
					continue;
				}
				News wrapper = new News();
				wrapper.set(next, w);
				wi.insert(wrapper);
			}
			ss.nextwin++;
			subjectQueue.push(ss);
		}
	}

	@Override
	public WindowIndex getIndex() {
		return wi;
	}
}
