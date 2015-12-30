package tom.data;

/**
*** HashKey class
*** Hashes given strings into hash key strings, formats them for use with the dimension sizes
*** of the array in the parent class, and converts them into two separate integer indexes for lookup and storage
*** in the two dimensions of the array in the parent class.
**/

public class HashKey implements java.io.Serializable {
	private static final long serialVersionUID = 2472495551910178508L;
	public int indexA; ///Stores the first integer index of the hash key
	public int indexB; ///Stores the second integer index of the hash key
	public String hashString; ///Stores the hash key string. (This is not necessary for the classes to function. Used for debugging purposes only.)
	/** Creates a new HashKey for the given key String. **/
	public HashKey(String key,int mod,int[] size) {
		/*
		** Jenkins Hash Function converted to Java from C.
		** http://en.wikipedia.org/wiki/Jenkins_hash_function
		*/
		int hashKey = 0; //start the hash key at 0
		for (int i = 0; i < key.length(); i++) { //loop for each character in the given key String
			hashKey+= key.charAt(i); //add the ASCII value of the character at the current index to the hash key
			hashKey+= (hashKey << 10); //increase the hash key by itself after an arithmetic shift left 10 times
			hashKey^= (hashKey >>> 6); //xor the hash key by itself after a logical shift right 6 times
		}
		hashKey+= (hashKey << 3); //increase the hash key by itself after an arithmetic shift left 3 times
		hashKey^= (hashKey >>> 11); //xor the hash key by itself after a logical shift right 11 times
		hashKey+= (hashKey << 15); //increase the hash key by itself after an arithmetic shift left 15 times
		//because unlike C, Java does not allow for creating unsigned primitive types (they are all signed)
		//the key must be validated and potentially altered further to ensure a positive value
		if (hashKey < 0) { hashKey = hashKey >>> 1; } //if the hash key is negative, logical shift it right once
		hashKey = hashKey % mod; //modulo the hash key to fit within hash table size (dimension1 * dimension2)
		parseKey(hashKey,size); //parse the generated key
	}
	/** Parses the hash key to get the two indexes for the array in the parent class. **/
	private void parseKey(int hashKey,int[] size) {
		//determine the first index
		indexA = 0;
		//while the hashKey is out of bounds of the second dimension
		while (hashKey >= size[1]) {
			//decrease it by the size of the second dimension
			hashKey-= size[1];
			//increase the position in the first dimension
			indexA++;
		}
		//the positionn in the second dimension is the remaining value
		indexB = hashKey;
	}
}