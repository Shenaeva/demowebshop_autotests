package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage extends BasePage {

    private static final String LOGIN_LINK = "a.ico-login";
    private static final String REGISTER_LINK = "a.ico-register";
    private static final String SEARCH_INPUT = "#small-searchterms";
    private static final String SEARCH_BUTTON = "input[value='Search']";
    private static final String CART_QTY = "span.cart-qty";
    private static final String CART_LINK = "a.ico-cart";
    private static final String LOGOUT_LINK = "a.ico-logout";
    private static final String ACCOUNT_LINK = ".account";

    public HomePage(Page page) {
        super(page);
    }

    public LoginPage openLoginPage() {
        click(LOGIN_LINK);
        waitForUrlContains("/login");
        return new LoginPage(page);
    }

    public RegistrationPage openRegistrationPage() {
        click(REGISTER_LINK);
        waitForUrlContains("/register");
        return new RegistrationPage(page);
    }

    public ProductsPage search(String query) {
        fill(SEARCH_INPUT, query);
        click(SEARCH_BUTTON);
        return new ProductsPage(page);
    }

    public int cartQty() {
        String rawText = text(CART_QTY);
        return Integer.parseInt(rawText.replaceAll("[^0-9]", ""));
    }

    public CartPage openCart() {
        click(CART_LINK);
        waitForUrlContains("/cart");
        return new CartPage(page);
    }

    public void logout() {
        click(LOGOUT_LINK);
    }

    public Locator loginLink() {
        return locator(LOGIN_LINK);
    }

    public Locator logoutLink() {
        return locator(LOGOUT_LINK);
    }

    public Locator accountLink() {
        return locator(ACCOUNT_LINK);
    }
}