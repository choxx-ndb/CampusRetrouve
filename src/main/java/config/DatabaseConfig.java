package config;

import java.util.Map;
import java.util.Objects;

public final class DatabaseConfig {

    private static final String DB_URL = "DB_URL";
    private static final String DB_USER = "DB_USER";
    private static final String DB_PASSWORD = "DB_PASSWORD";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        this(System.getenv());
    }

    DatabaseConfig(Map<String, String> environment) {
        Objects.requireNonNull(
                environment,
                "environment ne peut pas être null"
        );

        this.url = requireNonBlank(environment, DB_URL);
        this.user = requireNonBlank(environment, DB_USER);

        String configuredPassword = environment.get(DB_PASSWORD);
        this.password = configuredPassword == null
                ? ""
                : configuredPassword;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    private static String requireNonBlank(
            Map<String, String> environment,
            String key) {

        String value = environment.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Variable d'environnement obligatoire "
                            + "manquante ou vide : "
                            + key
            );
        }

        return value.trim();
    }
}
