package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Credentials {
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String email() {
        return require("DEMO_USER_EMAIL");
    }

    public static String password() {
        return require("DEMO_USER_PASSWORD");
    }

    private static String require(String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Value " + key + " is not set in .env"
            );
        }
        return value;
    }
}