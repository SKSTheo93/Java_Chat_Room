package utilitiies;

import java.util.Iterator;

public interface DoubleIterator<E> extends Iterator<E> {
	public abstract boolean hasPrevious();
	public abstract E previous();
	public abstract DoubleIterator<E> resetToBegin();
	public abstract DoubleIterator<E> resetToEnd();
}