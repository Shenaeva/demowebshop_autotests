package config;

public final class TestConfig {
    private TestConfig() {}

    public static String baseUrl() {
        return System.getProperty("baseUrl", "https://demowebshop.tricentis.com");
    }
    public static boolean headless() {return Boolean.parseBoolean(System.getProperty("headless", "true"));
    }
    public static String browser() {return System.getProperty("browser", "chromium");
    }
}