package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminService;
import service.ReclamationService;

import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final ReclamationService reclamationService = new ReclamationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/utilisateur?action=connexion");
            return;
        }
        String action = getAction(request);
        try {
            switch (action) {
                case "dashboard":
                case "":
                    request.setAttribute("stats", adminService.getStatistiquesGlobales());
                    forward(request, response, "/admin/dashboard.jsp");
                    break;
                case "utilisateurs":
                    request.setAttribute("utilisateurs", adminService.listerUtilisateurs());
                    forward(request, response, "/admin/utilisateurs.jsp");
                    break;
                case "reclamations":
                    String status = request.getParameter("status");
                    request.setAttribute("filtre", status == null ? "toutes" : status);
                    request.setAttribute("reclamations", reclamationService.recupererParStatut(status));
                    forward(request, response, "/admin/reclamations.jsp");
                    break;
                case "objets":
                    request.setAttribute("objets", adminService.listerObjets());
                    forward(request, response, "/admin/objets.jsp");
                    break;
                case "promouvoir":
                    adminService.promouvoirEnAdmin(parseInt(request.getParameter("id")));
                    response.sendRedirect(request.getContextPath() + "/admin?action=utilisateurs&success=Utilisateur promu");
                    break;
                case "retrograder":
                    adminService.retrograderEnUser(parseInt(request.getParameter("id")));
                    response.sendRedirect(request.getContextPath() + "/admin?action=utilisateurs&success=Utilisateur rÃ©trogradÃ©");
                    break;
                case "supprimer-user":
                    int adminId = (Integer) request.getSession().getAttribute("userId");
                    adminService.supprimerUtilisateur(parseInt(request.getParameter("id")), adminId);
                    response.sendRedirect(request.getContextPath() + "/admin?action=utilisateurs&success=Utilisateur supprimÃ©");
                    break;
                case "supprimer-objet":
                    adminService.supprimerAnnonce(parseInt(request.getParameter("id")));
                    response.sendRedirect(request.getContextPath() + "/admin?action=objets&success=Annonce supprimÃ©e");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
            }
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&erreur=" + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private boolean isAdmin(HttpServletRequest request) {
        return request.getSession(false) != null && "admin".equals(request.getSession(false).getAttribute("userRole"));
    }

    private String getAction(HttpServletRequest request) {
        String action = request.getParameter("action");
        return action == null ? "" : action;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher(page).forward(request, response);
    }
}