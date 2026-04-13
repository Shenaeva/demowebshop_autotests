package pages;

import com.microsoft.playwright.Page;

public class CartPage extends BasePage {

    private static final String CART_ITEMS = "table.cart tr.cart-item-row";
    private static final String REMOVE_CHECKBOX = "input[name='removefromcart']";
    private static final String UPDATE_CART_BUTTON = "input[name='updatecart']";

    public CartPage(Page page) {
        super(page);
    }

    public int itemsCount() {
        return locator(CART_ITEMS).count();
    }

    public CartPage removeFirstItem() {
        locator(REMOVE_CHECKBOX).first().check();
        click(UPDATE_CART_BUTTON);
        return this;
    }
}