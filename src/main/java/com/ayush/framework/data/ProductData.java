package com.ayush.framework.data;

/**
 * Known catalogue of the Sauce demo store, used as test data.
 * Keeping expected data in one place means a catalogue change is a one-line fix.
 */
public enum ProductData {

    BLACK_HEELS("Black heels", "flower-print-jeans", "£45.00", 4500, true),
    BRONZE_SANDALS("Bronze sandals", "bronze-sandals", "£39.99", 3999, true),
    BROWN_SHADES("Brown Shades", "brown-shades", "£20.00", 2000, false),
    GREY_JACKET("Grey jacket", "grey-jacket", "£55.00", 5500, true),
    NOIR_JACKET("Noir jacket", "noir-jacket", "£60.00", 6000, true),
    STRIPED_TOP("Striped top", "striped-top", "£50.00", 5000, true),
    WHITE_SANDALS("White sandals", "white-sandals", "£25.00", 2500, false);

    private final String title;
    private final String handle;
    private final String displayPrice;
    private final long priceInPence;
    private final boolean available;

    ProductData(String title, String handle, String displayPrice, long priceInPence, boolean available) {
        this.title = title;
        this.handle = handle;
        this.displayPrice = displayPrice;
        this.priceInPence = priceInPence;
        this.available = available;
    }

    public String title() {
        return title;
    }

    public String handle() {
        return handle;
    }

    public String displayPrice() {
        return displayPrice;
    }

    public long priceInPence() {
        return priceInPence;
    }

    public boolean available() {
        return available;
    }

    @Override
    public String toString() {
        return title;
    }
}
