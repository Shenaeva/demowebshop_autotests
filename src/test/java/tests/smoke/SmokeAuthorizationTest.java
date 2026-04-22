package tests.smoke;

import config.Credentials;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Auth

public class SmokeAuthorizationTest extends BaseTest {
    @Test
    void smokeAuthorization() {
        HomePage homePage = new HomePage(page);
        LoginPage loginPage = homePage.openLoginPage();

        homePage = loginPage.loginAsValidUser(Credentials.email(), Credentials.password());

        assertThat(homePage.accountLink()).isVisible();
        assertThat(homePage.logoutLink()).isVisible();

        homePage.logout();

        assertThat(homePage.loginLink()).isVisible();
    }
}
