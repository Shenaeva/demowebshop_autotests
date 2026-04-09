package tests.regression.auth;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.TestDataGenerator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RunTags.Regression
@FeatureTags.Registration
public class NewUserRegistrationTest extends BaseTest {

    @Test
    @DisplayName("Регистрация нового пользователя")
    void register_new_user_success() {
        String email = TestDataGenerator.uniqueEmail();
        String password = TestDataGenerator.validPassword();

        // 1. Главная уже открыта
        // 2. Перейти на страницу регистрации
        openRegistrationPage();

        // 3. Заполнить обязательные поля валидными данными
        fillRegistrationForm(
                "UserFName",
                "UserLastName",
                email,
                password,
                password
        );

        // 4. Нажать на кнопку Register
        page.locator("#register-button").click();

        // 5. Проверить успешную регистрацию
        Locator successMessage = page.locator(".result");
        Locator logout = page.locator(".ico-logout");
        Locator accountLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(email));

        assertThat(successMessage).hasText("Your registration completed");
        assertThat(logout).isVisible();
        assertThat(accountLink).isVisible();

        // 6. Для чистоты состояния выйти из аккаунта
        logout.click();

        // 7. Проверить, что пользователь разлогинен
        assertThat(page.locator(".ico-login").first()).isVisible();
        assertThat(page.locator(".ico-logout").first()).isHidden();
    }

    @Test
    @DisplayName("Регистрация не удалась из-за пустых обязательных полей")
    void register_fails_with_empty_required_fields() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу регистрации
        openRegistrationPage();

        // 3. Нажать Register без заполнения полей
        page.locator("#register-button").click();

        // 4. Проверить ошибки обязательных полей
        assertThat(page.locator("[data-valmsg-for='FirstName']"))
                .hasText("First name is required.");
        assertThat(page.locator("[data-valmsg-for='LastName']"))
                .hasText("Last name is required.");
        assertThat(page.locator("[data-valmsg-for='Email']"))
                .hasText("Email is required.");
        assertThat(page.locator("[data-valmsg-for='Password']"))
                .hasText("Password is required.");
        assertThat(page.locator("[data-valmsg-for='ConfirmPassword']"))
                .hasText("Password is required.");
    }

    @Test
    @DisplayName("Регистрация завершается неудачей, если пароли не совпадают")
    void register_fails_when_passwords_do_not_match() {
        String email = TestDataGenerator.uniqueEmail();
        String password = TestDataGenerator.validPassword();

        // 1. Главная уже открыта
        // 2. Перейти на страницу регистрации
        openRegistrationPage();

        // 3. Заполнить форму с разными Password / Confirm Password
        fillRegistrationForm(
                "UserFName",
                "UserLastName",
                email,
                password,
                password + "1"
        );

        // 4. Нажать Register
        page.locator("#register-button").click();

        // 5. Проверить ошибку несовпадения паролей
        assertThat(page.locator("[data-valmsg-for='ConfirmPassword']"))
                .containsText("do not match");
    }

    @Test
    @DisplayName("Регистрация для уже зарегистрированного email не удалась")
    void register_fails_for_existing_email() {
        String password = TestDataGenerator.validPassword();

        // 1. Главная уже открыта
        // 2. Перейти на страницу регистрации
        openRegistrationPage();

        // 3. Заполнить форму существующим email
        fillRegistrationForm(
                "UserFName",
                "UserLastName",
                Credentials.email(),
                password,
                password
        );

        // 4. Нажать Register
        page.locator("#register-button").click();

        // 5. Проверить ошибку, что email уже существует
        Locator summaryErrors = page.locator(".validation-summary-errors");

        assertThat(summaryErrors).isVisible();
        assertThat(summaryErrors).containsText("already exists");
    }

    @Test
    @DisplayName("Заполнение полей регистрации данными в невалидном формате")
    void filling_fields_with_data_in_invalid_format() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу регистрации
        openRegistrationPage();

        // 3. Заполнить форму невалидными данными
        fillRegistrationForm(
                "UserFName",
                "UserLastName",
                "invalid-email",
                "123",
                "123"
        );

        // 4. Нажать Register
        page.locator("#register-button").click();

        // 5. Проверить ошибки валидации
        assertThat(page.locator("[data-valmsg-for='Email']"))
                .hasText("Wrong email");
        assertThat(page.locator("[data-valmsg-for='Password']"))
                .hasText("The password should have at least 6 characters.");
    }

    private void openRegistrationPage() {
        page.locator(".ico-register").first().click();
    }

    private void fillRegistrationForm(
            String firstName,
            String lastName,
            String email,
            String password,
            String confirmPassword
    ) {
        page.locator("#FirstName").fill(firstName);
        page.locator("#LastName").fill(lastName);
        page.locator("#Email").fill(email);
        page.locator("#Password").fill(password);
        page.locator("#ConfirmPassword").fill(confirmPassword);
    }
}