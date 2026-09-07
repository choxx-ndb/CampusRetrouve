package servlet;

import jakarta.servlet.ServletException;

import service.ImageStorageService;
import java.nio.file.Path;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import modele.Objet;
import service.ObjetService;

import java.io.IOException;


@WebServlet("/objet")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 8 * 1024 * 1024
)
public class ObjetServlet extends HttpServlet {

    private final ObjetService objetService =
            new ObjetService();
    private final ImageStorageService imageStorageService =
            new ImageStorageService();

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

        String storedImagePath = null;

        try {

            int userId =
                    getCurrentUserId(request);

            storedImagePath =
                    imageStorageService
                            .storeIfPresent(
                                    request.getPart("image"),
                                    getUploadDirectory()
                            );

            String imagePath =
                    storedImagePath == null
                            ? ImageStorageService.DEFAULT_IMAGE_PATH
                            : storedImagePath;

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
                            + "&success=created"
            );
        } catch (IllegalStateException ex) {

            deleteImageQuietly(storedImagePath);

            request.setAttribute(
                    "erreur",
                    "L'image ne doit pas dépasser 5 Mo."
            );

            conserverValeursFormulaire(request);

            forward(
                    request,
                    response,
                    "/objets/ajouter.jsp"
            );

        } catch (IllegalArgumentException ex) {

            deleteImageQuietly(storedImagePath);

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

        } catch (RuntimeException ex) {

            deleteImageQuietly(storedImagePath);

            throw ex;
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

        Objet objet;

        try {

            objet =
                    objetService
                            .recupererAnnoncePourModification(
                                    objetId,
                                    userId
                            );

        } catch (IllegalArgumentException ex) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&error=not-allowed"
            );

            return;
        }

        String nouvelleImagePath = null;

        try {

            nouvelleImagePath =
                    imageStorageService
                            .storeIfPresent(
                                    request.getPart("image"),
                                    getUploadDirectory()
                            );

            if (nouvelleImagePath == null) {

                objetService.modifierAnnonce(
                        objetId,
                        userId,
                        request.getParameter("titre"),
                        request.getParameter("description"),
                        request.getParameter("type"),
                        request.getParameter("localisation")
                );

            } else {

                String ancienneImagePath =
                        objetService
                                .modifierAnnonceAvecImage(
                                        objetId,
                                        userId,
                                        request.getParameter("titre"),
                                        request.getParameter("description"),
                                        request.getParameter("type"),
                                        request.getParameter("localisation"),
                                        nouvelleImagePath
                                );

                deleteImageQuietly(
                        ancienneImagePath
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/objet?action=mes-annonces"
                            + "&success=updated"
            );

        } catch (IllegalStateException ex) {

            deleteImageQuietly(
                    nouvelleImagePath
            );

            conserverValeursModification(
                    request,
                    objet,
                    "L'image ne doit pas dépasser 5 Mo."
            );

            forward(
                    request,
                    response,
                    "/objets/modifier.jsp"
            );

        } catch (IllegalArgumentException ex) {

            deleteImageQuietly(
                    nouvelleImagePath
            );

            conserverValeursModification(
                    request,
                    objet,
                    ex.getMessage()
            );

            forward(
                    request,
                    response,
                    "/objets/modifier.jsp"
            );

        } catch (RuntimeException ex) {

            deleteImageQuietly(
                    nouvelleImagePath
            );

            throw ex;
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

            String imagePath =
                    objetService.supprimerAnnonce(
                            objetId,
                            userId
                    );

            deleteImageQuietly(
                    imagePath
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
    private void conserverValeursModification(
            HttpServletRequest request,
            Objet objet,
            String erreur) {

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
                erreur
        );
    }

    private Path getUploadDirectory() {

        String path =
                getServletContext()
                        .getRealPath("/uploads");

        if (path == null) {

            throw new IllegalStateException(
                    "Répertoire d'upload indisponible."
            );
        }

        return Path.of(path);
    }

    private void deleteImageQuietly(
            String imagePath) {

        if (imagePath == null) {
            return;
        }

        try {

            imageStorageService.deleteManagedImage(
                    imagePath,
                    getUploadDirectory()
            );

        } catch (Exception ex) {

            getServletContext().log(
                    "Impossible de supprimer l'image "
                            + imagePath,
                    ex
            );
        }
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
