package tests.smoke;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Catalog
public class SmokeTransitionToCategory extends BaseTest {

    @Test
    void transition_ToCategoryIsInProgress() {
        // 1) Главная уже открыта

        // 2) Клик по Books
        page.locator("ul.top-menu a[href='/books']").click();

        // 3) Дождаться полной загрузки и заголовка
        page.waitForLoadState();
        Locator title = page.locator("div.page-title h1");
        title.waitFor();
        assertEquals("Books", title.innerText().trim());

        // 4) Проверить URL
        assertTrue(page.url().contains("/books"), "URL должен содержать /books");

        // 5) Стабилизация перед скрином
        page.evaluate("() => window.scrollTo(0, 0)");
        page.addStyleTag(new Page.AddStyleTagOptions().setContent(
                "* { transition: none !important; animation: none !important; }"
        ));

        // 6) Визуальная проверка
        utils.VisualAssert.assertPageScreenshot(page, "transition_to_books_catalog");
    }

    @Test
    void transition_ToSubdirectoryInSideMenu() {
        // 1) Главная уже открыта

        // 2) Нажать на "Electronics"
        page.locator("a[href='/electronics']").first().click();

        // 3) Дождаться загрузки и проверить заголовок
        Locator titleElectronics = page.locator("div.page-title h1");
        titleElectronics.waitFor();
        assertEquals("Electronics", titleElectronics.innerText().trim());

        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "transition_to_electronics_catalog");

        // 4) Нажать на "Camera, photo"
        page.locator("a[href='/camera-photo']").first().click();

        // 5) Дождаться загрузки и проверить заголовок
        Locator titleCamera = page.locator("div.page-title h1");
        titleCamera.waitFor();
        assertEquals("Camera, photo", titleCamera.innerText().trim());

        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "transition_to_camera_catalog");
    }

    @Test
    void transition_ToTheProductPage() {
        // 1) Главная уже открыта

        // 2) Нажать на "Digital downloads"
        page.locator("a[href='/digital-downloads']").first().click();

        // 3) Дождаться загрузки и проверить заголовок
        Locator titleDigital = page.locator("div.page-title h1");
        titleDigital.waitFor();
        assertEquals("Digital downloads", titleDigital.innerText().trim());

        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "transition_to_digital_downloads_catalog");

        // 4) Открыть товар "3rd Album"
        page.locator("a[href='/album-3']").first().click();

        // 5) Проверить хлебные крошки (регистр не важен)
        Locator current = page.locator("xpath=//div[contains(@class,'breadcrumb')]//strong[contains(@class,'current-item')]");
        current.waitFor();
        assertTrue(current.innerText().trim().equalsIgnoreCase("3rd Album"));

        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "transition_to_3rd_album_product");
    }

    private void stabilizeForScreenshot() {
        page.evaluate("() => window.scrollTo(0, 0)");
        page.addStyleTag(new Page.AddStyleTagOptions().setContent(
                "* { transition: none !important; animation: none !important; }"
        ));
    }

}