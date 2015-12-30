package tom.data;

/**
*** HashTableFullException class
*** Exception to be thrown when a user attempts to add an item to a full HashTable.
**/

public class HashTableFullException extends Exception {
	private static final long serialVersionUID = -8319417007182681506L;
	/** Creates a new HashTableFullException with the given exception message. **/
	public HashTableFullException(String s) {
		super(s);
	}
}