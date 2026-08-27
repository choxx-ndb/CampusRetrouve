package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modele.Objet;
import modele.Reclamation;
import service.ObjetService;
import service.ReclamationService;

import java.io.IOException;

@WebServlet("/reclamation")
public class ReclamationServlet extends HttpServlet {
    private final ReclamationService reclamationService = new ReclamationService();
    private final ObjetService objetService = new ObjetService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!isConnected(request)) {
            response.sendRedirect(request.getContextPath() + "/utilisateur?action=connexion");
            return;
        }
        String action = getAction(request);
        switch (action) {
            case "creer":
                afficherFormulaireCreation(request, response);
                break;
            case "mes-reclamations":
                afficherMesReclamations(request, response);
                break;
            case "detail":
                afficherDetailDiscussion(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reclamation?action=mes-reclamations");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!isConnected(request)) {
            response.sendRedirect(request.getContextPath() + "/utilisateur?action=connexion");
            return;
        }
        String action = getAction(request);
        if ("creer".equals(action)) {
            creerReclamation(request, response);
        } else if ("envoyer-message".equals(action)) {
            envoyerMessage(request, response);
        } else if ("traiter".equals(action)) {

            traiterReclamation(
                    request,
                    response
            );

        } else {
            response.sendRedirect(request.getContextPath() + "/reclamation?action=mes-reclamations");
        }
    }

    private void afficherFormulaireCreation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Objet objet = objetService.recupererParId(parseInt(request.getParameter("id")));
        if (objet == null) {
            response.sendRedirect(request.getContextPath() + "/objet?action=liste&erreur=Objet introuvable");
            return;
        }
        request.setAttribute("objet", objet);
        forward(request, response, "/reclamations/creer.jsp");
    }

    private void creerReclamation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        try {
            Reclamation reclamation = new Reclamation(parseInt(request.getParameter("objetId")), userId, request.getParameter("message"));
            reclamationService.creerReclamation(reclamation);
            response.sendRedirect(request.getContextPath() + "/reclamation?action=mes-reclamations&success=Demande envoyÃ©e");
        } catch (IllegalArgumentException ex) {
            Objet objet = objetService.recupererParId(parseInt(request.getParameter("objetId")));
            request.setAttribute("objet", objet);
            request.setAttribute("erreur", ex.getMessage());
            forward(request, response, "/reclamations/creer.jsp");
        }
    }

    private void afficherMesReclamations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        request.setAttribute("envoyees", reclamationService.recupererHistoriqueEtudiant(userId));
        request.setAttribute("recues", reclamationService.recupererReclamationsRecuesPourUtilisateur(userId));
        forward(request, response, "/reclamations/mes-reclamation.jsp");
    }

    private void afficherDetailDiscussion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("userRole");
        Reclamation reclamation = reclamationService.recupererParId(parseInt(request.getParameter("id")));
        if (!reclamationService.peutVoirDiscussion(reclamation, userId, role)) {
            response.sendRedirect(request.getContextPath() + "/reclamation?action=mes-reclamation&erreur=AccÃ¨s refusÃ©");
            return;
        }
        request.setAttribute("reclamation", reclamation);
        request.setAttribute("objet", objetService.recupererParId(reclamation.getObjetId()));
        request.setAttribute("messages", reclamationService.recupererMessagesDiscussion(reclamation.getId()));
        forward(request, response, "/reclamations/detail-message.jsp");
    }

    private void envoyerMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        int reclamationId = parseInt(request.getParameter("reclamationId"));
        try {
            reclamationService.enregistrerMessage(reclamationId, userId, request.getParameter("contenu"));
            response.sendRedirect(request.getContextPath() + "/reclamation?action=detail&id=" + reclamationId);
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/reclamation?action=detail&id=" + reclamationId + "&erreur=" + ex.getMessage());
        }
    }

    private void traiterReclamation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = (Integer) request.getSession().getAttribute("userId");
        int reclamationId = parseInt(request.getParameter("id"));
        try {
            reclamationService.traiterReclamation(reclamationId, userId, request.getParameter("decision"));
            response.sendRedirect(request.getContextPath() + "/reclamation?action=mes-reclamation&success=RÃ©clamation traitÃ©e");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/reclamation?action=detail&id=" + reclamationId + "&erreur=" + ex.getMessage());
        }
    }

    private boolean isConnected(HttpServletRequest request) {
        return request.getSession(false) != null && request.getSession(false).getAttribute("userId") != null;
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