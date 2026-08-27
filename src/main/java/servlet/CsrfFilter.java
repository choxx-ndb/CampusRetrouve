package servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import security.CsrfTokenManager;

import java.io.IOException;
import java.util.Set;

@WebFilter(
        urlPatterns = {
                "/utilisateur",
                "/objet",
                "/reclamation",
                "/admin"
        }
)
public class CsrfFilter implements Filter {

    private static final Set<String> SAFE_METHODS =
            Set.of(
                    "GET",
                    "HEAD",
                    "OPTIONS"
            );

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        if (SAFE_METHODS.contains(
                request.getMethod())) {

            HttpSession session =
                    request.getSession(false);

            if (session != null) {
                CsrfTokenManager
                        .getOrCreate(session);
            }

            chain.doFilter(
                    servletRequest,
                    servletResponse
            );

            return;
        }

        if (!CsrfTokenManager.isValid(request)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Requête refusée : "
                            + "jeton CSRF invalide."
            );

            return;
        }

        chain.doFilter(
                servletRequest,
                servletResponse
        );
    }
}