package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modele.Utilisateur;
import service.UtilisateurService;

import java.io.IOException;

@WebServlet("/utilisateur")
public class UtilisateurServlet extends HttpServlet {
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = getAction(request);
        switch (action) {
            case "connexion":
                forward(request, response, "/connexion.jsp");
                break;
            case "inscription":
                forward(request, response, "/inscription.jsp");
                break;
            case "logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = getAction(request);
        if ("authentifier".equals(action)) {
            authentifier(request, response);
        } else if ("inscrire".equals(action)) {
            inscrire(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/utilisateur?action=connexion");
        }
    }

    private void authentifier(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String motdepass = request.getParameter("motdepass");
        Utilisateur utilisateur = utilisateurService.authentifier(email, motdepass);
        if (utilisateur == null) {
            request.setAttribute("erreur", "Email ou mot de passe incorrect.");
            forward(request, response, "/connexion.jsp");
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", utilisateur.getId());
        session.setAttribute("userName", utilisateur.getNom());
        session.setAttribute("userRole", utilisateur.getRole());
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private void inscrire(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utilisateur utilisateur = new Utilisateur(
                request.getParameter("nom"),
                request.getParameter("email"),
                request.getParameter("motdepass")
        );
        try {
            utilisateurService.inscrire(utilisateur);
            request.setAttribute("success", "Compte crÃ©Ã©. Vous pouvez vous connecter.");
            forward(request, response, "/connexion.jsp");
        } catch (IllegalArgumentException ex) {
            request.setAttribute("erreur", ex.getMessage());
            request.setAttribute("nom", request.getParameter("nom"));
            request.setAttribute("email", request.getParameter("email"));
            forward(request, response, "/inscription.jsp");
        }
    }

    private String getAction(HttpServletRequest request) {
        String action = request.getParameter("action");
        return action == null ? "" : action;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher(page).forward(request, response);
    }
}
