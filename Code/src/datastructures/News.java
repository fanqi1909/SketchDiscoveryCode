package datastructures;

public class News extends VariableTuple {
	private int ID = 0;
	private int TS = 1;
	private int WIN = 2;
	private int VAL = 3;
	private int RANK = 4;

	// private int hashCode;

	public News() {
		super(5);
	}

	public News(int id, int ts, int win, int val, int rank) {
		super(5);
		setValue(ID, id);
		setValue(TS, ts);
		setValue(WIN, win);
		setValue(VAL, val);
		setValue(RANK, rank);
		// hashCode = id * 113 + ts * 217 + win * 133;
	}

	public int getVal() {
		return getValue(VAL);
	}

	public int getID() {
		return getValue(ID);
	}

	public int getWin() {
		return getValue(WIN);
	}

	public int getTS() {
		return getValue(TS);
	}

	public int getRank() {
		return getValue(RANK);
	}

	public void setRank(int rank) {
		setValue(RANK, rank);
	}

	public void setVal(int val) {
		setValue(VAL, val);
	}

	@Override
	public String toString() {
		return "<" + getID() + "," + getTS() + "," + getWin() + "," + getVal()
				+ "," + getRank() + ">";
	}

	@Override
	public boolean equals(Object news) {
		if (news instanceof News) {
			News n = (News) news;
			return n.WIN == WIN && n.TS == TS && n.ID == ID;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getID() * 113 + this.getTS() * 217 + this.getWin() * 133;
	}

	public void updateRank(int i) {
		setValue(RANK, this.getRank() + i);
	}

	public void set(Event next, int w) {
		setValue(ID, next.getID());
		setValue(TS, next.getTS());
		setValue(WIN, w);
		setValue(VAL, next.getVal());
		setValue(RANK, -1);
	}

}
