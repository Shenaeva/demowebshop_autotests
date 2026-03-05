package pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;

    public HomePage(Page page) { this.page = page; }

    public void openLogin() {
        page.locator("a.ico-login").click();
    }

    public void search(String query) {
        page.locator("input#small-searchterms").fill(query);
        page.locator("input[value='Search']").click();
    }

    public int cartQty() {
        // "Shopping cart (0)" -> вытаскиваем число
        String text = page.locator("span.cart-qty").innerText(); // "(0)"
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public void openCart() {
        page.locator("a.ico-cart").click();
    }
}