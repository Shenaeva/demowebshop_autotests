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

    public LoginPage(Page page) {
        super(page);
    }

    public HomePage login(String email, String password) {
        fill(EMAIL_INPUT, email);
        fill(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);

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

    public String errorMessage() {
        return text(ERROR_MESSAGE);
    }

    public String validationSummaryText() {
        return text(VALIDATION_SUMMARY);
    }
}