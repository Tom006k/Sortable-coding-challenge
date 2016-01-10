import tom.data.LinkedList;
import tom.data.HashTable;
import tom.string.json.JSONDocument;
import tom.string.json.JSONData;

import java.io.IOException;

/**
*** Main class
*** Accepts 3 arguments: a file containing 3rd party product listings, a file to output the match results to, and file containing known products
*** Compares the product listings against the known products list, and outputs the matches into the output file. Input and output is in JSON.
**/

public class Main {
	///The default file to read known products from.
	private static final String defaultProductsFile = "products.txt";
	///The default file to output the results into.
	private static final String defaultMatchesFile = "matches.txt";
	///The array to contain the known products.
	private SortableProduct[] products;
	///The array to contain the 3rd party product listings.
	private SortableListing[] listings;
	///The match types in order of accuracy and therefore priority.
	private SortableMatchResult.MatchType[] matchTypes = {
			SortableMatchResult.MatchType.FULL_MATCH,
			SortableMatchResult.MatchType.STARTS_WITH_MATCH,
			SortableMatchResult.MatchType.CONTAINS_MATCH,
			SortableMatchResult.MatchType.FULL_PARTIAL_MATCH,
			SortableMatchResult.MatchType.STARTS_WITH_PARTIAL_MATCH,
			SortableMatchResult.MatchType.CONTAINS_PARTIAL_MATCH,
	};
	/** Main method. **/
	public static void main(String[] args) {
		//if there are no arguments
		if (args.length == 0) {
			//output the syntax showing that at least one argument is mandatory
			System.out.println("Insufficient parameters. 3rd party product listing information is required.\r\nSyntax: <3rd party product file> [Optional: <results output file> <known products file>]. Default known products file: \""+defaultProductsFile+"\"");
		}
		//else if there are arguments
		else {
			//set the 3rd party product listings file to the first argument
			String listingsFile = args[0];
			//if there is a second argument, set it as the output file, else set the output file as the default output file prefixed with the listing file
			String matchesFile = ( args.length >= 2 ? args[1] : listingsFile+"_"+defaultMatchesFile );
			//if there is a third argument, set it as the known product file, else use the default product file
			String productsFile = ( args.length >= 3 ? args[2] : defaultProductsFile );
			//initialise the program with the determined file locations
			new Main(listingsFile,matchesFile,productsFile);
		}
	}
	/** Constructs a new Main object to run the program. **/
	public Main(String listingsFile,String matchesFile,String productsFile) {
		System.out.println("executing...");
		long c = System.currentTimeMillis();
		//if the JSON files for the product listings and known products is read successfully
		if (readJSONFiles(listingsFile,defaultProductsFile)) {
			//get the match results as a JSON document
			JSONDocument jsonMatches = getMatchList();
			//anticipate IO errors
			try {
				//attempt to write the JSON text to the output file
				jsonMatches.writeToFile(matchesFile,JSONDocument.WriteOption.SINGLE_LINE_OBJECTS);
			}
			//catch IO errors
			catch(IOException e) {
				//output the error to the console
				e.printStackTrace();
				System.out.println("An IO error occurred.");
			}
			System.out.println("complete. Time taken: "+((System.currentTimeMillis()-c)/1000)+"secs.");
		}
		//else an error occurred
		else {
			//output that an error occurred
			System.out.println("An IO error occurred.");
		}
	}
	/** Attempts to read the JSON files for the known products and 3rd party product listings, and store the items in global arrays, returning true if successful and false for IO errors. **/
	private boolean readJSONFiles(String listingsFile,String productsFile) {
		//declare JSONDocument objects for the two files
		JSONDocument jsonProducts;
		JSONDocument jsonListings;
		//anticipate IO errors
		try {
			//attempt to load the data into the JSONDocument objects
			jsonProducts = new JSONDocument(productsFile);
			jsonListings = new JSONDocument(listingsFile);
		}
		//catch IO errors
		catch(IOException e) {
			//output the error and return false to indicate failure
			e.printStackTrace();
			return false;
		}
		//initialise the known products array to the appropriate size
		products = new SortableProduct[jsonProducts.getChildCount()];
		//create an array of the fields known products have and any additional fields listings have that are relevant for comparison
		String[] fields = {"product_name","manufacturer","model","family","announced-date","currency","price","title"};
		//for each known product
		for (int i = 0; i != jsonProducts.getChildCount(); i++) {
			//get the nth product from the JSON document
			JSONData data = jsonProducts.getData(i);
			//create a new String array with an element for each potential field
			String[] value = new String[fields.length];
			//for each field
			for (int ii = 0; ii != fields.length-1; ii++) {
				//attempt to get the nth field data from the JSON object
				JSONData d = data.getData(fields[ii]);
				//if the data exists, store it in the array, else ensure it is set to null
				value[ii] = ( d == null ? null : d.getValue() );
			}
			//create a SortableProduct object for the read product and store it in the known products array at the nth index
			products[i] = new SortableProduct(value[0],value[1],value[2],value[3],value[4]);
		}
		//initialise the 3rd party product listings array to the appropriate size
		listings = new SortableListing[jsonListings.getChildCount()];
		//for each product listing
		for (int i = 0; i != jsonListings.getChildCount(); i++) {
			//get the nth listing from the JSON document
			JSONData data = jsonListings.getData(i);
			//create a new Stromg arrau witth an element for each potential field
			String[] value = new String[fields.length];
			//for each field
			for (int ii = 0; ii != fields.length; ii++) {
				//attempt to get the nth field data from the JSON object
				JSONData d = data.getData(fields[ii]);
				//if the data exists, store it in the array, else ensure it is set to null
				value[ii] = ( d == null ? null : d.getValue() );
			}
			//create a SortableListing object for the read listing and store it in the listings array at the nth index
			listings[i] = new SortableListing(value[0],value[1],value[2],value[3],value[4],value[5],value[6],value[7]);
		}
		//if this point is reached, all completed successfully; return true
		return true;
	}
	/** Returns a HashTable containing a JSONData object for each known product. **/
	private HashTable<JSONData> getResultTable() {
		//create the hash table of size 25% greater than the number of known products
		HashTable<JSONData> table = new HashTable<JSONData>((int)(products.length*1.25));
		//for each product
		for (int i = 0; i != products.length; i++) {
			//create a new JSONData object of type object
			JSONData data = new JSONData(JSONData.Type.OBJECT);
			//create a JSONData object for the product name field and set the value and type
			JSONData productField = new JSONData("product_name",products[i].getName(),JSONData.Type.STRING);
			//create a JSONData object for the listing array and set the type to array
			JSONData listingArray = new JSONData(JSONData.Type.ARRAY);
			//set the name of the listings array
			listingArray.setName("listings");
			//add the product field and listings array to the first JSONData object
			data.addChild(productField);
			data.addChild(listingArray);
			//add the first JSONData object to the table
			table.add(products[i].getName(),data);
		}
		//return the table
		return table;
	}
	/** Returns the match results as a JSONDocument. **/
	private JSONDocument getMatchList() {
		//for performance testing
	//	System.out.println("Starting compare...");
	//	long c = System.currentTimeMillis();
		//get the JSONData for each known product to add the matching listings to
		HashTable<JSONData> resultTable = this.getResultTable();
		//create the SortableProductMatcher object for each known product separately to avoid carrying out the same String operations on the same data multiple times
		SortableProductMatcher[] productMatcher = new SortableProductMatcher[products.length];
		//for each product
		for (int i = 0; i != products.length; i++) {
			//create a new SortableProductMatcher object for the nth element in the array
			productMatcher[i] = new SortableProductMatcher();
			//set the product for the matcher
			productMatcher[i].setProduct(products[i]);
		}
		//for each 3rd party product listing
		for (int i = 0; i != listings.length; i++) {
			//create a new LinkedList for the matcher objects containing matches
			LinkedList<SortableProductMatcher> matcherList = new LinkedList<SortableProductMatcher>();
			//for each known product
			for (int ii = 0; ii != products.length; ii++) {
				//set the listing for the matcher
				productMatcher[ii].setListing(listings[i]);
				//carry out the comparison and get the match result
				SortableMatchResult result = productMatcher[ii].getMatchResult();
				//if there is a match
				if (result.getMatchType() != SortableMatchResult.MatchType.NO_MATCH) {
					//add the matcher object to the list of matcher objects containing matches
					matcherList.add(productMatcher[ii]);
				}
			}
			//if the matcher list is not 0
			if (matcherList.getSize() != 0) {
				//each listing may only have one matching product, so find the best match
				//get the list as an array
				SortableProductMatcher[] matcherArray = new SortableProductMatcher[matcherList.getSize()];
				matcherList.toArray(matcherArray);
				//name the loop so it can be broken from within a nested loop
				findBestMatch:
				//for each match type
				for (int ii = 0; ii != matchTypes.length; ii++) {
					//get the match type that equates to the priorty level of n, where 0 is highest
					SortableMatchResult.MatchType matchType = getPriorityMatchType(ii);
					//for each matcher object
					for (int iii = 0; iii  != matcherArray.length; iii++) {
						//get the match result for the 'iii'th match
						SortableMatchResult result = matcherArray[iii].getMatchResult();
						//if the match type is of the nth priority
						if (result.getMatchType() == matchType) {
							//get the known product and listing objects used in the matcher
							SortableProduct product = matcherArray[iii].getProduct();
							SortableListing listing = matcherArray[iii].getListing();
							//create a string for the field name, and a string for the value
							String name = "title";
							String value = listing.getTitle();
							//if the value is null
							if (value == null) {
								//change the name to name, and get the name
								name = "name";
								value = listing.getName();
							}
							//create a new JSONData object of type object for the element in the listings array
							JSONData object = new JSONData(JSONData.Type.OBJECT);
							//create and add a new JSONData object to the element, containing the name value and of data type string
							object.addChild(new JSONData(name,value,JSONData.Type.STRING));
							//create and add new JSONData objects to the element for other fields in the listing
							object.addChild(new JSONData("manufacturer",listing.getManufacturer(),JSONData.Type.STRING));
							object.addChild(new JSONData("currency",listing.getCurrency(),JSONData.Type.STRING));
							object.addChild(new JSONData("price",listing.getPrice(),JSONData.Type.STRING));
							//add the element to the listings array for the product in the result table
							resultTable.get(product.getName()).getData("listings").addChild(object);
							//exit the best match loop
							break findBestMatch;
						}
					}
				}
			}
		}
		//performance testing purposes
	//	System.out.println("Finished comparing. Time taken: "+(( System.currentTimeMillis() - c ) / 1000)+" seconds");
		//create a new JSONDocument to contain the JSONData objects, and add the results to it
		JSONDocument jsonMatches = new JSONDocument();
		//for each product
		for (int i = 0; i != products.length; i++) {
			//add the JSONData to the JSONDocument
			jsonMatches.addChild(resultTable.get(products[i].getName()));
		}
		//return the JSONDocument containing the match output
		return jsonMatches;
	}
	/** Returns the SortableMatchResult match type corresponding to the given priority level where 0 = highest. **/
	private SortableMatchResult.MatchType getPriorityMatchType(int priority) {
		return ( priority < matchTypes.length ? matchTypes[priority] : SortableMatchResult.MatchType.NO_MATCH );
	}
}