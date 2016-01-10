/**
*** SortableListing class
*** Represents a product listing.
*** A SortableListing is also a SortableProduct, but may contain additional information that might be compared against
*** fields of "known products" to find a match. For example, matching for the product name in a product listing title.
**/

public class SortableListing extends SortableProduct {
	///The title of the listing.
	private String title;
	///The currency of the listed product.
	private String currency;
	///The price of the listed product.
	private String price;
	/** Constructs a new SortableListing. **/
	public SortableListing() {
	}
	/** Constructs a new SortableListing with the given field values. **/
	public SortableListing(String name,String manufacturer,String model,String family,String announcedDate,String currency,String price,String title) {
		super(name,manufacturer,model,family,announcedDate);
		this.setTitle(title);
		this.setCurrency(currency);
		this.setPrice(price);
	}
	/** Returns the listing title. **/
	public String getTitle() {
		return this.title;
	}
	/** Sets the listing title. **/
	public void setTitle(String title) {
		this.title = title;
	}
	/** Returns the listed product's currency. **/
	public String getCurrency() {
		return this.currency;
	}
	/** Sets the listed product's currency. **/
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/** Returns the listed product's price. **/
	public String getPrice() {
		return this.price;
	}
	/** Sets the listed product's price. **/
	public void setPrice(String price) {
		this.price = price;
	}
}