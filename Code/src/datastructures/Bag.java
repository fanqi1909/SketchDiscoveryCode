package datastructures;

import java.util.ArrayList;

public interface Bag<E> {
	public void insert(E element);
	
	public E get(int id ,int seq);
	
	public ArrayList<E> get(int id);

	public int getObjectCount();

	public int getElementCount();

	public void print();

	boolean contains(E e);

	public boolean hasReported(News n);
}
