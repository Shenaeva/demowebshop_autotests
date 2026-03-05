package utils;

import com.microsoft.playwright.Page;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

public final class VisualAssert {
    private VisualAssert() {}

    private static final Path BASELINE_DIR = Path.of("src", "test", "resources", "visual-baseline");
    private static final Path ACTUAL_DIR = Path.of("artifacts", "screenshots", "actual");
    private static final Path DIFF_DIR = Path.of("artifacts", "screenshots", "diff");

    public static void assertPageScreenshot(Page page, String name) {
        try {
            Files.createDirectories(BASELINE_DIR);
            Files.createDirectories(ACTUAL_DIR);
            Files.createDirectories(DIFF_DIR);

            String fileName = sanitize(name) + ".png";
            Path baselinePath = BASELINE_DIR.resolve(fileName);
            Path actualPath = ACTUAL_DIR.resolve(fileName);
            Path diffPath = DIFF_DIR.resolve(fileName);

            // 1) actual
            page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(actualPath));

            // 2) baseline create
            if (!Files.exists(baselinePath)) {
                Files.copy(actualPath, baselinePath);
                fail("Baseline screenshot was created: " + baselinePath + "\nCommit it and re-run the test.");
            }

            BufferedImage expected = ImageIO.read(baselinePath.toFile());
            BufferedImage actual = ImageIO.read(actualPath.toFile());

            DiffResult diff = diffImages(expected, actual);

            if (diff.mismatch) {
                ImageIO.write(diff.diffImage, "png", diffPath.toFile());
                fail("Visual mismatch for: " + fileName +
                        "\nBaseline: " + baselinePath +
                        "\nActual:   " + actualPath +
                        "\nDiff:     " + diffPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Visual comparison failed: " + e.getMessage(), e);
        }
    }

    private static DiffResult diffImages(BufferedImage expected, BufferedImage actual) {
        int w = Math.max(expected.getWidth(), actual.getWidth());
        int h = Math.max(expected.getHeight(), actual.getHeight());

        BufferedImage exp = padToSize(expected, w, h);
        BufferedImage act = padToSize(actual, w, h);

        BufferedImage diff = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        boolean mismatch = false;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p1 = exp.getRGB(x, y);
                int p2 = act.getRGB(x, y);

                if (p1 != p2) {
                    mismatch = true;
                    // подсветим отличие красным пикселем
                    diff.setRGB(x, y, new Color(255, 0, 0, 180).getRGB());
                } else {
                    // совпало — делаем полупрозрачный фон из actual
                    diff.setRGB(x, y, (act.getRGB(x, y) & 0x00FFFFFF) | (80 << 24));
                }
            }
        }

        return new DiffResult(mismatch, diff);
    }

    private static BufferedImage padToSize(BufferedImage src, int w, int h) {
        if (src.getWidth() == w && src.getHeight() == h) return src;

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, w, h);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static final class DiffResult {
        final boolean mismatch;
        final BufferedImage diffImage;

        DiffResult(boolean mismatch, BufferedImage diffImage) {
            this.mismatch = mismatch;
            this.diffImage = diffImage;
        }
    }
}