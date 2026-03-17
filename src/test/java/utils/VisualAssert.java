package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.fail;

public final class VisualAssert {
    private VisualAssert() {}

    private static final Path BASELINE_DIR = Path.of("src", "test", "resources", "visual-baseline");
    private static final Path ACTUAL_DIR = Path.of("artifacts", "screenshots", "actual");
    private static final Path DIFF_DIR = Path.of("artifacts", "screenshots", "diff");

    /**
     * Пересъёмка baseline только по явному флагу:
     * -Dvisual.updateBaseline=true
     */
    private static final boolean UPDATE_BASELINE =
            Boolean.parseBoolean(System.getProperty("visual.updateBaseline", "false"));

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
     * Это помогает убрать ложные срабатывания по краям закрашенной области.
     */
    private static final int MASK_EXPAND_RADIUS = 2;

    /**
     * Допуск при сравнении цветов.
     */
    private static final int RGB_TOLERANCE = 12;
    private static final int ALPHA_TOLERANCE = 12;

    public static void assertPageScreenshot(Page page, String name) {
        try {
            PreparedPaths paths = preparePaths(name);

            page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(paths.actualPath));

            processScreenshot(paths);
        } catch (IOException e) {
            throw new RuntimeException("Visual comparison failed: " + e.getMessage(), e);
        }
    }

    public static void assertLocatorScreenshot(Locator locator, String name) {
        try {
            PreparedPaths paths = preparePaths(name);

            locator.screenshot(new Locator.ScreenshotOptions().setPath(paths.actualPath));

            processScreenshot(paths);
        } catch (IOException e) {
            throw new RuntimeException("Visual comparison failed: " + e.getMessage(), e);
        }
    }

    private static void processScreenshot(PreparedPaths paths) throws IOException {
        if (!Files.exists(paths.baselinePath)) {
            Files.copy(paths.actualPath, paths.baselinePath, StandardCopyOption.REPLACE_EXISTING);
            deleteIfExists(paths.diffPath);
            System.out.println("[VisualAssert] Baseline created: " + paths.baselinePath);
            return;
        }

        if (UPDATE_BASELINE) {
            Files.copy(paths.actualPath, paths.baselinePath, StandardCopyOption.REPLACE_EXISTING);
            deleteIfExists(paths.diffPath);
            System.out.println("[VisualAssert] Baseline updated: " + paths.baselinePath);
            return;
        }

        BufferedImage expected = ImageIO.read(paths.baselinePath.toFile());
        BufferedImage actual = ImageIO.read(paths.actualPath.toFile());

        if (expected == null) {
            throw new IOException("Cannot read baseline image: " + paths.baselinePath);
        }
        if (actual == null) {
            throw new IOException("Cannot read actual image: " + paths.actualPath);
        }

        DiffResult diff = diffImagesWithMaskedBaseline(expected, actual);

        if (diff.mismatch) {
            ImageIO.write(diff.diffImage, "png", paths.diffPath.toFile());
            fail("Visual mismatch for: " + paths.fileName +
                    "\nBaseline: " + paths.baselinePath +
                    "\nActual:   " + paths.actualPath +
                    "\nDiff:     " + paths.diffPath +
                    "\nTo accept new screenshot, rerun with -Dvisual.updateBaseline=true");
        }

        deleteIfExists(paths.diffPath);
    }

    private static PreparedPaths preparePaths(String name) throws IOException {
        Files.createDirectories(BASELINE_DIR);
        Files.createDirectories(ACTUAL_DIR);
        Files.createDirectories(DIFF_DIR);

        String fileName = sanitize(name) + ".png";
        Path baselinePath = BASELINE_DIR.resolve(fileName);
        Path actualPath = ACTUAL_DIR.resolve(fileName);
        Path diffPath = DIFF_DIR.resolve(fileName);

        return new PreparedPaths(fileName, baselinePath, actualPath, diffPath);
    }

    private static DiffResult diffImagesWithMaskedBaseline(BufferedImage expected, BufferedImage actual) {
        int width = Math.max(expected.getWidth(), actual.getWidth());
        int height = Math.max(expected.getHeight(), actual.getHeight());

        BufferedImage exp = padToSize(expected, width, height);
        BufferedImage act = padToSize(actual, width, height);

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        boolean mismatch = false;

        boolean[][] baseMask = buildMask(exp, width, height);
        boolean[][] expandedMask = expandMask(baseMask, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int expectedPixel = exp.getRGB(x, y);
                int actualPixel = act.getRGB(x, y);

                if (expandedMask[y][x]) {
                    diff.setRGB(x, y, overlayAlpha(actualPixel, 50));
                    continue;
                }

                if (!isSimilarColor(expectedPixel, actualPixel)) {
                    mismatch = true;
                    diff.setRGB(x, y, new Color(255, 0, 0, 180).getRGB());
                } else {
                    diff.setRGB(x, y, overlayAlpha(actualPixel, 80));
                }
            }
        }

        return new DiffResult(mismatch, diff);
    }

    private static boolean[][] buildMask(BufferedImage image, int width, int height) {
        boolean[][] mask = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mask[y][x] = isMaskedMagenta(image.getRGB(x, y));
            }
        }
        return mask;
    }

    private static boolean[][] expandMask(boolean[][] mask, int width, int height) {
        if (MASK_EXPAND_RADIUS <= 0) {
            return mask;
        }

        boolean[][] expanded = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!mask[y][x]) {
                    continue;
                }

                for (int dy = -MASK_EXPAND_RADIUS; dy <= MASK_EXPAND_RADIUS; dy++) {
                    for (int dx = -MASK_EXPAND_RADIUS; dx <= MASK_EXPAND_RADIUS; dx++) {
                        int newY = y + dy;
                        int newX = x + dx;

                        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                            expanded[newY][newX] = true;
                        }
                    }
                }
            }
        }

        return expanded;
    }

    private static boolean isMaskedMagenta(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        int red = (argb >>> 16) & 0xFF;
        int green = (argb >>> 8) & 0xFF;
        int blue = argb & 0xFF;

        return alpha >= MASK_MIN_A
                && red >= MASK_MIN_R
                && green <= MASK_MAX_G
                && blue >= MASK_MIN_B;
    }

    private static boolean isSimilarColor(int argb1, int argb2) {
        int alpha1 = (argb1 >>> 24) & 0xFF;
        int red1 = (argb1 >>> 16) & 0xFF;
        int green1 = (argb1 >>> 8) & 0xFF;
        int blue1 = argb1 & 0xFF;

        int alpha2 = (argb2 >>> 24) & 0xFF;
        int red2 = (argb2 >>> 16) & 0xFF;
        int green2 = (argb2 >>> 8) & 0xFF;
        int blue2 = argb2 & 0xFF;

        return Math.abs(alpha1 - alpha2) <= ALPHA_TOLERANCE
                && Math.abs(red1 - red2) <= RGB_TOLERANCE
                && Math.abs(green1 - green2) <= RGB_TOLERANCE
                && Math.abs(blue1 - blue2) <= RGB_TOLERANCE;
    }

    private static int overlayAlpha(int argb, int alpha) {
        return (argb & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    private static BufferedImage padToSize(BufferedImage source, int width, int height) {
        if (source.getWidth() == width && source.getHeight() == height) {
            return source;
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();

        return result;
    }

    private static void deleteIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static final class PreparedPaths {
        final String fileName;
        final Path baselinePath;
        final Path actualPath;
        final Path diffPath;

        PreparedPaths(String fileName, Path baselinePath, Path actualPath, Path diffPath) {
            this.fileName = fileName;
            this.baselinePath = baselinePath;
            this.actualPath = actualPath;
            this.diffPath = diffPath;
        }
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