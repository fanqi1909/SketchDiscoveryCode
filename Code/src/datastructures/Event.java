package datastructures;


/**
 * the very basic unit in our system
 * 
 * @author a0048267
 * 
 */
public class Event extends VariableTuple{
	private int ID = 0; // object id index
	private int TS = 1; // the sequence id index, starting from 1
	private int VAL = 2;// the attribute value

	public Event() {
		super(3);
	}

	/**
	 * val is a fix point number, which is represented using int
	 * @param id
	 * @param ts
	 * @param val
	 */
	public Event(int id, int ts, int val) {
		super(3);
		setValue(ID, id);
		setValue(TS, ts);
		setValue(VAL, val);
	}

	public int getID() {
		return getValue(ID);
	}

	public void setID(int id) {
		setValue(ID, id);
	}

	public int getTS() {
		return getValue(TS);
	}

	public void setTS(int ts) {
		setValue(TS,ts);
	}

	public int getVal() {
		return getValue(VAL);
	}

	public void setVal(int val) {
		setValue(VAL,val);
	}
	
	@Override 
	public Event clone() {
		return new Event(getID(), getTS(), getVal());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Event e = new Event();
		System.out.println(e);
		e.setID(10);
		e.setTS(2);
		e.setVal(15);
		System.out.println(e);
		Event e2 = e.clone();
		e2.setTS(3);
		System.out.println(e+"\t"+e2);
//		System.out.println(EVENT_VAL_BIG_COMP.compare(e2, e));
	}

	// supply several static comparators that compares Events based on either
	// time sequence
	// or value;
	
}
