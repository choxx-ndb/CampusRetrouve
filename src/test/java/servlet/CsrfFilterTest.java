package servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import security.CsrfTokenManager;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsrfFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain chain;

    private CsrfFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CsrfFilter();
    }

    @Test
    void shouldAllowGetRequest()
            throws Exception {

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getSession(false))
                .thenReturn(null);

        filter.doFilter(
                request,
                response,
                chain
        );

        verify(chain).doFilter(
                request,
                response
        );

        verify(
                response,
                never()
        ).sendError(
                anyInt(),
                anyString()
        );
    }

    @Test
    void shouldAllowPostWithValidCsrfToken()
            throws Exception {

        when(request.getMethod())
                .thenReturn("POST");

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                CsrfTokenManager.SESSION_ATTRIBUTE
        )).thenReturn("valid-token");

        when(request.getParameter(
                CsrfTokenManager.REQUEST_PARAMETER
        )).thenReturn("valid-token");

        filter.doFilter(
                request,
                response,
                chain
        );

        verify(chain).doFilter(
                request,
                response
        );

        verify(
                response,
                never()
        ).sendError(
                anyInt(),
                anyString()
        );
    }

    @Test
    void shouldRejectPostWithInvalidCsrfToken()
            throws Exception {

        when(request.getMethod())
                .thenReturn("POST");

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                CsrfTokenManager.SESSION_ATTRIBUTE
        )).thenReturn("expected-token");

        when(request.getParameter(
                CsrfTokenManager.REQUEST_PARAMETER
        )).thenReturn("wrong-token");

        filter.doFilter(
                request,
                response,
                chain
        );

        verify(response).sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Requête refusée : jeton CSRF invalide."
        );

        verifyNoInteractions(chain);
    }
}