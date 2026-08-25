package config;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseConfigTest {

    @Test
    void shouldLoadValidDatabaseConfiguration() {
        Map<String, String> environment = Map.of(
                "DB_URL",
                " jdbc:mysql://localhost:3306/campus_retrouve ",
                "DB_USER",
                " root ",
                "DB_PASSWORD",
                "secret123"
        );

        DatabaseConfig config =
                new DatabaseConfig(environment);

        assertAll(
                () -> assertEquals(
                        "jdbc:mysql://localhost:3306/campus_retrouve",
                        config.getUrl()
                ),
                () -> assertEquals(
                        "root",
                        config.getUser()
                ),
                () -> assertEquals(
                        "secret123",
                        config.getPassword()
                )
        );
    }

    @Test
    void shouldRejectMissingDatabaseUrl() {
        Map<String, String> environment = Map.of(
                "DB_USER",
                "root"
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> new DatabaseConfig(environment)
                );

        assertEquals(
                "Variable d'environnement obligatoire "
                        + "manquante ou vide : DB_URL",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankDatabaseUser() {
        Map<String, String> environment = Map.of(
                "DB_URL",
                "jdbc:mysql://localhost:3306/campus_retrouve",
                "DB_USER",
                "   "
        );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> new DatabaseConfig(environment)
                );

        assertEquals(
                "Variable d'environnement obligatoire "
                        + "manquante ou vide : DB_USER",
                exception.getMessage()
        );
    }

    @Test
    void shouldUseEmptyPasswordWhenPasswordIsNotConfigured() {
        Map<String, String> environment = Map.of(
                "DB_URL",
                "jdbc:mysql://localhost:3306/campus_retrouve",
                "DB_USER",
                "root"
        );

        DatabaseConfig config =
                new DatabaseConfig(environment);

        assertEquals("", config.getPassword());
    }
}
