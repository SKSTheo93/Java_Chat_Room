package utilitiies;

public interface DoubleIterable<T> extends Iterable<T> {
	@Override
	public abstract DoubleIterator<T> iterator();
}