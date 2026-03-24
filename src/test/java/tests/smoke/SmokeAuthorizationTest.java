package tests.smoke;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Cart

public class SmokeAuthorizationTest extends BaseTest {
    @Test
    void smokeAuthorization() {
        // 1. Главная уже открыта
        // 2. Перейти на страницу авторизации
        page.locator(".ico-login").first().click();

        // 3. Ввести логин
        page.locator("input#Email").fill(Credentials.email());

        // 4. Ввести пароль
        page.locator("input#Password").fill(Credentials.password());

        // 5. Нажать на кнопку Log in
        //page.locator("button-1 login-button").click();
        page.locator(".button-1.login-button").click();

        // 6. Проверить выполнение авторизации (шапка). в account должен быть введенный email
       // assertThat(page.getByRole(AriaRole.LINK,
        //        new Page.GetByRoleOptions().setName(Credentials.email()))).isVisible();

        //assertThat(page.getByRole(AriaRole.LINK,
        //        new Page.GetByRoleOptions().setName("My account"))).isVisible();
        // assertThat(page.locator(".ico-logout")).isVisible();

        Locator logout = page.locator(".ico-logout");
        Locator myAccount = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("My account"));
        Locator validationSummary = page.locator(".validation-summary-errors");
        Locator emailError = page.locator("[data-valmsg-for='Email']");
        Locator passwordError = page.locator("[data-valmsg-for='Password']");

        page.waitForTimeout(2000);

        if (validationSummary.isVisible()) {
            fail("Login failed. Validation summary: " + validationSummary.textContent());
        }

        if (emailError.isVisible()) {
            fail("Login failed. Email error: " + emailError.textContent());
        }

        if (passwordError.isVisible()) {
            fail("Login failed. Password error: " + passwordError.textContent());
        }

        if (!logout.isVisible() && !myAccount.isVisible()) {
            fail("Login did not succeed. Current URL: " + page.url()
                    + " | Page title: " + page.title());
        }

        // 7. Нажать на кнопку Log out
        assertThat(myAccount).isVisible();
        assertThat(logout).isVisible();

        page.locator(".ico-logout").click();

        // 8. Проверить разлогин
        assertThat(page.locator(".ico-login")).containsText("Log in");
    }
}
