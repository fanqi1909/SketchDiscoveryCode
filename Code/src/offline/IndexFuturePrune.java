package offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.WindowIndex;

import model.CumulativeSum;
import model.SubjectComputation;
import conf.Comp;

public class IndexFuturePrune implements IndexCreator {
	private static final int K = conf.Constants.P;
	private final EventsBag histories;
	WindowIndex wi;
	ArrayList<ArrayList<ArrayList<Event>>> J;
	ArrayList<CumulativeSum> GS;
	ArrayList<SubjectComputation> FCS;
	Queue<SubjectState> subjectQueue;

	int max_window;
	int num_of_subject;

	public IndexFuturePrune(EventsBag input_history) {
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
		// initialize G
		// initialize FC
		// initialize Ms
		// Ms = new ArrayList<>();
		// Um = new ArrayList<>();
		GS = new ArrayList<>();
		FCS = new ArrayList<>();
		subjectQueue = new LinkedList<>();
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
			if (ms < wi.getBeta(w)) {
				continue;
			}
			if (w >= histories.getEvents(id).size()) {
				continue;
			}
			int um = ss.um;
			SubjectComputation FC = FCS.get(id);
			for (int e = 0; e < depth; e++) {
				Event next = FC.eval(w, e);
				if (next == null) {
					// means K is large for this window,
					// we break the loop
					break;
				}
				News wrapper = new News();
				wrapper.set(next, w);
				wi.insert(wrapper);
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
		
	@Override
	public WindowIndex getIndex() {
		return wi;
	}

}
