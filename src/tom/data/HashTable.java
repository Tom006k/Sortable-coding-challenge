package tom.data;

/**
*** HashTable Class
*** Maps keys to array indexes providing an efficient expansion capable
*** data structure and associative arrays.
*** The hash table will create a multi dimensional array close to the given initial size
*** and can automatically expand at specified remaining capacity percentage points.
*** When the size exceeds 1000 it will begin to split the hash key into a two-part index key for the two dimensions in the array.
*** Uses linear probing collision prevention techique.
**/

public class HashTable<T> implements java.io.Serializable {
	private static final long serialVersionUID = -6724741648294861871L;
	//MAXIMUM DEFAULT ARRAY STACK HEAP = 44,708,731 (unless otherwise specified with flags)
	public static int MAX_SIZE = 998001; ///The maximum number of elements a HashTable can have
	private double expansionPoint = 75.0; ///The percentage of elements used before expanding the table
	private double expansionRate = 100; ///The percentage to expand the table by
	private int mod; ///The modulo for the hashing algorithm
	private int itemCount = 0; ///The number of items stored in the table
	private int[] size = new int[2]; ///The length of the corresponding dimension of the table
	private Object[][][] table; ///The array that the items and their keys will be stored in
	/** Creates a new HashTable of size 999. **/
	public HashTable() {
		table = createTable(999);
		//table = new Object[1][999][2]; //initialise the table for 999 elements
		//mod = 999; //set mod to the default 999 elements
	}
	/** Creates a new HashTable of the specified size. **/
	public HashTable(int size) {
		if (size > MAX_SIZE) { //if the specified size is greater than the allowed maximum size
			//throw the HashTableTooLargeException
			try { throw new HashTableTooLargeException("Requested size exceeds maximum"); }
			catch(Exception e) { e.printStackTrace(); }
		}
		else { //otherwise create the table to the specified size
			table = createTable(size);
		}
	}
	/** Returns an array of the keys to access all the information stored in the table. **/
	@SuppressWarnings("unchecked")
	public String[] getKeys() {
		String[] keys = new String[itemCount];
		int count = itemCount;
		mainloop:
		for (int i = 0; i != table.length; i++) {
			for (int ii = 0; ii != table[i].length; ii++) {
				//if (itemCount > 20) { System.out.println("table["+i+"]["+ii+"][1] => "+(String)table[i][ii][1]); }
				if (table[i][ii][1] != null) {
					keys[itemCount-count] = (String)table[i][ii][1];
					count--;
					if (count == 0) { break mainloop; }
				}
			}
		}
		return keys;
	}
	/** Add the given item and key to the specified index of the HashTable. **/
	private void add(String key,T item,int keyA,int keyB) {
		table[keyA][keyB][0] = item; //store the item
		table[keyA][keyB][1] = key; //store the key for use in determining if hash keys are pointing to the correct index
		itemCount++; //increase the counter for the number of items stored in the HashTable
	}
	/** Add the given item to the HashTable using the given key. **/
	public void add(String key,T item) {
		HashKey hashKey = new HashKey(key,mod,size); //get the hash key for the given key
		int a = hashKey.indexA,b = hashKey.indexB; //copy the two indexes
		do {
			try {
				if (table[a][b][1] == null) { }
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.out.println("Exception at: table["+a+"]["+b+"][1] -> mod: "+mod+"; size; "+table.length+"*"+table[0].length+" = "+(table.length*table[0].length));
			}
			if (table[a][b][1] == null) { //if there is no key stored at the index
				add(key,item,a,b); //add the item and key to the HashTable at that index
				if (expansionPoint > 0) { //if the expansion point is greater than 0
					double percent = (100.0 / (table.length * table[0].length)) * itemCount; //determine percentage of elements in use
					if (percent >= expansionPoint && expansionRate > 0) { //if the percentage is greater than or equal to the expansion point, and the expansion rate is greater than 0
						expandTable((int)(mod + (mod * (expansionRate / 100)))); //expand table by the expansionRate
					}
				}
				return; //exit the loop and return to the calling statement
			}
			else if (table[a][b][1] != null && table[a][b][1].equals(key)) { //else, if the key stored at the index is the same key being used to store the given item
				add(key,item,a,b); //overwrite the item in the HashTable
				return; //exit the loop and return to the calling statement
			}
			else { //else, point to the next item in the HashTable to probe for a free element
				b++; //increase the second index of the hash key
				if (b == table[a].length) { //if the second index is equal to the length of the second dimension
					b = 0; //set the second index of the hash key to 0, and
					a++; //increase the first index of the hash key
					if (a == table.length) { //if the first index is equal to the length of the first dimension
						a = 0; //set the first index of the hash key to 0
					}
				}
			}
		} while (a != hashKey.indexA || b != hashKey.indexB); //loop until the key points to the original position
		//if this point is reached then the HashTable has no more free elements. throw a HashTableFullException and display it to the user
		try { throw new HashTableFullException("There are no more available indexes in "+this); }
		catch(HashTableFullException e) { e.printStackTrace(); }
	}
	/** Creates the table to the specified size and assigns values to setLen and mod. **/
	private Object[][][] createTable(int i) {
		String iStr = (""+i); //store the given size as a String
		double len = iStr.length() / 3.0; //calculate and store the number of digits in the given size divided by 3 to determine the number of maximum (999) dimensions
		this.size = new int[2]; //create an int array of two elements
		for (int ii = 0; ii != size.length; ii++) { //loop for each element in the int array
			if (len < 0) { size[ii] = 1; } //if the number of maximum dimensions is less than 0, store the dimension size for this index to be 1
			else if (len >= 1) { size[ii] = (i < 999 ? i : 999); } //if the maximum dimensions is greater than or equal 1, store the dimension size for this index to be the smaller of i and 999
			else if (len < 1) { //if the number of maximum dimensions is less than 1 but greater than 0
				int r = 1; //start r at 1
				for (int iii = 0; iii != ii; iii++) { r*= 999; } //loop, multiplying r by 999 for the number of iterations done by the previous loop
				//calculate the remaining number of elements required to reach the specified size
				r = (int)Math.ceil(i / (double)r); //divide r by i to double precision, and round the value up because you cannot have a fraction of an element
				size[ii] = r; //store the dimension size for this index to be the value calculated and stored in r
			}
			len--; //decrease the number of maximum dimensions
		}
		//swap the values in the size array
		int tmp = size[0];
		size[0] = size[1];
		size[1] = tmp;
		Object[][][] newTable = new Object[size[0]][size[1]][2]; //crease a new 3 dimensional array using the two calculated sizes and 2 for the third dimension
		mod = (size[0] * size[1]) - 1; //set the modulo to the HashTable size minus 1
		return newTable; //return the array
	}
	/** Returns whether the given item exists in the HashTable. **/
	public boolean exists(T item) {
		return (getKey(item) != null ? true : false); //if there are matching keys, return true, otherwise return false
	}
	@SuppressWarnings("unchecked")
	/** Expands the table to the new specified size. (Public for potential convenience; the automatic expansion should be used.) **/
	public void expandTable(int newSize) {
		//System.out.println("Creating new table of size "+newSize);
		HashTable<T> newTable = new HashTable<T>(newSize); //create a new HashTable of the specified new size
		newTable.setExpansionPoint(this.expansionPoint);
		newTable.setExpansionRate(this.expansionRate);
		for (int i = 0; i != table.length; i++) { //loop for each element in the first dimension of the current array
			for (int ii = 0; ii != table[i].length; ii++) { //loop for each element in the second dimension of the current array
				if (table[i][ii][1] != null) { newTable.add((String)table[i][ii][1],(T)table[i][ii][0]); } //if the index is not null, hash the key and store the item in the new HashTable
			}
		}
		table = newTable.table; //point this instance's array to the array created with the new HashTable object
		mod = newTable.mod; //update the mod
		size = newTable.size; //update the index lengths
		//prepare the new HashTable object to be garbage collected
		newTable.table = null; //set the new HashTable object's table array reference to null
		newTable = null; //set the new HashTable object reference to null
	}
	@SuppressWarnings("unchecked")
	/** Returns the item stored at the index of the given key in the HashTable. **/
	public T get(String key) {
		HashKey hashKey = new HashKey(key,mod,size); //get the hash key for the given key
		//if the key stored at the hash key index is the given key, return the item
		if (table[hashKey.indexA][hashKey.indexB][1] != null && table[hashKey.indexA][hashKey.indexB][1].equals(key)) { return (T)table[hashKey.indexA][hashKey.indexB][0]; }
		else { return getNextItem(key,hashKey); } //else, return the result of a linear probe for the given key
	}
	/** Returns the number of items stored in the HashTable. **/
	public int getItemCount() {
		return itemCount; //return the item count
	}
	/** Returns the number of occurances of the given item in the HashTable. **/
	public int getItemCount(T item) {
		return getKey(item).length; //return the length of the array returned from the getKey for the given item method
	}
	/** Return a String array of keys pointing to items matching the given item. **/
	public String[] getKey(T item) {
		/*
		** improve this method to allow wild card and possibly regex searches
		*/
		String[] keys = new String[1]; //create an array for the keys initially at size 1
		for (int i = 0; i != table.length; i++) { //loop for each element in the first dimension of the array
			for (int ii = 0; ii != table[i].length; ii++) { //loop for each element in the second dimension of the array
				if (table[i][ii][0] == item) { //if the index has the given item stored
					if (keys[keys.length-1] != null && !keys[keys.length-1].equals("")) { //if the keys array is full
						String[] keys2 = new String[keys.length+1]; //create a new array 1 size larger
						System.arraycopy(keys,0,keys2,0,keys.length); //copy the items in keys to the new array
						keys = keys2; //point keys to the new array
					}
					keys[keys.length-1] = (String)table[i][ii][1]; //store the key for the matching item in the last index of the keys array
				}
			}
		}
		return (keys[0] == null || keys[0].equals("") ? null : keys); //if a match was found and corresponding key stored, return the keys array, otherwise return null
	}
	@SuppressWarnings("unchecked")
	/** Returns the item stored with the given key in the HashTable. **/
	private T getNextItem(String key,HashKey hashKey) {
		seek(key,hashKey); //attempt to point the hash key to the correct item
		if (hashKey.indexA == -1) { return null; } //if the first index of the hash key is -1, there is no item for the given key. return null
		else { return (T)table[hashKey.indexA][hashKey.indexB][0]; } //otherwise, return the item
	}
	/** Returns the percentage of the HashTable that is not in use. **/
	public double getRemainingCapacity() {
		return 100.0 - getUsedCapacity(); //return 100 subtract the used percentage
	}
	/** Returns the size of the HashTable. **/
	public int getSize() {
		return (table.length * table[0].length); //calculate and return the size of the first two dimensions of the array
	}
	/** Returns the percentage of the HashTable that is in use. **/
	public double getUsedCapacity() {
		return (100.0 / (table.length * table[0].length)) * itemCount; //calculate and return the percentage of elements in use
	}
	/** Remove the item at the specified index from the HashTable. **/
	private void remove(int keyA,int keyB) {
		table[keyA][keyB][0] = null; //set the item space to null
		table[keyA][keyB][1] = null; //set the key space to null
		itemCount--; //decrease the counter for the number of items stored in the HashTable
	}
	/** Remove the item at the specified index from the HashTable. **/
	public void remove(String key) {
		HashKey hashKey = new HashKey(key,mod,size); //get the hash key for the given key
		if (table[hashKey.indexA][hashKey.indexB][1] != null && table[hashKey.indexA][hashKey.indexB][1].equals(key)) { //if the hash key points to the correct location
			remove(hashKey.indexA,hashKey.indexB); //remove the item from the HashTable
		}
		else { //otherwise, seek the correct item via linear probing
			seek(key,hashKey); //attempt to point the hash key to the correct item
			if (hashKey.indexA != -1) { remove(hashKey.indexA,hashKey.indexB); } //if the first index is not -1, remove the item from the HashTable
			//otherwise there is no item in the HashTable for the given key, so do nothing
		}
	}
	/** Linear probe with a HashKey for the index storing the item stored with the given key. **/
	private void seek(String key,HashKey hashKey) {
		int a = hashKey.indexA,b = hashKey.indexB; //copy the two indexes
		do {
			b++; //increase the second index of the hash key
			if (b == table[a].length) { //if the second index is equal to the length of the second dimension
				b = 0; //set the second index of the hash key to 0, and
				a++; //increase the first index of the hash key
				if (a == table.length) { //if the first index is equal to the length of the first dimension
					a = 0; //set the first index of the hash key to 0
				}
			}
			if (table[a][b][1] != null && table[a][b][1].equals(key)) { //if the key at the index is the key being searched for
				hashKey.indexA = a; //set the first index of the hash key to point to the correct first dimension
				hashKey.indexB = b; //set the second index of the hash key to point to the correct second dimension
				return; //exit the loop and return to the calling statement
			}
		} while (a != hashKey.indexA || b != hashKey.indexB); //loop until the key points to the original position
		//if this point is reached then the item being searched for does not exist in the table
		hashKey.indexA = -1; //set the first index of the hash key to -1 to indicate no results
	}
	/** Set the percentage of elements used before the HashTable expands. **/
	public void setExpansionPoint(double d) {
		expansionPoint = d;
	}
	/** Set the percentage of the current HashTable size that the HashTable will expand by when the expansion point is reached. **/
	public void setExpansionRate(double d) {
		expansionRate = d;
	}
}