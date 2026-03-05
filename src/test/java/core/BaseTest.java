package core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import config.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

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
        context = PlaywrightFactory.getBrowser().newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720));
        page = context.newPage();
        TestContext.setPage(page);

        page.setDefaultTimeout(10_000);
        page.navigate(TestConfig.baseUrl());
    }

    @AfterEach
    void tearDown() {
        TestContext.clear();
        context.close();
    }
}