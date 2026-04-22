package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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
        return new LoginPage(page).waitUntilOpened();
    }

    public RegistrationPage openRegistrationPage() {
        click(REGISTER_LINK);
        waitForUrlContains("/register");
        return new RegistrationPage(page);
    }

    public ProductsPage search(String query) {
        fill(SEARCH_INPUT, query);
        click(SEARCH_BUTTON);
        page.waitForURL(url -> url.contains("search"));
        return new ProductsPage(page);
    }

    public int cartQty() {
        String rawText = text(CART_QTY);
        return Integer.parseInt(rawText.replaceAll("[^0-9]", ""));
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(page).waitUntilOpened();
    }

    public HomePage logout() {
        click(LOGOUT_LINK);
        assertThat(locator(LOGIN_LINK)).isVisible();
        return this;
    }

    public boolean isLoggedIn() {
        return isVisible(LOGOUT_LINK) && isVisible(ACCOUNT_LINK);
    }

    public boolean isLoggedOut() {
        return isVisible(LOGIN_LINK);
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