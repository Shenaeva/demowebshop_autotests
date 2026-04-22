package tests.smoke;

import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.ProductPage;
import pages.ProductsPage;
import pages.CartPage;
import utils.VisualAssert;

import static org.junit.jupiter.api.Assertions.*;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Search
@FeatureTags.Catalog

public class SmokeSearchTest extends BaseTest {

    @Test
    void search_returnsResults() {
        HomePage homePage = new HomePage(page);
        ProductsPage productsPage = homePage.search("computer");
        ProductPage productPage = productsPage.openFirstProductFromGrid();

        productPage.addToCart()
                .closeSuccessNotification();

        CartPage cartPage = homePage.openCart();
        assertTrue(cartPage.hasItems());

        assertTrue(productsPage.hasResults(), "Ожидали непустые результаты поиска");
        assertTrue(productsPage.isSearchPageOpened(), "URL должен содержать 'search'");
        assertTrue(productsPage.titleText().toLowerCase().contains("search"),
                "Должен быть заголовок страницы поиска");

        productsPage.prepareForScreenshot();
        VisualAssert.assertPageScreenshot(page, "home_search_results_tests");
    }
}