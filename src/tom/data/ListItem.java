package tom.data;

/**
*** ListItem class
*** Stores a value added to a LinkedList and its precedeing ListItem.
**/

public class ListItem<T> implements java.io.Serializable {
	private static final long serialVersionUID = -349673617555909340L;
	private T value; ///The item stored.
	private ListItem<T> previous; ///The precedeing ListItem.
	/** Constructs a new ListItem for the given object. **/
	public ListItem(T t) {
		this(t,null); //call the other constructor with a null second parameter
	}
	/** Constructs a new ListItem for the given object. **/
	public ListItem(T t,ListItem<T> previous) {
		setValue(t); //set the value to the given item
		this.previous = previous; //set the previous item
	}
	/** Sets the value of the item stored. **/
	public void setValue(T t) {
		value = t; //set the value to the given item
	}
	/** Returns the item stored. **/
	public T getValue() {
		return value; //return the item stored
	}
	/** Defines the precedeing ListItem. **/
	public void setPrevious(ListItem<T> listItem) {
		previous = listItem; //store the precedeing ListItem
	}
	/** Returns the precedeing ListItem. **/
	public ListItem<T> getPrevious() {
		return previous; //return the precedeing ListItem
	}
}