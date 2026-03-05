package pages;

import com.microsoft.playwright.Page;

public class ProductsPage {
    private final Page page;

    public ProductsPage(Page page) { this.page = page; }

    public void openFirstProductFromGrid() {
        page.locator(".product-grid .product-item h2.product-title a").first().click();
    }

    public int resultsCount() {
        return page.locator(".product-grid .product-item").count();
    }
}