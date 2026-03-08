package utils;

import com.microsoft.playwright.Locator;
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

    /**
     * Маска в baseline: яркая магента (#FF00FF).
     * Все пиксели baseline, похожие на этот цвет, исключаются из сравнения.
     */
    private static final int MASK_MIN_R = 200;
    private static final int MASK_MAX_G = 140;
    private static final int MASK_MIN_B = 200;
    private static final int MASK_MIN_A = 100;

    /**
     * На сколько пикселей расширять маску вокруг найденной магенты.
     * Это убирает ложные срабатывания на "кайму" по краям закрашенной области.
     */
    private static final int MASK_EXPAND_RADIUS = 2;

    /**
     * Допуск при сравнении цветов.
     * Нужен, чтобы не падать на микросдвигах рендера, сглаживании, субпикселях и т.д.
     */
    private static final int RGB_TOLERANCE = 12;
    private static final int ALPHA_TOLERANCE = 12;

    public static void assertPageScreenshot(Page page, String name) {
        try {
            Files.createDirectories(BASELINE_DIR);
            Files.createDirectories(ACTUAL_DIR);
            Files.createDirectories(DIFF_DIR);

            String fileName = sanitize(name) + ".png";
            Path baselinePath = BASELINE_DIR.resolve(fileName);
            Path actualPath = ACTUAL_DIR.resolve(fileName);
            Path diffPath = DIFF_DIR.resolve(fileName);

            page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(actualPath));

            if (!Files.exists(baselinePath)) {
                Files.copy(actualPath, baselinePath);
                fail("Baseline screenshot was created: " + baselinePath + "\n" +
                        "Open it, optionally mask dynamic areas with color #FF00FF, commit it and re-run the test.");
            }

            BufferedImage expected = ImageIO.read(baselinePath.toFile());
            BufferedImage actual = ImageIO.read(actualPath.toFile());

            DiffResult diff = diffImagesWithMaskedBaseline(expected, actual);

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

    public static void assertLocatorScreenshot(Locator locator, String name) {
        try {
            Files.createDirectories(BASELINE_DIR);
            Files.createDirectories(ACTUAL_DIR);
            Files.createDirectories(DIFF_DIR);

            String fileName = sanitize(name) + ".png";
            Path baselinePath = BASELINE_DIR.resolve(fileName);
            Path actualPath = ACTUAL_DIR.resolve(fileName);
            Path diffPath = DIFF_DIR.resolve(fileName);

            locator.screenshot(new Locator.ScreenshotOptions().setPath(actualPath));

            if (!Files.exists(baselinePath)) {
                Files.copy(actualPath, baselinePath);
                fail("Baseline screenshot was created: " + baselinePath + "\n" +
                        "Open it, optionally mask dynamic areas with color #FF00FF, commit it and re-run the test.");
            }

            BufferedImage expected = ImageIO.read(baselinePath.toFile());
            BufferedImage actual = ImageIO.read(actualPath.toFile());

            DiffResult diff = diffImagesWithMaskedBaseline(expected, actual);

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

    private static DiffResult diffImagesWithMaskedBaseline(BufferedImage expected, BufferedImage actual) {
        int w = Math.max(expected.getWidth(), actual.getWidth());
        int h = Math.max(expected.getHeight(), actual.getHeight());

        BufferedImage exp = padToSize(expected, w, h);
        BufferedImage act = padToSize(actual, w, h);

        BufferedImage diff = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        boolean mismatch = false;

        boolean[][] baseMask = buildMask(exp, w, h);
        boolean[][] expandedMask = expandMask(baseMask, w, h, MASK_EXPAND_RADIUS);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pExp = exp.getRGB(x, y);
                int pAct = act.getRGB(x, y);

                if (expandedMask[y][x]) {
                    diff.setRGB(x, y, overlayAlpha(pAct, 50));
                    continue;
                }

                if (!isSimilarColor(pExp, pAct, RGB_TOLERANCE, ALPHA_TOLERANCE)) {
                    mismatch = true;
                    diff.setRGB(x, y, new Color(255, 0, 0, 180).getRGB());
                } else {
                    diff.setRGB(x, y, overlayAlpha(pAct, 80));
                }
            }
        }

        return new DiffResult(mismatch, diff);
    }

    private static boolean[][] buildMask(BufferedImage image, int w, int h) {
        boolean[][] mask = new boolean[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                mask[y][x] = isMaskedMagenta(image.getRGB(x, y));
            }
        }
        return mask;
    }

    private static boolean[][] expandMask(boolean[][] mask, int w, int h, int radius) {
        if (radius <= 0) {
            return mask;
        }

        boolean[][] expanded = new boolean[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (!mask[y][x]) {
                    continue;
                }

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int ny = y + dy;
                        int nx = x + dx;

                        if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                            expanded[ny][nx] = true;
                        }
                    }
                }
            }
        }

        return expanded;
    }

    private static boolean isMaskedMagenta(int argb) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;

        return a >= MASK_MIN_A
                && r >= MASK_MIN_R
                && g <= MASK_MAX_G
                && b >= MASK_MIN_B;
    }

    private static boolean isSimilarColor(int argb1, int argb2, int rgbTolerance, int alphaTolerance) {
        int a1 = (argb1 >>> 24) & 0xFF;
        int r1 = (argb1 >>> 16) & 0xFF;
        int g1 = (argb1 >>> 8) & 0xFF;
        int b1 = argb1 & 0xFF;

        int a2 = (argb2 >>> 24) & 0xFF;
        int r2 = (argb2 >>> 16) & 0xFF;
        int g2 = (argb2 >>> 8) & 0xFF;
        int b2 = argb2 & 0xFF;

        return Math.abs(a1 - a2) <= alphaTolerance
                && Math.abs(r1 - r2) <= rgbTolerance
                && Math.abs(g1 - g2) <= rgbTolerance
                && Math.abs(b1 - b2) <= rgbTolerance;
    }

    private static int overlayAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    private static BufferedImage padToSize(BufferedImage src, int w, int h) {
        if (src.getWidth() == w && src.getHeight() == h) {
            return src;
        }

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