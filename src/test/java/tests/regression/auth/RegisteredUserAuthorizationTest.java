package tests.regression.auth;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RunTags.Regression
@FeatureTags.Auth


public class RegisteredUserAuthorizationTest extends BaseTest {

    @Test
    @DisplayName("логин с валидными данными")
    void login_with_valid_credentials_success() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        page.locator(".ico-login").first().click();

        // 3. Ввести логин
        page.locator("input#Email").fill(Credentials.email());

        // 4. Ввести пароль
        page.locator("input#Password").fill(Credentials.password());

        // 5. Нажать на кнопку Log in
        page.locator(".button-1.login-button").click();

        // 6. Проверить выполнение авторизации (шапка). в account должен быть введенный email
        Locator logout = page.locator(".ico-logout");
        Locator myAccount = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("My account"));
        assertThat(myAccount).isVisible();
        assertThat(logout).isVisible();

        // 7. Нажать на кнопку Log out
        page.locator(".ico-logout").click();

    }

    @Test
    @DisplayName("логин с невалидным паролем")
    void login_with_invalid_password_shows_error() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        page.locator(".ico-login").first().click();

        // 3. Ввести логин
        page.locator("input#Email").fill(Credentials.email());

        // 4. Ввести невалидный пароль
        page.locator("input#Password").fill("QWERTY");

        // 5. Нажать на кнопку Log in
        page.locator(".button-1.login-button").click();

        //6. Проверить появление нотификации о неверном пароле
        Locator errorBlock = page.locator(".validation-summary-errors");
        Locator errorTitle = page.locator(".validation-summary-errors span");
        Locator errorDetails = page.locator(".validation-summary-errors li");

        assertThat(errorBlock).isVisible();
        assertThat(errorTitle).hasText("Login was unsuccessful. Please correct the errors and try again.");
        assertThat(errorDetails).hasText("The credentials provided are incorrect");
    }

    @Test
    @DisplayName("логин под незарегистрированным пользователем")
    void login_as_an_unregistered_user() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        Locator logIn = page.locator(".ico-login").first();
        logIn.click();

        // 3. Ввести логин
        page.locator("input#Email").fill("f@d.com");

        // 4. Ввести невалидный пароль
        page.locator("input#Password").fill("QWERTY");

        // 5. Нажать на кнопку Log in
        page.locator(".button-1.login-button").click();

        //6. Проверить появление нотификации о неверном пароле
        Locator errorBlock = page.locator(".validation-summary-errors");
        Locator errorTitle = page.locator(".validation-summary-errors span");
        Locator errorDetails = page.locator(".validation-summary-errors li");

        assertThat(errorBlock).isVisible();
        assertThat(errorTitle).hasText("Login was unsuccessful. Please correct the errors and try again.");
        assertThat(errorDetails).hasText("No customer account found");
    }

    @Test
    @DisplayName("Разлогин")
    void logout_success() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        Locator logIn = page.locator(".ico-login").first();
        logIn.click();

        // 3. Ввести логин
        page.locator("input#Email").fill(Credentials.email());

        // 4. Ввести валидный пароль
        page.locator("input#Password").fill(Credentials.password());

        // 5. Нажать на кнопку Log in
        page.locator(".button-1.login-button").click();

        // 6. Проверить появления функционала разлогина и скрытие логина
        Locator logOut = page.locator(".ico-logout").first();
        assertThat(logOut).isVisible();
        assertThat(logIn).isHidden();

        //7. Выполнить разлогин
        logOut.click();

        //8. Проверить возвращения функционала авторизации
        assertThat(logIn).isVisible();
        assertThat(logOut).isHidden();
    }

    @Test
    @DisplayName("Восстановление пароля")
    void password_recovery() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        page.locator(".ico-login").first().click();

        //3. Проверить наличие ссылки и нажать на "Forgot password?"
        Locator linkForgotPassword = page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Forgot password?")
        );
        assertThat(linkForgotPassword).isVisible();
        linkForgotPassword.click();

        //4. Проверить переход на страницу восстановления пароля
        assertThat(page).hasURL("https://demowebshop.tricentis.com/passwordrecovery");
        assertThat(page.getByText("Password recovery")).isVisible();

        Locator emailField = page.locator("#Email");
        assertThat(emailField).isVisible();

        Locator buttonRecover = page.locator(".button-1.password-recovery-button");
        assertThat(buttonRecover).isVisible();

        //5. Нажать на кнопку Recover и проверить нотификацию об ошибке
        buttonRecover.click();
        assertThat(page.getByText("Enter your email", new Page.GetByTextOptions().setExact(true))).isVisible();

        //6. Ввести незарегистрированный email, нажать на кнопку Recover и проверить нотификацию об ошибке
        emailField.fill("f@d.com");
        buttonRecover.click();

        assertThat(page.getByText("Email not found.", new Page.GetByTextOptions().setExact(true))).isVisible();

        //7. Ввести зарегистрированный email, нажать на кнопку Recover и проверить нотификацию
        emailField.fill(Credentials.email());
        buttonRecover.click();

        assertThat(page.getByText("Email with instructions has been sent to you.",
                new Page.GetByTextOptions().setExact(true))).isVisible();

    }
}
