/**
*** SortableProduct class
*** Represents a product and has fields for each potential field of a "known product".
**/

public class SortableProduct {
	///The name of the product.
	private String name;
	///The manufacturer of the product.
	private String manufacturer;
	///The model of the product.
	private String model;
	///The model family of the product.
	private String family;
	///The product announced date.
	private String announcedDate;
	/** Constructs a new SortableProduct. **/
	public SortableProduct() {
	}
	/** Constructs a new SortableProduct with the given field values. **/
	public SortableProduct(String name,String manufacturer,String model,String family,String announcedDate) {
		setName(name);
		setManufactuer(manufacturer);
		setModel(model);
		setFamily(family);
		setAnnouncedDate(announcedDate);
	}
	/** Returns the product name. **/
	public String getName() {
		return this.name;
	}
	/** Sets the product name. **/
	public void setName(String name) {
		this.name = name;
	}
	/** Returns the product manufacturer. **/
	public String getManufacturer() {
		return this.manufacturer;
	}
	/** Sets the product manufacturer. **/
	public void setManufactuer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	/** Returns the product model. **/
	public String getModel() {
		return this.model;
	}
	/** Sets the product model. **/
	public void setModel(String model) {
		this.model = model;
	}
	/** Returns the family. **/
	public String getFamily() {
		return this.family;
	}
	/** Sets the family. **/
	public void setFamily(String family) {
		this.family = family;
	}
	/** Returns the announced date. **/
	public String getAnnouncedDate() {
		return this.announcedDate;
	}
	/** Sets the announced date. **/
	public void setAnnouncedDate(String announcedDate) {
		this.announcedDate = announcedDate;
	}
}