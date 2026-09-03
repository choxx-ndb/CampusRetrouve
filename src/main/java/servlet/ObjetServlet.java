package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import modele.Objet;
import service.ObjetService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/objet")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 8 * 1024 * 1024
)
public class ObjetServlet extends HttpServlet {

    private final ObjetService objetService =
            new ObjetService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = getAction(request);

        switch (action) {

            case "liste":
                request.setAttribute(
                        "objets",
                        objetService.recupererTous()
                );
                request.setAttribute(
                        "filtre",
                        "tous"
                );
                forward(
                        request,
                        response,
                        "/objets/liste.jsp"
                );
                break;

            case "perdue":
                request.setAttribute(
                        "objets",
                        objetService.recupererParType(
                                "perdue"
                        )
                );
                request.setAttribute(
                        "filtre",
                        "perdue"
                );
                forward(
                        request,
                        response,
                        "/objets/liste.jsp"
                );
                break;

            case "trouve":
                request.setAttribute(
                        "objets",
                        objetService.recupererParType(
                                "trouve"
                        )
                );
                request.setAttribute(
                        "filtre",
                        "trouve"
                );
                forward(
                        request,
                        response,
                        "/objets/liste.jsp"
                );
                break;

            case "detail":
                int id =
                        parseInt(
                                request.getParameter("id")
                        );

                Objet objet =
                        objetService.recupererParId(id);

                if (objet == null) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/objet?action=liste"
                                    + "&erreur=Objet introuvable"
                    );

                    return;
                }

                request.setAttribute(
                        "objet",
                        objet
                );

                forward(
                        request,
                        response,
                        "/objets/detail.jsp"
                );
                break;

            case "formulaire":

                if (!isConnected(request)) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/utilisateur?action=connexion"
                    );

                    return;
                }

                forward(
                        request,
                        response,
                        "/objets/ajouter.jsp"
                );
                break;

            case "mes-annonces":

                if (!isConnected(request)) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/utilisateur?action=connexion"
                    );

                    return;
                }

                afficherMesAnnonces(
                        request,
                        response
                );
                break;

            case "modifier-formulaire":

                if (!isConnected(request)) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/utilisateur?action=connexion"
                    );

                    return;
                }

                afficherFormulaireModification(
                        request,
                        response
                );
                break;

            default:
                response.sendRedirect(
                        request.getContextPath()
                                + "/objet?action=liste"
                );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!isConnected(request)) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/utilisateur?action=connexion"
            );

            return;
        }

        String action = getAction(request);

        if ("ajouter".equals(action)) {

            ajouter(
                    request,
                    response
            );

        } else if ("modifier".equals(action)) {

            modifier(
                    request,
                    response
            );

        } else if ("supprimer".equals(action)) {

            supprimer(
                    request,
                    response
            );

        } else {

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=liste"
            );
        }
    }

    private void ajouter(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int userId =
                    getCurrentUserId(request);

            String imagePath =
                    uploadImage(
                            request.getPart("image")
                    );

            Objet objet =
                    new Objet(
                            request.getParameter("titre"),
                            request.getParameter("description"),
                            request.getParameter("type"),
                            request.getParameter("localisation"),
                            imagePath,
                            userId
                    );

            objetService.publierAnnonce(objet);

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=liste"
                            + "&success=Annonce publiÃ©e"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "erreur",
                    ex.getMessage()
            );

            conserverValeursFormulaire(request);

            forward(
                    request,
                    response,
                    "/objets/ajouter.jsp"
            );
        }
    }

    private void afficherMesAnnonces(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int userId =
                getCurrentUserId(request);

        request.setAttribute(
                "objets",
                objetService
                        .recupererParProprietaire(
                                userId
                        )
        );

        forward(
                request,
                response,
                "/objets/mes-annonces.jsp"
        );
    }

    private void afficherFormulaireModification(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int objetId =
                parseInt(
                        request.getParameter("id")
                );

        int userId =
                getCurrentUserId(request);

        try {

            Objet objet =
                    objetService
                            .recupererAnnoncePourModification(
                                    objetId,
                                    userId
                            );

            request.setAttribute(
                    "objet",
                    objet
            );

            forward(
                    request,
                    response,
                    "/objets/modifier.jsp"
            );

        } catch (IllegalArgumentException ex) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&error=not-allowed"
            );
        }
    }

    private void modifier(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int objetId =
                parseInt(
                        request.getParameter("id")
                );

        int userId =
                getCurrentUserId(request);

        try {

            objetService.modifierAnnonce(
                    objetId,
                    userId,
                    request.getParameter("titre"),
                    request.getParameter("description"),
                    request.getParameter("type"),
                    request.getParameter("localisation")
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&success=updated"
            );

        } catch (IllegalArgumentException ex) {

            try {

                Objet objet =
                        objetService
                                .recupererAnnoncePourModification(
                                        objetId,
                                        userId
                                );

                objet.setTitre(
                        request.getParameter("titre")
                );

                objet.setDescription(
                        request.getParameter("description")
                );

                objet.setType(
                        request.getParameter("type")
                );

                objet.setLocalisation(
                        request.getParameter("localisation")
                );

                request.setAttribute(
                        "objet",
                        objet
                );

                request.setAttribute(
                        "erreur",
                        ex.getMessage()
                );

                forward(
                        request,
                        response,
                        "/objets/modifier.jsp"
                );

            } catch (IllegalArgumentException denied) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/objet?action=mes-annonces"
                                + "&error=not-allowed"
                );
            }
        }
    }

    private void supprimer(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        int objetId =
                parseInt(
                        request.getParameter("id")
                );

        int userId =
                getCurrentUserId(request);

        try {

            objetService.supprimerAnnonce(
                    objetId,
                    userId
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&success=deleted"
            );

        } catch (IllegalArgumentException ex) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&error=not-allowed"
            );
        }
    }

    private void conserverValeursFormulaire(
            HttpServletRequest request) {

        request.setAttribute(
                "titre",
                request.getParameter("titre")
        );

        request.setAttribute(
                "description",
                request.getParameter("description")
        );

        request.setAttribute(
                "type",
                request.getParameter("type")
        );

        request.setAttribute(
                "localisation",
                request.getParameter("localisation")
        );
    }

    private String uploadImage(
            Part part)
            throws IOException {

        if (part == null
                || part.getSize() == 0) {

            return "uploads/default-object.jpeg";
        }

        String submitted =
                Paths.get(
                        part.getSubmittedFileName()
                )
                .getFileName()
                .toString();

        if (submitted == null
                || submitted.trim().isEmpty()) {

            return "uploads/default-object.jpeg";
        }

        String extension = "";

        int dot =
                submitted.lastIndexOf('.');

        if (dot >= 0) {
            extension =
                    submitted
                            .substring(dot)
                            .toLowerCase();
        }

        if (!extension.matches(
                "\\.(jpg|jpeg|png|gif|webp)")) {

            throw new IllegalArgumentException(
                    "Format image non autorisée."
            );
        }

        String uploadDirPath =
                getServletContext()
                        .getRealPath("/uploads");

        File uploadDir =
                new File(uploadDirPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String filename =
                UUID.randomUUID()
                        + extension;

        part.write(
                new File(
                        uploadDir,
                        filename
                )
                .getAbsolutePath()
        );

        return "uploads/" + filename;
    }

    private boolean isConnected(
            HttpServletRequest request) {

        return request.getSession(false) != null
                && request
                        .getSession(false)
                        .getAttribute("userId")
                        != null;
    }

    private int getCurrentUserId(
            HttpServletRequest request) {

        return (Integer) request
                .getSession(false)
                .getAttribute("userId");
    }

    private String getAction(
            HttpServletRequest request) {

        String action =
                request.getParameter("action");

        return action == null
                ? ""
                : action;
    }

    private int parseInt(
            String value) {

        try {

            return Integer.parseInt(value);

        } catch (Exception e) {

            return 0;
        }
    }

    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException, IOException {

        request
                .getRequestDispatcher(page)
                .forward(
                        request,
                        response
                );
    }
}
