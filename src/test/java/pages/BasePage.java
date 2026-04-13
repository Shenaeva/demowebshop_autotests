package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public abstract class BasePage {

    private static final String DEFAULT_PAGE_TITLE = "div.page-title h1";

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected Locator locator(String selector) {
        return page.locator(selector);
    }

    protected void click(String selector) {
        locator(selector).click();
    }

    protected void fill(String selector, String value) {
        locator(selector).fill(value);
    }

    protected String text(String selector) {
        return locator(selector).innerText().trim();
    }

    protected boolean isVisible(String selector) {
        return locator(selector).isVisible();
    }

    protected void waitForUrlContains(String urlPart) {
        page.waitForURL(url -> url.contains(urlPart));
    }

    protected void waitForBrowserTitleContains(String titlePart) {
        assertThat(page).hasTitle(Pattern.compile(".*" + Pattern.quote(titlePart) + ".*"));
    }

    /**
     * Ожидание заголовка страницы (h1 внутри div.page-title)
     */
    protected void waitForTitle(String expectedTitle) {
        assertThat(locator(DEFAULT_PAGE_TITLE)).hasText(expectedTitle);
    }

    protected String pageTitle() {
        return text(DEFAULT_PAGE_TITLE);
    }

    /**
     * Подготовка страницы к визуальному сравнению:
     * - скролл наверх
     * - отключение анимаций/transition
     * - скрытие всплывающих уведомлений, которые часто мешают baseline
     */
    protected void stabilizeForScreenshot() {
        page.evaluate("""
            () => {
                window.scrollTo(0, 0);

                let style = document.getElementById('visual-stabilizer');
                if (!style) {
                    style = document.createElement('style');
                    style.id = 'visual-stabilizer';
                    style.textContent = `
                        *, *::before, *::after {
                            animation: none !important;
                            transition: none !important;
                            caret-color: transparent !important;
                        }

                        .bar-notification {
                            display: none !important;
                        }
                    `;
                    document.head.appendChild(style);
                }
            }
        """);
    }
}