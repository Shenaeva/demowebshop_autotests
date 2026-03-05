package core;

import com.microsoft.playwright.Page;

public final class TestContext {
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private TestContext() {}

    public static void setPage(Page page) {
        PAGE.set(page);
    }

    public static Page getPage() {
        return PAGE.get();
    }

    public static void clear() {
        PAGE.remove();
    }
}