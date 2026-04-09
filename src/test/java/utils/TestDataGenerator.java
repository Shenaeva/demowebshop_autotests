package utils;

import java.util.UUID;

public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    public static String uniqueEmail() {
        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

        return "autotest_" + suffix + "@example.com";
    }

    public static String validPassword() {
        return "Qwerty123";
    }
}