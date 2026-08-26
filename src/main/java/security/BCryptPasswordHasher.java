package security;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Arrays;

public final class BCryptPasswordHasher
        implements PasswordHasher {

    private static final int COST = 12;

    @Override
    public String hash(String rawPassword) {

        if (rawPassword == null) {
            throw new IllegalArgumentException(
                    "Le mot de passe ne peut pas être null."
            );
        }

        char[] password = rawPassword.toCharArray();

        try {
            return BCrypt.withDefaults()
                    .hashToString(COST, password);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Override
    public boolean matches(
            String rawPassword,
            String encodedPassword) {

        if (rawPassword == null
                || encodedPassword == null
                || encodedPassword.isBlank()) {
            return false;
        }

        char[] password = rawPassword.toCharArray();

        try {
            return BCrypt.verifyer()
                    .verify(
                            password,
                            encodedPassword
                    )
                    .verified;
        } catch (IllegalArgumentException ex) {
            return false;
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}