package tom.string.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import tom.data.LinkedList;

/**
***	JSONDocument class
***	Stores a JSON document in a structure making its objects and values easily accessible.
**/

public class JSONDocument extends JSONData {
	/**
	*** WriteOption enum
	*** Contains values to select write options.
	**/
	public enum WriteOption {
		SINGLE_LINE_OBJECTS,
		MULTI_LINE_OBJECTS
	}
	///The file the XML document is of.
	private File file;
	/** Constructs a new JSONDocument. **/
	public JSONDocument() {
		super.setDataType(JSONData.Type.ARRAY);
	}
	/** Constructs a new JSONDocument for the given file. **/
	public JSONDocument(File file) throws IOException {
		this();
		this.file = file;
		parse();
	}
	/** Constructs a new JSONDocument for the given file. **/
	public JSONDocument(String file) throws FileNotFoundException, IOException {
		this(new File(file));
	}
	/** Returns the file the JSONDocument was constructed from. **/
	public File getFile() {
		return this.file;
	}
	/** Parses the file to create the JSON document objects. **/
	private void parse() throws IOException {
		//create a buffered reader for the file
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(file));
		int ascii = 0; //stores the read code
		char c; //stores the character read
		char prevDelimiter = '\0'; //stores the previous delimiter read
		String name = null; //object or field name
		String value = null; //value of a field
		JSONData parent = this; //the current parent
		//while there are characters to read
		while ((ascii = reader.read()) != -1) {
			ascii = consumeWhitespaces(ascii,reader);
			StringBuilder stringBuilder = new StringBuilder();
			//get the character
			c = (char)ascii;
			//if the character is not an object or array delimiter
			if (c != '{' && c != '}' && c != '[' && c != ']' && c != ',') {
				boolean isString = false;
				boolean isEscape = false;
				//seek a name
				//while there are characters to read
				do {
					//get the character
					c = (char)ascii;
					//if the pointer is not inside quotes and the character is a whitespace
					if (!isString && isWhitespace(ascii)) {
						//consume the whitespaces
						ascii = consumeWhitespaces(ascii,reader);
						c = (char)ascii;
					}
					//if it is a quote and the escape flag is not on
					if (c == '"' && !isEscape) {
						isString = !isString; //invert the isString flag
						stringBuilder.append("\""); //append the quote
					}
					//if it is not in a string and it is the element name delimiter
					else if (!isString && c == ':') {
						//set the name
						name = stringBuilder.toString();
						//exit the loop
						break;
					}
					//else if it is not in a string and it is a value, array or object delimiter
					else if (!isString && (c == ',' || c == ']' || c == '}')) {
						//set the value
						value = stringBuilder.toString();
						//exit the loop
						break;
					}
					//else append the character to the string
					else {
						stringBuilder.append(c);
						//if it is a backslash or the escape flag is on
						if (c == '\\' || isEscape) {
							//invert the escape character flag
							isEscape = !isEscape;
						}
					}
				}
				while ((ascii = reader.read()) != -1);
			}
			//if the character is the terminating delimiter of a field
			if (c == ',' && prevDelimiter != '}' && prevDelimiter != ']') {
				//ensure the name is valid for the data structure
				name = getValidName(name);
				//determine the data type
				JSONData.Type dataType;
				//get the data type for the value
				dataType = JSONData.getDataType(value);
				//if it is a string, remove the quotes
				if (dataType == JSONData.Type.STRING) { value = removeQuotes(value); }
				//create a JSONData for the new field
				JSONData object = new JSONData(name,value,dataType);
				//System.out.println(name+" : "+value+",\r\n"); //debug
				//reset the name and value because they have now been used
				name = null;
				value = null;
				//if the parent is null, there is a problem with the markup
				if (parent == null) { parent = this; } //proceed anyway
				//add this new field as a child
				parent.addChild(object);
				prevDelimiter = c;
			}
			//else if the character is the beginning or ending delimiter of an object
			else if (c == '{' || c == '}') {
				if (c == '{') {
					//ensure the name is valid for the data structure
					name = getValidName(name);
					//create a JSONData for the new object
					JSONData object = new JSONData();
					object.setName(name);
					object.setDataType(JSONData.Type.OBJECT);
					//System.out.println(name+" {\r\n"); //debug
					//reset the name because it has now been used
					name = null;
					//if the parent is null, there is a problem with the markup
					if (parent == null) { parent = this; } //proceed anyway
					//add this new object as a child
					parent.addChild(object);
					//set the new parent
					parent = object;
				}
				if (c == '}') {
					//if there are items to add
					if (name != null || value != null) {
						//ensure the name is valid for the data structure
						name = getValidName(name);
						//determine the data type
						JSONData.Type dataType;
						//get the data type for the data
						dataType = JSONData.getDataType(value);
						//if it is a string, remove the quotes
						if (dataType == JSONData.Type.STRING) { value = removeQuotes(value); }
						//create a JSONData for the item
						JSONData object = new JSONData(name,value,dataType);
						//System.out.println(name+" : "+value+"\r\n}\r\n"); //debug
						//reset the name and value because they have now been used
						name = null;
						value = null;
						//add this new object as a child
						parent.addChild(object);
					}
					//returning to the previous level
					//if the parent is null, there is a problem with the markup
					if (parent == null) { parent = this; } //proceed anyway
					else { parent = parent.getParent(); }
				}
				prevDelimiter = c;
			}
			//else if the character is the beginning or ending delimiter of an array
			else if (c == '[' || c == ']') {
				if (c == '[') {
					//ensure the name is valid for the data structure
					name = getValidName(name);
					//create a JSONData for the new Array
					JSONData object = new JSONData();
					object.setName(name);
					object.setDataType(JSONData.Type.ARRAY);
					//System.out.println(name+" [\r\n"); //debug
					//reset the name because it has now been used
					name = null;
					//if the parent is null, there is a problem with the markup
					if (parent == null) { parent = this; } //proceed anyway
					//add this new object as a child
					parent.addChild(object);
					//set the new parent
					parent = object;
				}
				if (c == ']') {
					//if there are items to add
					if (name != null || value != null) {
						//ensure the name is valid for the data structure
						name = getValidName(name);
						//determine the data type
						JSONData.Type dataType;
						//get the data type for the data
						dataType = JSONData.getDataType(value);
						//if it is a string, remove the quotes
						if (dataType == JSONData.Type.STRING) { value = removeQuotes(value); }
						//create a JSONData for the item
						JSONData object = new JSONData(name,value,dataType);
						object.setDataType(JSONData.Type.ARRAY);
						//System.out.println(name+" : "+data+"\r\n]\r\n"); //debug
						//reset the name and value because they have now been used
						name = null;
						value = null;
						//add this new object as a child
						parent.addChild(object);
					}
					//returning to the previous level
					//if the parent is null, there is a problem with the markup
					if (parent == null) { parent = this; } //proceed anyway
					else { parent = parent.getParent(); }
				}
				prevDelimiter = c;
			}
			//else set the previous delimiter to null
			else {
				prevDelimiter = '\0';
			}
		}
		//close the reader
		reader.close();
	}
	/** Returns the next non-whitespace character in the buffer. **/
	private int consumeWhitespaces(int ascii,BufferedReader reader) throws IOException {
		do {
			if (!isWhitespace(ascii)) { return ascii; }
		}
		while ((ascii = reader.read()) != -1);
		return -1;
	}
	/** Returns the given name as a valid name. **/
	private String getValidName(String name) {
		//if the name is null, create a virtual key
		//if (name == null) {
		//	name = "!"+vkey;
		//	vkey++;
		//}
		//else remove its quotes
		//else {
		if (name != null) {
			name = removeQuotes(name);
		}
		return name;
	}
	/** Returns whether the given int equates to a whitespace character. **/
	public boolean isWhitespace(int ascii) {
		switch(ascii) {
			case 9: break; //tab
			case 10: break; //return
			case 13: break; //linefeed
			case 32: break; //space
			case 64: break; //another space
			default: return false;
		}
		return true;
	}
	/** Returns the string without the quotes identifying it as a string type. **/
	private String removeQuotes(String name) {
		//if it has quotes
		if (JSONData.hasQuotes(name)) {
			//remove them
			name = name.substring(1,name.length()-1);
		}
		return name;
	}
	/** Writes the JSON document to the given file. (Convenience method) **/
	public void writeToFile(String file) throws IOException {
		writeToFile(new File(file),JSONDocument.WriteOption.SINGLE_LINE_OBJECTS);
	}
	/** Writes the JSON document to the given file. **/
	public void writeToFile(String file,JSONDocument.WriteOption writeOption) throws IOException {
		writeToFile(new File(file),writeOption);
	}
	/** Writes the JSON document to the given file. (Convenience method) **/
	public void writeToFile(File file) throws IOException {
		this.writeToFile(file,JSONDocument.WriteOption.SINGLE_LINE_OBJECTS);
	}
	/** Writes the JSON document to the given file. **/
	public void writeToFile(File file,JSONDocument.WriteOption writeOption) throws IOException {
		//create a BufferedWriter
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		//if the document contains objects
		if (super.getChildCount() != 0) {
			//get the indexes of those objects
			JSONData[] array = super.getArray();
			JSONData previous = null;
			//for each item
			for (int i = 0; i != array.length; i++) {
				//if this is not the first item
				if (i != 0) {
					//if the previous item has children
					if (previous != null && previous.getChildCount() != 0) {
						//if the type allows it to have children (if it doesn't, the children wouldn't have been written)
						if (previous.getDataType() == JSONData.Type.OBJECT || previous.getDataType() == JSONData.Type.ARRAY) {
							//add a comma before the next item
							writer.write(",");
						}
					}
					//add a new line for readability
					writer.write("\r\n");
				}
				//write it to the file
				array[i].write(writer,writeOption);
				previous = array[i];
			}
		}
		//finish with a new line
		writer.write("\r\n");
		//close the writer
		writer.close();
	}
	/** Override to prevent JSONDocument being any other data type **/
	public void setDataType(JSONData.Type dataType) {
		super.setDataType(JSONData.Type.ARRAY);
	}
}