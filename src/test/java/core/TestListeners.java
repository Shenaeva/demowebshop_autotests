package core;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestListeners implements TestWatcher, BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        Page page = TestContext.getPage();
        if (page == null) return;

        // Start trace for every test; save ZIP only if the test fails
        page.context().tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Page page = TestContext.getPage();
        if (page == null) return;

        String safeName = makeSafeName(context);

        attachText("URL", safe(page.url()));
        attachText("Failure", safe(cause.toString()));

        attachAndSaveScreenshot(page, safeName);
        attachAndSaveTrace(page, safeName);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Page page = TestContext.getPage();
        if (page == null) return;

        try {
            // If test passed, stop trace without saving
            page.context().tracing().stop();
        } catch (Exception ignored) {
        }
    }

    private void attachAndSaveScreenshot(Page page, String safeName) {
        try {
            Path screenshotPath = Path.of("artifacts", "screenshots", safeName + ".png");
            Files.createDirectories(screenshotPath.getParent());

            // Save to disk (so Jenkins can archive it)...
            page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(screenshotPath));

            // ...and attach to Allure
            byte[] png = Files.readAllBytes(screenshotPath);
            Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
        } catch (Exception e) {
            attachText("Screenshot error", safe(e.toString()));
        }
    }

    private void attachAndSaveTrace(Page page, String safeName) {
        try {
            Path tracePath = Path.of("artifacts", "traces", safeName + ".zip");
            Files.createDirectories(tracePath.getParent());

            // Stop trace and save ZIP
            page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));

            // Attach ZIP to Allure
            byte[] zip = Files.readAllBytes(tracePath);
            Allure.addAttachment("Playwright trace", "application/zip", new ByteArrayInputStream(zip), ".zip");
        } catch (Exception e) {
            attachText("Trace error", safe(e.toString()));
        }
    }

    private void attachText(String name, String text) {
        Allure.addAttachment(name, "text/plain", text);
    }

    private String makeSafeName(ExtensionContext context) {
        String cls = context.getRequiredTestClass().getSimpleName();
        String mtd = context.getRequiredTestMethod().getName();
        return (cls + "_" + mtd).replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}