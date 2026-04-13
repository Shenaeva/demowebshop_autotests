package tests.smoke;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Auth

public class SmokeAuthorizationTest extends BaseTest {
    @Test
    void smokeAuthorization() {
        HomePage homePage = new HomePage(page);
        LoginPage loginPage = homePage.openLoginPage();

        homePage = loginPage.login(Credentials.email(), Credentials.password());

        assertThat(homePage.accountLink()).isVisible();
        assertThat(homePage.logoutLink()).isVisible();

        homePage.logout();

        assertThat(homePage.loginLink()).isVisible();
    }
}
