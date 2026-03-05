package pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) { this.page = page; }

    public void login(String email, String password) {
        page.locator("input#Email").fill(email);
        page.locator("input#Password").fill(password);
        page.locator("input[value='Log in']").click();
    }

    public String errorMessage() {
        return page.locator("div.message-error").innerText();
    }
}