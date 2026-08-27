package security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class CsrfTokenManager {

    public static final String SESSION_ATTRIBUTE =
            "csrfToken";

    public static final String REQUEST_PARAMETER =
            "_csrf";

    private static final int TOKEN_SIZE_BYTES = 32;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private CsrfTokenManager() {
    }

    public static String getOrCreate(
            HttpSession session) {

        Objects.requireNonNull(
                session,
                "session ne peut pas être null"
        );

        Object existing =
                session.getAttribute(
                        SESSION_ATTRIBUTE
                );

        if (existing instanceof String token
                && !token.isBlank()) {
            return token;
        }

        return rotate(session);
    }

    public static String rotate(
            HttpSession session) {

        Objects.requireNonNull(
                session,
                "session ne peut pas être null"
        );

        byte[] randomBytes =
                new byte[TOKEN_SIZE_BYTES];

        RANDOM.nextBytes(randomBytes);

        String token =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                randomBytes
                        );

        session.setAttribute(
                SESSION_ATTRIBUTE,
                token
        );

        return token;
    }

    public static boolean isValid(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return false;
        }

        Object expectedValue =
                session.getAttribute(
                        SESSION_ATTRIBUTE
                );

        String received =
                request.getParameter(
                        REQUEST_PARAMETER
                );

        if (!(expectedValue instanceof String expected)
                || expected.isBlank()
                || received == null
                || received.isBlank()) {

            return false;
        }

        return MessageDigest.isEqual(
                expected.getBytes(
                        StandardCharsets.UTF_8
                ),
                received.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }
}