package security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordHasherTest {

    @Test
    void shouldHashAndVerifyPassword() {
        BCryptPasswordHasher hasher =
                new BCryptPasswordHasher();

        String rawPassword = "CampusUser2026!";

        String hash =
                hasher.hash(rawPassword);

        assertNotEquals(rawPassword, hash);
        assertTrue(
                hasher.matches(
                        rawPassword,
                        hash
                )
        );
    }

    @Test
    void shouldRejectInvalidHash() {
        BCryptPasswordHasher hasher =
                new BCryptPasswordHasher();

        assertFalse(
                hasher.matches(
                        "CampusUser2026!",
                        "invalid-hash"
                )
        );
    }
}