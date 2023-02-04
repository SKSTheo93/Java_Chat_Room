package exceptions;

public class NotComparableObjectException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public NotComparableObjectException() {
		super("Error during comparison");
	}
	
	public NotComparableObjectException(String message) {
		super(message);
	}
}