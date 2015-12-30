package tom.data;

/**
*** LinkedList class
*** Allows for adding items to the start of end of a list, checking, retrieving and removing items from the list (although inefficient to do so)
*** and retrieving the data as an array. The data in the array will be references to the same objects stored - not copies.
*** Superior to array for prepending, appending and inserting items, inferior for searching (incl. remove, get).
*** This LinkedList is not doubly linked so that the performance of routines best suited to the data structure type are not reduced.
*** However, the head and tail are always known for quick accessing of the first and last items, and adding to the start and end of the list.
*** A LinkedList can be created using an array.
*** In an attempt to optimise the implementation where it is lacking, item removal has been enhanced to allow for batch removals of consecutive items,
*** and the MaintainArray feature can be enabled to maintain an array of the data for regular calls to contains(T item) and get(int index) between additions to the list.
*** The LinkedList has been optimised to continue to use the maintained array for the get(int index) method without updating it after the list has changed, if the requested index can still be located within it. 
*** The MaintainArray feature does not result in a simulation of a Vector or ArrayList. All operations remain that of a LinkedList, but the array is updated only
*** when a call is made to the methods contains(T item), get(int index) where the index would point to a newly added item, and updateArray(), and only if the list has changed since the last sync.
*** This provides some of the benefits of an array without sacrificing the additional speed of a linked list.
*** The MaintainArray feature can be made the most of by toggling it on before many calls to contains() and get(), and toggling off before many calls to add(), insert, prepend() and or append().
*** It also means that the behaviour does not need to be created when needed by an implementing system.
**/

public class LinkedList<T> implements java.io.Serializable {
	private static final long serialVersionUID = 3026711137136834721L;
	private boolean maintainArray = true; ///Toggle for the MaintainArray feature.
	private boolean changed; ///Whether the LinkedList has changed.
	private int newAppended = 0; ///How many new items have been appended.
	private int newPrepended = 0; ///How many new items have been prepended.
	private boolean newInsertion; //Whether the LinkedList's array needs to be completely reconstructed.
	private int size; ///The size of the LinkedList.
	private ListItem<T> head; ///The first ListItem in the LinkedList.
	private ListItem<T> tail; ///The last ListItem in the LinkedList.
	private ListItem<T> maintainedHead; ///The first ListItem in the maintained array.
	private Object[] array; ///The optionally maintained array.
	/** Constructs a new LinkedList object. **/
	public LinkedList() {
		setSize(0); //set the size to 0
	}
	/** Constructs a new LinkedList object using the given array. **/
	public LinkedList(T[] array) {
		setSize(0); //set the size to 0
		while (size != array.length) { //loop for each item in the array
			append(array[size]); //add the array at the current index
		}
	}
	/** Sets the Maintain Array feature on or off. **/
	public void setMaintainArray(boolean b) {
		maintainArray = b;
	}
	/** Returns whether the LinkedList is maintaining an array. **/
	public boolean isMaintainArray() {
		return maintainArray;
	}
	/** Returns whether the maintained array is up to date. **/
	public boolean isMaintainedArrayUpToDate() {
		return (array != null && !newInsertion && newAppended == 0 && newPrepended == 0);
	}
	/** Updates the maintained array. **/
	public void updateArray() {
		Object[] newArray = new Object[this.size]; //create a new array for the list
		toArray(newArray); //populate the array
		this.array = newArray;
		//mark the array as up to date
		changed = false;
		newAppended = 0;
		newPrepended = 0;
		newInsertion = false;
	}
	/** Sets the size of the list. **/
	private void setSize(int i) {
		size = i; //set the new size
		if (!changed) { changed = true; } //mark the list as changed since the last array sync
	}
	/** Adds the given item to the end of the LinkedList. **/
	public void add(T item) {
		append(item);
	}
	/** Adds the given item to the end of the LinkedList. **/
	public void append(T item) {
		ListItem<T> listItem = new ListItem<T>(item,(size == 1 ? head : tail)); //create a new ListItem object for the given item
		if (size == 0) { head = listItem; } //if the size of the LinkedList is 0, set the head to the new ListItem as well
		tail = listItem; //update the tail to reference the new last item
		setSize(size+1); //increase the size
		newAppended++;
	}
	/** Adds the given item to the beginning of the LinkedList. **/
	public void prepend(T item) {
		ListItem<T> listItem = new ListItem<T>(item); //create a new ListItem object for the given item
		if (size == 0) { tail = listItem; } //if the size of the LinkedList is 0, set the tail to the new ListItem aswell
		else { head.setPrevious(listItem); } //if there are already items in the LinkedList, set the previous item of the current head to the new ListItem
		head = listItem; //set the LinkedList head to the new ListItem
		setSize(size+1); //increase the size
		newPrepended++;
	}
	/** Inserts the given item into the LinkedList at the specified index. **/
	public void insert(T item,int index) {
		if (index == 0) { prepend(item); }
		else if (index == this.size) { append(item); }
		else {
			ListItem<T> nextItem = getItem(index); //get the item currently at the specified index
			ListItem<T> listItem = new ListItem<T>(item,nextItem.getPrevious()); //create a new ListItem object for the given item, with a link to the item in the preceeding index
			nextItem.setPrevious(listItem); //set the previous item for the ListItem at the specified index to the newly inserted item
			setSize(size+1); //increase the size;
			//mark the maintained array as needing to be reconstructed
			newInsertion = true;
		}
	}
	/** Removes every ListItem in the LinkedList. **/
	public void clear() {
		head = null;
		tail = null;
		array = null;
		setSize(0);
		//while (size != 0) { //loop until the list size is 0
		//	remove(tail.getValue()); //remove the last ListItem
		//}
	}
	/** Returns whether the list contains the given item. **/
	public boolean contains(T item) {
		return contains(item,true);
	}
	/** Returns whether the list contains the given item. **/
	public boolean contains(T item,boolean instance) {
		boolean isMaintainedArrayUpToDate = isMaintainedArrayUpToDate();
		if (maintainArray || isMaintainedArrayUpToDate) { //if an array should be maintained for this method or the maintained array is up to date anyway
			if (!isMaintainedArrayUpToDate) { updateArray(); } //if the array is not up to date, update it
			//search the array
			for (int i = 0; i != array.length; i++) {
				if (instance && array[i] == item) { //if this item is the same instance as the given item
					return true; //return true
				}
				else if (!instance && array[i].equals(item)) { //else if this item equals the given item
					return true; //return true
				}
			}
			//the item doesn't exist in the list; return false
			return false;
		}
		else { //else search the LinkedList
			ListItem<T> index = tail; //reference the last ListItem in the LinkedList
			int i = size - 1; //store the last index of the array (get in descending order)
			while (index != null) { //loop for each ListItem in the LinkedList
				if (instance && index.getValue() == item) { //if this ListItem value is the same instance as the given item
					return true; //return true
				}
				else if (!instance && index.getValue().equals(item)) { //else if this ListItem value equals the given item
					return true; //return true
				}
				index = index.getPrevious(); //reference the precedeing ListItem
				i--; //decrease the index counter
			}
			//the item doesn't exist in the list; return false
			return false;
		}
	}
	/** Remove the given item from the LinkedList. **/
	public void remove(T item) {
		/*
		** This LinkedList is not designed to be efficient for frequent removal of items, only efficient storage and retrieval as an array.
		** However one removal like this is faster than generating an array from the list in order to remove an item.
		*/
		if (tail.getValue().equals(item)) { //if the last ListItem stores the given item
			ListItem<T> previous = tail.getPrevious(); //get the second to last ListItem
			tail.setPrevious(null); //set the last ListItem's preceding reference to null
			tail = previous; //set the tail to the second to last ListItem
			setSize(size-1); //decrease the defined size of the LinkedList
		}
		else { //else check the previous ListItem for each iteration
			ListItem<T> next = tail; //set the succeeding ListItem to the last ListItem
			while (next.getPrevious() != null) { //loop for each ListItem
				if (next.getPrevious().getValue().equals(item)) { //if this iteration's previous ListItem stores the given item
					ListItem<T> index = next.getPrevious(); //reference the previous ListItem
					next.setPrevious(next.getPrevious().getPrevious()); //set the succeeding ListItem's previous reference two ListItems back
					if (index == head) { head = next; } //if the index being removed is the head, set the next ListItem as the head
					index.setPrevious(null); //set the ListItem to be removed's previous reference to null
					index = null; //nullify the reference
					setSize(size-1); //decrease the defined size
					break; //exit the loop
				}
				next = next.getPrevious(); //set the succeeding ListItem to the previous ListItem
			}
		}
	}
	/** Remove the item at the specified index from the LinkedList. **/
	public void remove(int i) {
		/*
		** This LinkedList is not designed to be efficient for frequent removal of items, only efficient storage and retrieval as an array.
		** However one removal like this is faster than generating an array from the list in order to remove an item.
		*/
		remove(i,1);
	}
	/** Remove the item at the specified index and N following items from the LinkedList. **/
	public void remove(int i,int n) {
		/*
		** This LinkedList is not designed to be efficient for frequent removal of items, only efficient storage and retrieval as an array.
		** However one removal like this is faster than generating an array from the list in order to remove an item.
		** Steps have been taken to optimize it by tracking from the last item because more recently added items are most likely to be modified,
		** and the method allows for removing multiple consecutive items, which while does not address the penalties of seeking, makes such necessary
		** removals require only one invocation, and thus more efficient.
		*/
		if (size == 0) { return; } //if the size is 0, nothing can be removed
		i = size - i - 1; //convert the index (given as from the first element) into the number from the last element as each ListItem only knows its preceding item
		i+= n; //start from the last item to be removed
		if (i > -1) { //if the specified index is within the range of this LinkedList
			ListItem<T> next = null; //the last ListItem's succeeding ListItem must be null
			if (i != 0) { //if the last item to be removed is not the tail
				//since i isn't 1, do the first would-be iteration outside the loop to avoid checking for a null next reference each iteration
				next = tail;
				i--; //decrease the index counter
				while (i != 0) { //loop until the last item to be removed is reached
					next = next.getPrevious(); //set the succeeding ListItem to its previous ListItem
					i--; //decrease the index counter
				}
			}
			if (next == null) { //if the succeeding ListItem is null, the start point is the tail
				//for efficiency, removing from the tail has its own loop to avoid unecessary conditional statements
				while (n != 0) { //while there are still items to be removed
					ListItem<T> previous = tail.getPrevious(); //get the previous ListItem
					tail.setPrevious(null); //unlink the tail from the list
					tail = previous; //set the new tail
					n--; //decrease the number of items remaining to be removed
					setSize(size-1); //decrease the defined list size
				}
			}
			else { //else the start point is not the tail
				while (n != 0) { //while there are items to be removed
					ListItem<T> index = next.getPrevious(); //get the item to be removed
					next.setPrevious(index.getPrevious()); //remove the item to be removed from the list
					index.setPrevious(null); //separate the item from the list completely
					n--; //decrease the number of items remaining to be removed
					setSize(size-1); //decrease the defined list size
				}
				if (next.getPrevious() == null) { //if the succeeding item of the removed items has no predecessor
					head = next; //set it as the head
				}
			}
		}
	}
	/** Returns the size of the LinkedList. **/
	public int getSize() {
		return size; //return the list size
	}
	@SuppressWarnings("unchecked")
	/** Returns the value of the item at the given point in the list. **/
	public T get(int i) {
		/*
		** This method is for convenience for one-time fetches and debugging purposes; it's not an efficient means to repetitively access or search
		** for items in the LinkedList. For that, the toArray(T[] array) method should be used.
		** If this method is to be used repetitively, it can be greatly improved by using the maintain array feature.
		*/
		//if there is a maintained array
		if (this.array != null) {
			//if the maintained array has not changed
			if (isMaintainedArrayUpToDate()) {
				return (T)this.array[i]; //return the item from the array
			}
			//else if the out dated array does not require reconstruction to still be useful
			else if (!newInsertion) {
				//if the translated index is within the bounds of the old array
				int translatedI = i - newPrepended;
				if (translatedI >= 0 && translatedI < this.array.length) {
					return (T)array[translatedI]; //return the item from the out dated array
				}
			}
		}
		//if an array should be maintained
		if (maintainArray) {
			updateArray(); //update the array
			return (T)this.array[i]; //return the item
		}
		//else use the LinkedList
		else {
			return getItem(i).getValue();
		}
	}
	/** Returns the item at the given point in the list. **/
	public ListItem<T> getItem(int i) {
		/*
		** This method is for convenience for one-time fetches and debugging purposes; it's not an efficient means to repetitively access or search
		** for items in the LinkedList. For that, the toArray(T[] array) method or setMaintainArray(true) should be used.
		*/
		ListItem<T> index = tail; //reference the last ListItem in the LinkedList
		i = size - i - 1; //convert the index (given as from the first element) into the number from the last element
		while (i != 0) { //loop until the requested index is reached
			index = index.getPrevious(); //reference the precedeing ListItem
			i--; //decrease the index counter
		}
		return index; //return the item stored at the specified index
	}
	/** Returns the last item added to the list (convenience method). **/
	public T getLast() {
		return tail.getValue();
	}
	/** Returns the last ListItem in the LinkedList (convenience method). **/
	public ListItem<T> getLastItem() {
		return tail;
	}
	/** Returns the first item added to the list (convenience method). **/
	public T getFirst() {
		return head.getValue();
	}
	/** Returns the first ListItem in the LinkedList (convenience method). **/
	public ListItem<T> getFirstItem() {
		return head;
	}
	/** Fills the given array with the items in the LinkedList. **/
	public void toArray(Object[] array) {
		if (isMaintainedArrayUpToDate()) { //if a maintained array is up to date, read from the array instead
			for (int i = 0; i != array.length; i++) { //loop for each item in the list
				array[i] = this.array[i]; //add the item to the array
			}
		}
		else if (this.array != null && !newInsertion) { //else if it has changed but only from appendages or prependages
			//only read the newly added items from the linked list
			ListItem<T> index = maintainedHead.getPrevious(); //reference the first newly added list item
			//add the new prependages
			for (int i = newPrepended-1; i != -1; i--) {
				array[i] = index.getValue();
				index = index.getPrevious();
			}
			//update the maintainedHead
			maintainedHead = head;
			//copy from the current maintained array
			System.arraycopy(this.array,0,array,newPrepended,(this.array.length));
			//add the new appendages
			index = tail; //reference the last ListItem in the LinkedList
			newAppended++; //increase because always need to deduct 1, while length must remain the same
			for (int i = 1; i != newAppended; i++) {
				array[array.length-i] = index.getValue(); //store the value stored in the ListItem in the current index of the given array
				index = index.getPrevious(); //reference the precedeing ListItem
			}
			//note if the given array is not the same size as the list, it will be incorrectly filled.
			//this is a fault of the calling code, and so is left unchecked for to not reduce this class's performance speed
		}
		else { //else use the LinkedList
			maintainedHead = head;
			ListItem<T> index = tail; //reference the last ListItem in the LinkedList
			int i = size - 1; //store the last index of the array (fill in descending order)
			//don't worry about error handling.
			//if an IndexOutOfBoundsException occurs due to the given array being too small, let a RunTimeException be thrown; it's the invoking class' problem
			while (index != null) { //loop for each ListItem in the LinkedList
				array[i] = index.getValue(); //store the value stored in the ListItem in the current index of the given array
				index = index.getPrevious(); //reference the precedeing ListItem
				i--; //decrease the index counter
			}
		}
	}
}