package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import service.AdminService;
import service.ReclamationService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService =
            new AdminService();

    private final ReclamationService reclamationService =
            new ReclamationService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/utilisateur?action=connexion"
            );

            return;
        }

        String action = getAction(request);

        try {

            switch (action) {

                case "dashboard":
                case "":

                    request.setAttribute(
                            "stats",
                            adminService.getStatistiquesGlobales()
                    );

                    forward(
                            request,
                            response,
                            "/admin/dashboard.jsp"
                    );

                    break;

                case "utilisateurs":

                    request.setAttribute(
                            "utilisateurs",
                            adminService.listerUtilisateurs()
                    );

                    forward(
                            request,
                            response,
                            "/admin/utilisateurs.jsp"
                    );

                    break;

                case "reclamations":

                    String status =
                            request.getParameter("status");

                    request.setAttribute(
                            "filtre",
                            status == null
                                    ? "toutes"
                                    : status
                    );

                    request.setAttribute(
                            "reclamations",
                            reclamationService
                                    .recupererParStatut(status)
                    );

                    forward(
                            request,
                            response,
                            "/admin/reclamations.jsp"
                    );

                    break;

                case "objets":

                    request.setAttribute(
                            "objets",
                            adminService.listerObjets()
                    );

                    forward(
                            request,
                            response,
                            "/admin/objets.jsp"
                    );

                    break;

                default:

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=dashboard"
                    );
            }

        } catch (IllegalArgumentException ex) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin?action=dashboard"
                            + "&erreur="
                            + encodeQueryParam(
                                    ex.getMessage()
                            )
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/utilisateur?action=connexion"
            );

            return;
        }

        String action = getAction(request);

        try {

            switch (action) {

                case "promouvoir":

                    adminService.promouvoirEnAdmin(
                            parseInt(
                                    request.getParameter("id")
                            )
                    );

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=utilisateurs"
                                    + "&success="
                                    + encodeQueryParam(
                                            "Utilisateur promu"
                                    )
                    );

                    break;

                case "retrograder":

                    adminService.retrograderEnUser(
                            parseInt(
                                    request.getParameter("id")
                            ),
                            getCurrentAdminId(request)
                    );

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=utilisateurs"
                                    + "&success="
                                    + encodeQueryParam(
                                            "Utilisateur rétrogradé"
                                    )
                    );

                    break;

                case "supprimer-user":

                    adminService.supprimerUtilisateur(
                            parseInt(
                                    request.getParameter("id")
                            ),
                            getCurrentAdminId(request)
                    );

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=utilisateurs"
                                    + "&success="
                                    + encodeQueryParam(
                                            "Utilisateur supprimé"
                                    )
                    );

                    break;

                case "supprimer-objet":

                    adminService.supprimerAnnonce(
                            parseInt(
                                    request.getParameter("id")
                            )
                    );

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=objets"
                                    + "&success="
                                    + encodeQueryParam(
                                            "Annonce supprimée"
                                    )
                    );

                    break;

                default:

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/admin?action=dashboard"
                    );
            }

        } catch (IllegalArgumentException ex) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin?action=dashboard"
                            + "&erreur="
                            + encodeQueryParam(
                                    ex.getMessage()
                            )
            );
        }
    }

    private int getCurrentAdminId(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        Object value =
                session == null
                        ? null
                        : session.getAttribute("userId");

        if (!(value instanceof Integer adminId)
                || adminId <= 0) {

            throw new IllegalArgumentException(
                    "Session administrateur invalide."
            );
        }

        return adminId;
    }

    private boolean isAdmin(
            HttpServletRequest request) {

        HttpSession session =
                request.getSession(false);

        return session != null
                && "admin".equals(
                        session.getAttribute("userRole")
                );
    }

    private String getAction(
            HttpServletRequest request) {

        String action =
                request.getParameter("action");

        return action == null ? "" : action;
    }

    private int parseInt(
            String value) {

        try {

            return Integer.parseInt(value);

        } catch (Exception e) {

            return 0;
        }
    }

    private String encodeQueryParam(
            String value) {

        return URLEncoder.encode(
                value == null ? "" : value,
                StandardCharsets.UTF_8
        );
    }

    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException, IOException {

        request.getRequestDispatcher(page)
                .forward(request, response);
    }
}