/**
*** SortableProductMatcher class
*** Compares a SortableListing against a SortableProduct and yields a SortableMatchResult.
**/

public class SortableProductMatcher {
	///The product listing for comparison.
	private SortableListing listing;
	///The known product for comparison.
	private SortableProduct product;
	///The pattern string created for comparisons for the product name.
	private String productNamePattern;
	/** Constructs a new SortableProductMatcher. **/
	public SortableProductMatcher() {
	}
	/** Constructs a new SortableProductMatcher with the given product listing and known product. **/
	public SortableProductMatcher(SortableListing listing,SortableProduct product) {
		setListing(listing);
		setProduct(product);
	}
	/** Returns the known product. **/
	public SortableProduct getProduct() {
		return this.product;
	}
	/** Sets the known product for comparison. **/
	public void setProduct(SortableProduct product) {
		this.product = product;
		createProductNamePattern();
	}
	/** Creates the product name pattern used for matching. **/
	private void createProductNamePattern() {
		String name = product.getName();
		//if the name is not null
		if (name != null) {
			name = name.toUpperCase();
			String[] fields = {product.getManufacturer(),product.getFamily(),product.getModel()};
			//for each field
			for (int i = 0; i != fields.length; i++) {
				//if it is not null
				if (fields[i] != null) {
					fields[i] = fields[i].toUpperCase();
					//if it is in the product name
					if (name.contains(fields[i])) {
						//remove it
						name = name.replace(fields[i],"");
					}
				}
			}
		}
		//the name string should now be whitespaces or contain key word(s) for matching
		//remove leading and trailing whitespaces and separators
		name = name.replaceAll("(^[_\\-\\s+]|[_\\-\\s]$)","");
		this.productNamePattern = this.getEscaptedPattern(name);
		this.productNamePattern = this.productNamePattern.replaceAll("[_\\-\\s]+","[_\\\\-\\\\s]*");
	}
	/** Returns the given string with special regular expression characters escaped, excluding hyphens because they will be handled separately. **/
	private String getEscaptedPattern(String pattern) {
		return pattern.replaceAll("(\\\\|\\.|\\{|\\}|\\[|\\]|\\*|\\?|\\+|\\^|\\$|\\!|\\(|\\)|\\<|\\>|\\/|\\|)","\\\\$1");
	}
	/** Returns the product listing. **/
	public SortableListing getListing() {
		return this.listing;
	}
	/** Sets the product listing for comparison. **/
	public void setListing(SortableListing listing) {
		this.listing = listing;
	}
	/** Returns whether the known product and product listing match. **/
	public boolean isMatch() {
		return ( getMatchResult().getMatchType() == SortableMatchResult.MatchType.NO_MATCH ? false : true );
	}
	/** Returns a SortableMatchResult object for the match result of the known product and product listing. **/
	public SortableMatchResult getMatchResult() {
		//create a new result object with no match
		SortableMatchResult result = new SortableMatchResult(SortableMatchResult.MatchType.NO_MATCH);
		//compare the known product and product listing
		//if the manufacturer and family match
		if (isManufacturerMatch() && isFamilyMatch()) {
			//if the model matches
			if (isModelMatch()) {
				//if the remaning text in the product name matches
				if ((listing.getTitle() != null && listing.getTitle().toUpperCase().matches(".*"+productNamePattern+".*")) ||
					(listing.getName() != null && listing.getName().toUpperCase().matches(".*"+productNamePattern+".*"))) {
					//set the type to full match
					result.setMatchType(SortableMatchResult.MatchType.FULL_MATCH);
				}
				else {
					//set the match type to partial
					result.setMatchType(SortableMatchResult.MatchType.FULL_PARTIAL_MATCH);
				}
			}
			//else if the model can be matched partially
			else if (isModelPartialMatch()) {
				//set the match type to the lowest level
				result.setMatchType(SortableMatchResult.MatchType.CONTAINS_PARTIAL_MATCH);
			}
		}
		//return the result
		return result;
	}
	/** Returns whether the manufacturers match. **/
	public boolean isManufacturerMatch() {
		//get the manufacturers from the product and listing
		String productManufacturer = product.getManufacturer();
		String listingManufacturer = listing.getManufacturer();
		//if the product manufacturer isn't specified
		if (productManufacturer == null) {
			//it can't be matched
			return false;
		}
		//compare using the same casing
		productManufacturer = productManufacturer.toUpperCase();
		//if the manufacturer can be matched in field
		if (listingManufacturer != null && productManufacturer.equals(listingManufacturer.toUpperCase())) {
			return true;
		}
		else {
			//if the manufacturer can be matched in title or name
			if (contains(listing.getTitle(),productManufacturer) || contains(listing.getName(),productManufacturer)) {
				return true;
			}
		}
		//else return false
		return false;
	}
	/** Returns whether the families match. **/
	public boolean isFamilyMatch() {
		//get the families from the product and listing
		String productFamily = product.getFamily();
		String listingFamily = listing.getFamily();
		//if the product family isn't specified
		if (productFamily == null) {
			//it can't be matched
			return false;
		}
		//compare using the same casing
		productFamily = productFamily.toUpperCase();
		//if the family can be matched in field, title or name
		if (listingFamily != null && productFamily.equals(listingFamily.toUpperCase())) {
			return true;
		}
		else {
			//if the family can be matched in title or name
			if (contains(listing.getTitle(),productFamily) || contains(listing.getName(),productFamily)) {
				//return true
				return true;
			}
		}
		//else return false
		return false;
	}
	/** Returns whether the models match. **/
	public boolean isModelMatch() {
		//get the models form the product and listing
		String productModel = product.getModel();
		String listingModel = listing.getModel();
		//if the product model is not specified
		if (productModel == null) {
			//it can't be matched
			return false;
		}
		//compare using the same casing
		productModel = productModel.toUpperCase();
		//if the model can be matched in field, title or name
		if (listingModel != null && productModel.equals(listingModel.toUpperCase())) {
			return true;
		}
		else {
			String forPattern = "^.*[_\\-\\s]FOR[_\\-\\s](?:.*[_\\-\\s])?"+productModel+"(?:[_\\-\\s\\,].*|$)";
			//if the model can be matched in title or name
			if ((contains(listing.getTitle(),productModel) && !listing.getTitle().toUpperCase().matches(forPattern)) ||
				(contains(listing.getName(),productModel) && !listing.getName().toUpperCase().matches(forPattern))) {
				//return true
				return true;
			}
		}
		//else return false
		return false;
	}
	/** Returns whether the models match partially. **/
	public boolean isModelPartialMatch() {
		//get the product model
		String productModel = product.getModel();
		//if it is not specified
		if (productModel == null) {
			//it can't be matched
			return false;
		}
		//compare using the same casing
		productModel = productModel.toUpperCase();
		//get the title and name of the listing for comparison
		String[] subjects = {listing.getTitle(),listing.getName()};
		//for each
		for (int i = 0; i != subjects.length; i++) {
			//if the subject is not null
			if (subjects[i] != null) {
				//compare using the same casing
				subjects[i] = subjects[i].toUpperCase();
				//split the product model into tokens by whitespace, dash and underscore separators
				String[] tokens = productModel.split("[_\\-\\s]+");
				//for each token
				for (int ii = 0; ii != tokens.length; ii++) {
					tokens[ii] = tokens[ii].toUpperCase();
					//if the subject contains the token, and is not preceded by "for" indicating a different product for use with this product
					if (contains(subjects[i],tokens[ii]) && !subjects[i].matches("^.*[_\\-\\s]FOR[_\\-\\s](?:.*[_\\-\\s])?"+tokens[ii]+"(?:[_\\-\\s,].*|$)")) {
						//partial match found
						return true;
					}
				}
			}
		}
		//no partial match
		return false;
	}
	/** Returns whether the subject string contains the search string. **/
	private boolean contains(String subject,String search) {
		//if the subject is null
		if (subject == null) {
			return false;
		}
		//compare using the same case
		subject = subject.toUpperCase();
		//create an array of allowed boundary characters
		char[] boundary = {32,'_','-'};
		//for each boundary character
		for (int i = 0; i != boundary.length; i++) {
			//for each boundary character again
			for (int ii = 0; ii != boundary.length; ii++) {
				//if the subject contains the search string preceded by the boundary character of the outer loop and followed by the boundary character of this inner loop
				if (subject.contains(boundary[i]+search+boundary[ii])) {
					return true;
				}
			}
			//if the subject starts or ends with the search string and is followed by or preceded respectively by the boundary character
			if (subject.startsWith(search+boundary[i]) || subject.endsWith(boundary[i]+search)) {
				return true;
			}
		}
		//no match found
		return false;
	}
}