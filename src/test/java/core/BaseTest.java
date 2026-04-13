package core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import config.TestConfig;
import core.PlaywrightFactory;
import core.TestContext;
import core.TestListeners;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@ExtendWith(TestListeners.class)
public abstract class BaseTest {

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void beforeAll() {
        PlaywrightFactory.getBrowser();
    }

    @AfterAll
    static void afterAll() {
        PlaywrightFactory.shutdown();
    }

    @BeforeEach
    void setUp() {
        context = PlaywrightFactory.getBrowser().newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1280, 720)
        );

        page = context.newPage();
        TestContext.setPage(page);

        page.setDefaultTimeout(10_000);
        page.setDefaultNavigationTimeout(30_000);

        page.navigate(
                TestConfig.baseUrl(),
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(30_000)
        );

        assertThat(page.locator(".ico-register").first()).isVisible();
    }

    @AfterEach
    void tearDown() {
        TestContext.clear();
        context.close();
    }
}