package tom.string.json;

import tom.data.HashTable;
import tom.data.LinkedList;

/**
***	JSONData class
***	Represents a JSON object and its references and or data.
**/

public class JSONData {
	/**
	***	Type enum
	*** Represents the data type of the data.
	**/
	public enum Type {
		BOOLEAN,
		NUMBER,
		STRING,
		OBJECT,
		ARRAY,
		NULL,
		ILEGAL
	};
	///The name of the data.
	private String name;
	///The data object which references this data object.
	private JSONData parent;
	///The data type
	private Type dataType;
	///The child objects of an object data.
	private HashTable<JSONData> unorderedList;
	///The array object for an array data type.
	private LinkedList<JSONData> orderedList;
	///The value of a field value.
	private String value;
	///Counter for virtual key creation.
	private long vkey = 0;
	/** Constructs a new JSONData object. **/
	public JSONData() {
	}
	/** Constructs a new JSONData object with the given value. **/
	public JSONData(String value) {
		setValue(value);
	}
	/** Constructs a new JSONData object of the given type. **/
	public JSONData(Type dataType) {
		setDataType(dataType);
	}
	/** Constructs a new JSONData object of the given type with the given value. **/
	public JSONData(String value,Type dataType) {
		setValue(value);
		setDataType(dataType);
	}
	/** Constructs a new JSONData object with the given value and name. **/
	public JSONData(String name,String value) {
		setName(name);
		setValue(value);
	}
	/** Constructs a new JSONData object of the given type with the given value and name. **/
	public JSONData(String name,String value,Type dataType) {
		setName(name);
		setValue(value);
		setDataType(dataType);
	}
	/** Returns the value as a String. **/
	public String getValue() {
		return value;
	}
	/** Sets the value. **/
	public void setValue(String value) {
		this.value = value;
	}
	/** Sets the data type. **/
	public void setDataType(Type dataType) {
		this.dataType = dataType;
	}
	/** Returns the data type. **/
	public Type getDataType() {
		return this.dataType;
	}
	/** Returns the object name. **/
	public String getName() {
		return name;
	}
	/** Sets the object name. **/
	public void setName(String name) {
		this.name = name;
	}
	/** Returns the parent object - the data object which references this data object. **/
	public JSONData getParent() {
		return parent;
	}
	/** Sets the parent object - data object which references this data object. **/
	public void setParent(JSONData parent) {
		this.parent = parent;
	}
	/** Returns the document this object is a child of. **/
	public JSONDocument getDocument() {
		//start at this object
		JSONData data = this;
		//while the reference is not null
		while (data != null) {
			//if the reference is a document object, return it
			if (data instanceof JSONDocument) { return (JSONDocument)data; }
			//else reference the next parent
			data = data.getParent();
		}
		//if the object is not in a document, return null
		return null;
	}
	/** Adds the given object to the document. **/
	public void addChild(JSONData object) {
		if (this.dataType == Type.OBJECT) {
			addToUnorderedList(object);
		}
		else if (this.dataType == Type.ARRAY) {
			addToOrderedList(object);
		}
		//set this object as its parent
		object.setParent(this);
	}
	/** Adds the given object to the unordered list. **/
	private void addToUnorderedList(JSONData object) {
		//if the list is null
		if (unorderedList == null) {
			//initialize it
			unorderedList = new HashTable<JSONData>(20);
		}
		//get the name to be the key
		String key = object.getName();
		//if the name is null, create a virtual key
		if (key == null) {
			key = "!"+vkey;
			vkey++;
		}
		unorderedList.add(key,object);
	}
	/** Adds the given object to the ordered list. **/
	private void addToOrderedList(JSONData object) {
		//if the list is null
		if (orderedList == null) {
			//initialize it
			orderedList = new LinkedList<JSONData>();
		}
		orderedList.add(object);
	}
	/** Returns the number of children the object has. **/
	public int getChildCount() {
		if (this.dataType == Type.OBJECT && unorderedList != null) {
			return unorderedList.getItemCount();
		}
		else if (this.dataType == Type.ARRAY && orderedList != null) {
			return orderedList.getSize();
		}
		return 0;
	}
	/** Removes the given object from the document. **/
	public void removeChild(JSONData object) {
		if (this.dataType == Type.OBJECT) {
			unorderedList.remove(object.getName());
		}
		else if (this.dataType == Type.ARRAY) {
			orderedList.remove(object);
		}
	}
	/** Removes the object at the given index from the document. **/
	public void removeChild(int i) {
		if (this.dataType == Type.ARRAY) {
			orderedList.remove(i);
		}
	}
	/** Returns the number of parents this data item has. **/
	private int getGenerations() {
		JSONData object = this;
		int gen = 0;
		while ((object = object.getParent()) != null) { gen++; }
		return gen;
	}
	/** Returns the data at the specified index of the unordered list. **/
	public JSONData getData(String name) {
		return unorderedList.get(name);
	}
	/** Returns the field at the given array index. **/
	public JSONData getData(int index) {
		return orderedList.get(index);
	}
	/** Returns the unordered list of child objects. **/
	public HashTable<JSONData> getUnorderedList() {
		return unorderedList;
	}
	/** Returns the ordered list/array of data. **/
	public JSONData[] getArray() {
		JSONData[] array = new JSONData[orderedList.getSize()];
		orderedList.toArray(array);
		return array;
	}
	/** Sets the maintain array property of the linked list managing the array data. **/
	public void setMaintainArray(boolean b) {
		orderedList.setMaintainArray(b);
	}
	/** Returns the status of maintain array feature of the linked list managing the array data. **/
	public boolean isMaintainArray() {
		return ( orderedList == null ? false : orderedList.isMaintainArray() );
	}
	/** Returns the JSON data type for the given value. **/
	public static Type getDataType(String value) {
		if (value != null) {
			if (hasQuotes(value)) {
				return JSONData.Type.STRING;
			}
			else if (value.matches("^\\d+$")) {
				return JSONData.Type.NUMBER;
			}
			else if (value.equals("true") || value.equals("false")) {
				return JSONData.Type.BOOLEAN;
			}
			else if (value.equals("null")) {
				return JSONData.Type.NULL;
			}
			else {
				return JSONData.Type.ILEGAL;
			}
		}
		return null;
	}
	/** Returns whether the given string is enclosed in quotes. **/
	public static boolean hasQuotes(String string) {
		if (string.length() < 2) { return false; }
		return (string.charAt(0) == '"' && string.charAt(string.length()-1) == '"');
	}
	/** Returns the JSONData object as a string. **/
	public String toString() {
		//if the name is not null and is not a virtual key, set it
		String name = (this.name != null && !this.name.matches("^!\\d+$") ? "\""+this.name+"\":" : "");
		String dataString;
		//if the data is an object
		if (this.dataType == Type.OBJECT) {
			dataString = "{ }";
		}
		//else if the data is an array
		else if (this.dataType == Type.ARRAY) {
			dataString = " [ ]";
		}
		//else if the data is a string
		else if (this.dataType == Type.STRING) {
			dataString = " \""+value+"\"";
		}
		//else if the data is a boolean, number or null
		else if (this.dataType == Type.BOOLEAN || this.dataType == Type.NUMBER || this.dataType == Type.NULL) {
			dataString = " "+value;
		}
		//else it must be ilegal. return null
		else {
			return null;
		}
		//return the string representation
		return name+dataString;
	}
	/** Returns the JSONData object as a string for writing to a JSONDocument with tabulation. **/
	public String toStringWithIndent() {
		//count parents to set an appropriate indent
		String indent = "";
		//for each generation
		for (int i = getGenerations()-1; i > 0; i--) {
			//append an indent
			indent+= "	";
		}
		//return the string representation indented
		return indent+this.toString();
	}
	/** Writes this data and all child data via recursion using the given BufferedWriter. **/
	protected void write(java.io.BufferedWriter writer,JSONDocument.WriteOption writeOption) throws java.io.IOException {
		boolean isMultiLine = ( writeOption == JSONDocument.WriteOption.MULTI_LINE_OBJECTS );
		//count parents to set an appropriate indent
		String indent = "";
		//if each object is to be written with new lines
		if (isMultiLine) {
			//for each generation
			for (int i = getGenerations()-1; i > 0; i--) {
				//append an indent
				indent+= "	";
			}
			//write the indent
			writer.write(indent);
		}
		//if the name is not null and is not a virtual key, append it
		if (this.name != null && !this.name.matches("^!\\d+$")) { writer.write("\""+this.name+"\": "); }
		//if the data is an object
		if (this.dataType == Type.OBJECT) {
			//open the object definition
			writer.write("{");
			//if the unordered list has been initialised
			if (unorderedList != null) {
				//if each object is to be written with new lines
				if (isMultiLine) {
					//add a new line for readability
					writer.write("\r\n");
				}
				//get the keys for every child object
				String[] recoveredKeys = unorderedList.getKeys();
				//set the previous data to null
				JSONData previousData = null;
				//for each item
				for (int i = 0; i != recoveredKeys.length; i++) {
					//if there is a previous item and it also has data
					if (i != 0 && previousData != null && previousData.toString() != null) {
						//separate the items with a comma for readability
						writer.write(",");
						//if each object is to be written with new lines
						if (isMultiLine) {
							//add a new line for readability
							writer.write("\r\n");
						}
					}
					//get the object at the index
					JSONData object = unorderedList.get(recoveredKeys[i]);
					//if it is a type that can have children
					if (object.getDataType() == JSONData.Type.OBJECT || object.getDataType() == JSONData.Type.ARRAY) {
						//invoke their write methods
						object.write(writer,writeOption);
					}
					//else write them here using their toString methods
					else {
						String dataString = ( isMultiLine ? object.toStringWithIndent() : object.toString());
						//if there is data
						if (dataString != null) {
							writer.write(dataString);
						}
					}
					previousData = object;
				}
				//if each object is to be written with new lines
				if (isMultiLine) {
					//add a new line for readability
					writer.write("\r\n");
				}
			}
			else { writer.write(" "); }
			//if each object is to be written with new lines
			if (isMultiLine) {
				writer.write(indent);
			}
			//close the object definition
			writer.write("}");
		}
		//else if the data is an array
		else if (this.dataType == Type.ARRAY) {
			writer.write("[");
			//if the orderedList has been initialised
			if (orderedList != null) {
				//if each object is to be written with new lines
				if (isMultiLine) {
					//add a new line for readability
					writer.write("\r\n");
				}
				JSONData[] array = this.getArray();
				JSONData previousData = null;
				//for each element in the array
				for (int i = 0; i != array.length; i++) {
					//if there is a previous item and it also has data
					if (i != 0 && previousData != null && previousData.toString() != null) {
						//separate the items with a comma for readability
						writer.write(",");
						//if each object is to be written with new lines
						if (isMultiLine) {
							//add a new line for readability
							writer.write("\r\n");
						}
					}
					//if it is an object or array type, invoke its write method
					if (array[i].getDataType() == JSONData.Type.OBJECT || array[i].getDataType() == JSONData.Type.ARRAY) {
						//write it
						array[i].write(writer,writeOption);
					}
					//else use its toString method
					else {
						String dataString = ( isMultiLine ? array[i].toStringWithIndent() : array[i].toString());
						//if there is data
						if (dataString != null) {
							writer.write(dataString);
						}
					}
					previousData = array[i];
				}
				//if each object is to be written with new lines
				if (isMultiLine) {
					//add a new line for readability
					writer.write("\r\n");
				}
			}
			//if each object is to be written with new lines
			if (isMultiLine) {
				writer.write(indent);
			}
			//close the array
			writer.write("]");
		}
		/*redundant code//else if the data is any other legal type
		else if (this.dataType == Type.STRING || this.dataType == Type.BOOLEAN || this.dataType == Type.NUMBER || this.dataType == Type.NULL) {
			String dataString = this.toString();
			if (dataString != null) {
				dataString+=",\r\n";
				writer.write(dataString);
			}
		}*/
	}
}