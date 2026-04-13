package pages;

import com.microsoft.playwright.Page;

public class ProductsPage extends BasePage {

    private static final String PRODUCT_CARDS = ".product-grid .product-item";
    private static final String FIRST_PRODUCT_LINK = ".product-grid .product-item h2.product-title a";

    public ProductsPage(Page page) {
        super(page);
    }

    public ProductPage openFirstProductFromGrid() {
        click(FIRST_PRODUCT_LINK);
        return new ProductPage(page);
    }

    public int resultsCount() {
        return locator(PRODUCT_CARDS).count();
    }
}