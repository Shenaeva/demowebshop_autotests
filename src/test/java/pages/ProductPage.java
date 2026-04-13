package pages;

import com.microsoft.playwright.Page;

public class ProductPage extends BasePage {

    private static final String PRODUCT_NAME = "div.product-name h1";
    private static final String ADD_TO_CART_BUTTON = "input[value='Add to cart']";
    private static final String SUCCESS_NOTIFICATION = "div.bar-notification.success";
    private static final String SUCCESS_NOTIFICATION_CLOSE = "div.bar-notification.success span.close";

    public ProductPage(Page page) {
        super(page);
    }

    public String title() {
        return text(PRODUCT_NAME);
    }

    public ProductPage addToCart() {
        locator(ADD_TO_CART_BUTTON).first().click();
        locator(SUCCESS_NOTIFICATION).waitFor();
        return this;
    }

    public boolean isSuccessNotificationVisible() {
        return isVisible(SUCCESS_NOTIFICATION);
    }

    public void closeSuccessNotification() {
        if (isVisible(SUCCESS_NOTIFICATION_CLOSE)) {
            click(SUCCESS_NOTIFICATION_CLOSE);
        }
    }
}