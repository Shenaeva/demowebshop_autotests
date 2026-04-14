package pages;

import com.microsoft.playwright.Page;

public class ProductsPage extends BasePage {

    private static final String PRODUCT_CARDS = ".product-grid .product-item";
    private static final String FIRST_PRODUCT_LINK = ".product-grid .product-item h2.product-title a";
    private static final String PAGE_TITLE = "div.page-title";

    public ProductsPage(Page page) {
        super(page);
    }

    public ProductPage openFirstProductFromGrid() {
        click(FIRST_PRODUCT_LINK);
        return new ProductPage(page);
    }

    public boolean isSearchPageOpened() {
        return page.url().contains("search");
    }

    public int resultsCount() {
        return locator(PRODUCT_CARDS).count();
    }

    public boolean hasResults() {
        return resultsCount() > 0;
    }

    public String titleText() {
        return text(PAGE_TITLE);
    }
}