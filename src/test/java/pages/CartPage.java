package pages;

import com.microsoft.playwright.Page;

public class CartPage {
    private final Page page;

    public CartPage(Page page) { this.page = page; }

    public int itemsCount() {
        return page.locator("table.cart tr.cart-item-row").count();
    }

    public void removeFirstItem() {
        page.locator("input[name='removefromcart']").first().check();
        page.locator("input[name='updatecart']").click();
    }
}