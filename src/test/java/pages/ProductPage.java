package pages;

import com.microsoft.playwright.Page;

public class ProductPage {
    private final Page page;

    public ProductPage(Page page) { this.page = page; }

    public String title() {
        return page.locator("div.product-name h1").innerText();
    }

    public void addToCart() {
        page.locator("input[value='Add to cart']").first().click();
        // ждём, что нотификация появится (Playwright сам ждёт клика, но лучше явно)
        page.locator("div.bar-notification.success").waitFor();
        // закрыть уведомление, чтобы не мешало
        page.locator("div.bar-notification.success span.close").click();
    }
}