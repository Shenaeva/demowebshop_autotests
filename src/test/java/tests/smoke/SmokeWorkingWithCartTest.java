package tests.smoke;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@RunTags.UI
@RunTags.Smoke
@FeatureTags.Cart

public class SmokeWorkingWithCartTest extends BaseTest {

    @Test
    void going_ToAnEmptyCart() {
        // 1. Главная уже открыта

        // 2. Проверить, что корзина обозначена пустой, 0 добавленных товаров
        assertThat(page.locator(".cart-qty")).containsText("(0)");

        // 3. Перейти в корзину
        page.locator(".ico-cart").first().click();

        // 4. Проверить страницу пустой страницы
        assertThat(page.locator(".order-summary-content")).containsText("Your Shopping Cart is empty!");

        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "cart_is_empty_test");
    }

    @Test
    void addingAnItemToCart() {
        // 1. Главная уже открыта

        // 2. Перейти в Apparel & Shoes
        page.locator("a[href='/apparel-shoes']").first().click();

        // 3. У Товара Casual Golf Belt нажать на кнопку Add to cart
        Locator productCard = page.locator(".product-item")
                .filter(new Locator.FilterOptions().setHasText("Casual Golf Belt"));

        productCard.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Add to cart")
        ).click();

        // 4. Проверить появления зеленой нотификации о добавлении товара в корзину
        Locator notification = page.locator(".bar-notification.success");
        assertThat(notification).containsText("The product has been added to your shopping cart");

        //5. Закрыть нотификацию
        notification.locator(".close").click();

        // 5. Проверить изменение количества товара в корзине
        assertThat(page.locator(".cart-qty")).containsText("(1)");

        // 6. Перейти в корзину
        page.locator(".ico-cart").first().click();
        //assertThat(page).hasURL("**/cart");
        assertThat(page.locator(".page-title")).containsText("Shopping cart");

        // 7. Проверить корзину визуально
        stabilizeForScreenshot();
        utils.VisualAssert.assertPageScreenshot(page, "the_item_has_been_added_to_the_cart_test");

    }

    private void stabilizeForScreenshot() {
        page.evaluate("() => window.scrollTo(0, 0)");
        page.addStyleTag(new Page.AddStyleTagOptions().setContent(
                "* { transition: none !important; animation: none !important; }" +
                        ".mini-shopping-cart, .flyout-cart { display: none !important; }"
        ));

        page.mouse().move(60, 500);
        page.locator("body").click(new Locator.ClickOptions().setPosition(100, 500));
    }
}
