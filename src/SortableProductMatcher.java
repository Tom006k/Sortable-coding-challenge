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
		this.productNamePattern = this.getEscaptedPattern(product.getName().toUpperCase());
		this.productNamePattern = this.productNamePattern.replaceAll("[_\\-\\s]+","[_\\\\-\\\\s]*");
		this.productNamePattern = this.productNamePattern.replaceAll("(?:\\[_\\\\-\\\\s\\]\\*)?"+product.getManufacturer()+"(?:\\[_\\\\-\\\\s\\]\\*)?","[_\\\\-\\\\s]*(?:"+product.getManufacturer()+")?[_\\\\-\\\\s]*");
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
		//check that the manufacturers are the same
		if (listing.getManufacturer().equals(product.getManufacturer())) {
			//if it is a full match
			if (isFullMatch()) {
				//set the result type to full match
				result.setMatchType(SortableMatchResult.MatchType.FULL_MATCH);
			}
			//else if the listing starts with the product name
			else if (isStartsWithMatch()) {
				//set the result type to starts with
				result.setMatchType(SortableMatchResult.MatchType.STARTS_WITH_MATCH);
			}
			//else if the listing contains the product name
			else if (listingContainsProduct()) {
				//set the result type to contains
				result.setMatchType(SortableMatchResult.MatchType.CONTAINS_MATCH);
			}
			//other match types are unsuitable because they would create "relevant" matches, but less accurate ones
		}
		//return the result
		return result;
	}
	/** Returns the given string with special regular expression characters escaped, excluding hyphens because they will be handled separately. **/
	private String getEscaptedPattern(String pattern) {
		return pattern.replaceAll("(\\\\|\\.|\\{|\\}|\\[|\\]|\\*|\\?|\\+|\\^|\\$|\\!|\\(|\\)|\\<|\\>|\\/|\\|)","\\\\$1");
	}
	/** Returns whether there is a full match. **/
	public boolean isFullMatch() {
		//get the listing name and title
		String name = listing.getName();
		String title = listing.getTitle();
		//if there is a listing title
		if (title != null) {
			//attempt a pure string comparison first because it is faster than regex
			//if it fails, attempt it for the regex equivalent but with the product name pattern string to account for potential differences in word spacing
			//return the result
			return (title.equalsIgnoreCase(product.getName()) || title.toUpperCase().matches("^"+productNamePattern+"$"));
		}
		//else if there is a listing name
		else if (listing.getName() != null) {
			//attempt a pure string comparison first because it is faster than regex
			//if it fails, attempt it for the regex equivalent but with the product name pattern string to account for potential differences in word spacing
			//return the result
			return (name.equalsIgnoreCase(product.getName()) || name.toUpperCase().matches("^"+productNamePattern+"$"));
		}
		//else return false
		return false;
	}
	/** Returns whether there is a starts with match. **/
	public boolean isStartsWithMatch() {
		//if the listing has a title
		if (listing.getTitle() != null) {
			String title = listing.getTitle().toUpperCase();
			//test for the title starting with the product name pattern followed by a whitepsace and anything else, and return the result
			return title.matches("^"+productNamePattern+"\\s.*");
		}
		//else if the listing has a name
		else if (listing.getName() != null) {
			//get the name
			String name = listing.getName().toUpperCase();
			//test for the name starting with the product name pattern followed by a whitepsace and anything else, and return the result
			return name.matches("^"+productNamePattern+"\\s.*");
		}
		//else return false
		return false;
	}
	/** Returns whether there is a contains match. **/
	public boolean listingContainsProduct() {
		//if the listing has a title
		if (listing.getTitle() != null) {
			//get the title
			String title = listing.getTitle().toUpperCase();
			//test for the name contianing the product name pattern with a whitespace or end-of-string delimiter at either end, and return the result
			return title.matches(".*(?:^|\\s*)"+productNamePattern+"(?:\\s.*|$)");
		}
		//else if the listing has a name
		else if (listing.getName() != null) {
			//get the name
			String name = listing.getName().toUpperCase();
			//test for the name contianing the product name pattern with a whitespace or end-of-string delimiter at either end, and return the result
			return name.matches(".*(?:^|\\s*)"+productNamePattern+"(?:\\s.*|$)");
		}
		//else return false
		return false;
	}
}