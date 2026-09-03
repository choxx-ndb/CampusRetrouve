<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modele.Objet" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%
    List<Objet> objets =
            (List<Objet>) request.getAttribute("objets");

    String success =
            request.getParameter("success");

    String error =
            request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Mes annonces - CampusRetrouve</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link href="<%= request.getContextPath() %>/css/style.css"
          rel="stylesheet">
</head>

<body>

<jsp:include page="/navbar.jsp"/>

<header class="page-header">
    <div class="container">
        <h1>
            <i class="fa-solid fa-box me-2"></i>
            Mes annonces
        </h1>

        <p class="lead">
            Gérez les annonces que vous avez publiées.
        </p>
    </div>
</header>

<main class="container pb-5">

    <% if ("updated".equals(success)) { %>
        <div class="alert alert-success">
            Annonce modifiée avec succès.
        </div>
    <% } %>

    <% if ("deleted".equals(success)) { %>
        <div class="alert alert-success">
            Annonce supprimée avec succès.
        </div>
    <% } %>

    <% if ("not-allowed".equals(error)) { %>
        <div class="alert alert-danger">
            Cette opération n'est pas autorisée.
        </div>
    <% } %>

    <div class="d-flex justify-content-end mb-3">
        <a
            class="btn btn-primary"
            href="<%= request.getContextPath() %>/objet?action=formulaire">

            <i class="fa-solid fa-plus me-1"></i>
            Ajouter une annonce
        </a>
    </div>

    <div class="panel p-4">

        <% if (objets == null || objets.isEmpty()) { %>

            <div class="text-muted">
                Vous n'avez aucune annonce.
            </div>

        <% } else { %>

            <div class="table-responsive">

                <table class="table align-middle">

                    <thead>
                        <tr>
                            <th>Titre</th>
                            <th>Type</th>
                            <th>Localisation</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>

                    <% for (Objet objet : objets) { %>

                        <tr>

                            <td>
                                <%= Encode.forHtml(
                                        objet.getTitre()
                                ) %>
                            </td>

                            <td>
                                <%= Encode.forHtml(
                                        objet.getType()
                                ) %>
                            </td>

                            <td>
                                <%= Encode.forHtml(
                                        objet.getLocalisation()
                                ) %>
                            </td>

                            <td>
                                <span class="badge badge-status">
                                    <%= Encode.forHtml(
                                            objet.getStatus()
                                    ) %>
                                </span>
                            </td>

                            <td>
                                <div class="d-flex flex-wrap gap-2">

                                    <a
                                        class="btn btn-sm btn-outline-primary"
                                        href="<%= request.getContextPath() %>/objet?action=detail&id=<%= objet.getId() %>">

                                        Détails
                                    </a>

                                    <% if ("disponible".equals(
                                            objet.getStatus()
                                    )) { %>

                                        <a
                                            class="btn btn-sm btn-warning"
                                            href="<%= request.getContextPath() %>/objet?action=modifier-formulaire&id=<%= objet.getId() %>">

                                            Modifier
                                        </a>

                                        <form
                                            method="post"
                                            action="<%= request.getContextPath() %>/objet"
                                            onsubmit="return confirm('Supprimer cette annonce ?')">

                                            <input
                                                type="hidden"
                                                name="action"
                                                value="supprimer">

                                            <input
                                                type="hidden"
                                                name="id"
                                                value="<%= objet.getId() %>">

                                            <input
                                                type="hidden"
                                                name="_csrf"
                                                value="<%= session.getAttribute("csrfToken") %>">

                                            <button
                                                type="submit"
                                                class="btn btn-sm btn-danger">

                                                Supprimer
                                            </button>

                                        </form>

                                    <% } %>

                                </div>
                            </td>

                        </tr>

                    <% } %>

                    </tbody>
                </table>

            </div>

        <% } %>

    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
