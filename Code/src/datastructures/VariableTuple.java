package datastructures;

public class VariableTuple {
	private int[] values;
	private int length;
	
	public VariableTuple() {
		values = null;
		length = 0;
	}
	
	public VariableTuple(int size) {
		length = size;
		values = new int[length];
	}
	
	public void setValue(int size, int value) {
		values[size] = value;
	}
	
	public int getValue(int size) {
		return values[size];
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		for(int i = 0; i < length-1; i++) {
			sb.append(values[i]+",");
		}
		sb.append(values[length-1]+">");
		return sb.toString();
	}
}
