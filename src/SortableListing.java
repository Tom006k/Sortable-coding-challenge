/**
*** SortableListing class
*** Represents a product listing.
*** A SortableListing is also a SortableProduct, but may contain additional information that might be compared against
*** fields of "known products" to find a match. For example, matching for the product name in a product listing title.
**/

public class SortableListing extends SortableProduct {
	///The title of the listing.
	private String title;
	/** Constructs a new SortableListing. **/
	public SortableListing() {
	}
	/** Constructs a new SortableListing with the given field values. **/
	public SortableListing(String name,String manufacturer,String model,String family,String announcedDate,String title) {
		super(name,manufacturer,model,family,announcedDate);
		this.setTitle(title);
	}
	/** Returns the listing title. **/
	public String getTitle() {
		return this.title;
	}
	/** Sets the listing title. **/
	public void setTitle(String title) {
		this.title = title;
	}
}