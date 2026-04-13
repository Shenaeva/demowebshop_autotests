package pages;

import com.microsoft.playwright.Page;

public class RegistrationPage extends BasePage {

    private static final String FIRST_NAME = "#FirstName";
    private static final String LAST_NAME = "#LastName";
    private static final String EMAIL = "#Email";
    private static final String PASSWORD = "#Password";
    private static final String CONFIRM_PASSWORD = "#ConfirmPassword";
    private static final String REGISTER_BUTTON = "#register-button";
    private static final String SUCCESS_MESSAGE = ".result";
    private static final String SUMMARY_ERRORS = ".validation-summary-errors";

    public RegistrationPage(Page page) {
        super(page);
    }

    public RegistrationPage fillForm(
            String firstName,
            String lastName,
            String email,
            String password,
            String confirmPassword
    ) {
        fill(FIRST_NAME, firstName);
        fill(LAST_NAME, lastName);
        fill(EMAIL, email);
        fill(PASSWORD, password);
        fill(CONFIRM_PASSWORD, confirmPassword);
        return this;
    }

    public RegistrationPage submit() {
        click(REGISTER_BUTTON);
        return this;
    }

    public String successMessage() {
        return text(SUCCESS_MESSAGE);
    }

    public boolean hasSummaryErrors() {
        return isVisible(SUMMARY_ERRORS);
    }

    public String fieldError(String fieldName) {
        return text("[data-valmsg-for='" + fieldName + "']");
    }
}