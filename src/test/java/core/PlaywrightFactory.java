package core;

import com.microsoft.playwright.*;

public class PlaywrightFactory {
    private static Playwright playwright;
    private static Browser browser;

    public static Browser getBrowser() {
        if (browser == null) {
            playwright = Playwright.create();

            String browserName = System.getProperty("browser", "chromium").toLowerCase();
            boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(headless);

            browser = switch (browserName) {
                case "firefox" -> playwright.firefox().launch(options);
                case "webkit" -> playwright.webkit().launch(options);
                default -> playwright.chromium().launch(options);
            };
        }
        return browser;
    }

    public static void shutdown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        browser = null;
        playwright = null;
    }
}