package offline;

import java.util.ArrayList;
import java.util.Collections;

import datastructures.Event;
import datastructures.EventsBag;
import datastructures.News;
import datastructures.WindowIndex;

import model.CumulativeSum;
import model.SubjectComputation;
import conf.Comp;

public class IndexCurrentPrune implements IndexCreator{
	private static final int K = conf.Constants.P;
	private final EventsBag histories;
	WindowIndex wi;
	// beta(i) is for window_index(i)
	ArrayList<ArrayList<ArrayList<Event>>> J;
	ArrayList<CumulativeSum> GS;
	ArrayList<SubjectComputation> FCS;
	int max_window;
	int num_of_subject;

	public IndexCurrentPrune(EventsBag input_history) {
		
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
		for (int i = 0; i < num_of_subject; i++) {
			ArrayList<Event> history = histories.getEvents(i);
			GS.add(new CumulativeSum(history));
			FCS.add(new SubjectComputation(history));
		}
//		buildIndex();
	}

	public void buildIndex() {
		// get player order
		// use static order first, later change to a dynamic order
		for(int id = 0, totalID=histories.getObjectCount(); id <totalID; id++) {
			SubjectComputation FC = FCS.get(id);
			for(int w = 1, totalWin = histories.getEvents(id).size(); 
					w <= totalWin; w++) {
				int depth = K;
				int k1 = w / 2, k2 = w - k1;
				Event e1 = FC.eval(k1, 0);
				if(e1 == null) {
					break;
				}
				Event e2 = FC.eval(k2,0);
				if(e2 == null) {
					break;
				}
				if (e1.getVal() + 
						e2.getVal() < 
						wi.getBeta(w)) {
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
					wi.insert(wrapper);
				}
			}
		}
	}
	

	@Override
	public WindowIndex getIndex() {
		return wi;
	}
}
