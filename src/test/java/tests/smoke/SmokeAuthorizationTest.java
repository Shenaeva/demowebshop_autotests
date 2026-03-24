package tests.smoke;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("My account"))).isVisible();
        assertThat(page.locator(".ico-logout")).isVisible();

        // 7. Нажать на кнопку Log out
        page.locator(".ico-logout").click();

        // 8. Проверить разлогин
        assertThat(page.locator(".ico-login")).containsText("Log in");
    }
}
