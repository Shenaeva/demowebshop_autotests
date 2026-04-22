package pages;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage extends BasePage {

    private static final String EMAIL_INPUT = "#Email";
    private static final String PASSWORD_INPUT = "#Password";
    private static final String LOGIN_BUTTON = "input[value='Log in']";

    private static final String ERROR_MESSAGE = "div.message-error";
    private static final String VALIDATION_SUMMARY = ".validation-summary-errors";
    private static final String EMAIL_ERROR = "[data-valmsg-for='Email']";
    private static final String PASSWORD_ERROR = "[data-valmsg-for='Password']";

    private static final String PAGE_TITLE = "Welcome, Please Sign In!";

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage waitUntilOpened() {
        waitForUrlContains("/login");
        waitForTitle(PAGE_TITLE);
        return this;
    }

    public boolean isOpened() {
        return page.url().contains("/login");
    }

    public LoginPage fillEmail(String email) {
        fill(EMAIL_INPUT, email);
        return this;
    }

    public LoginPage fillPassword(String password) {
        fill(PASSWORD_INPUT, password);
        return this;
    }

    public LoginPage clickLogin() {
        click(LOGIN_BUTTON);
        return this;
    }

    /**
     * Универсальный submit для негативных сценариев:
     * заполнили форму, нажали логин, остались работать с LoginPage.
     */
    public LoginPage submitLogin(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        clickLogin();
        return this;
    }

    /**
     * Позитивный логин:
     * после сабмита ждем признак успешной авторизации и возвращаем HomePage.
     */
    public HomePage loginAsValidUser(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        clickLogin();

        HomePage homePage = new HomePage(page);
        assertThat(homePage.logoutLink()).isVisible();
        return homePage;
    }

    public boolean hasValidationSummary() {
        return isVisible(VALIDATION_SUMMARY);
    }

    public boolean hasEmailError() {
        return isVisible(EMAIL_ERROR);
    }

    public boolean hasPasswordError() {
        return isVisible(PASSWORD_ERROR);
    }

    public boolean hasErrorMessage() {
        return isVisible(ERROR_MESSAGE);
    }

    public String errorMessage() {
        return text(ERROR_MESSAGE);
    }

    public String validationSummaryText() {
        return text(VALIDATION_SUMMARY);
    }

    public String emailErrorText() {
        return text(EMAIL_ERROR);
    }

    public String passwordErrorText() {
        return text(PASSWORD_ERROR);
    }
}