package tom.data;
/**
*** HashTableTooLargeException class
*** Exception to be thrown when a user attempts to create a HashTable of a size larger than it's maximum size is designed to be.
**/

public class HashTableTooLargeException extends Exception {
	private static final long serialVersionUID = 19194255099439580L;
	/** Creates a new HashTableTooLargeException with the given exception message. **/
	public HashTableTooLargeException(String s) {
		super(s);
	}
}