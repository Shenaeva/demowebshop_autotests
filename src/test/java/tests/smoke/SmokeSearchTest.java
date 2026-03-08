package tests.smoke;

import com.microsoft.playwright.Page;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Search
@FeatureTags.Catalog
public class SmokeSearchTest extends BaseTest {

    @Test
    void search_returnsResults() {
        // 1) в BaseTest уже открыта главная страница

        // 2) вводим запрос
        page.locator("input#small-searchterms").fill("computer");

        // 3) жмем Search
        page.locator("input[value='Search']").click();

        // 4) проверяем, что результаты есть
        int count = page.locator(".product-grid .product-item").count();
        assertTrue(count > 0, "Ожидали непустые результаты поиска");

        // 5) доп.проверки (не обязательны, но полезны)
        assertTrue(page.url().contains("search"), "URL должен содержать 'search'");
        assertTrue(page.locator("div.page-title").innerText().toLowerCase().contains("search"),
                "Должен быть заголовок страницы поиска");

        //page.addStyleTag(new Page.AddStyleTagOptions()
        //        .setContent("body { filter: grayscale(1) !important; }"));

        utils.VisualAssert.assertPageScreenshot(page, "home_search_results");
    }
}