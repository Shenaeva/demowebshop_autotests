package pages;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartPage extends BasePage {

    private static final String CART_ITEMS = "table.cart tr.cart-item-row";
    private static final String REMOVE_CHECKBOX = "input[name='removefromcart']";
    private static final String UPDATE_CART_BUTTON = "input[name='updatecart']";
    private static final String EMPTY_CART_MESSAGE = ".order-summary-content";

    public CartPage(Page page) {
        super(page);
    }

    public CartPage waitUntilOpened() {
        waitForUrlContains("/cart");
        return this;
    }

    public int itemsCount() {
        return locator(CART_ITEMS).count();
    }

    public boolean hasItems() {
        return itemsCount() > 0;
    }

    public boolean isEmpty() {
        return itemsCount() == 0;
    }

    public String emptyCartMessage() {
        return text(EMPTY_CART_MESSAGE);
    }

    public CartPage removeFirstItem() {
        int beforeCount = itemsCount();

        locator(REMOVE_CHECKBOX).first().check();
        click(UPDATE_CART_BUTTON);

        if (beforeCount > 1) {
            assertThat(locator(CART_ITEMS)).hasCount(beforeCount - 1);
        } else {
            assertThat(locator(CART_ITEMS)).hasCount(0);
        }

        return this;
    }
}