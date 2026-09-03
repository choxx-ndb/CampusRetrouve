<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="modele.Objet" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%
    Objet objet =
            (Objet) request.getAttribute("objet");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modifier l'annonce - CampusRetrouve</title>

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
            <i class="fa-solid fa-pen me-2"></i>
            Modifier mon annonce
        </h1>
    </div>
</header>

<main class="container pb-5 narrow-page">

    <% if (objet == null) { %>

        <div class="alert alert-danger">
            Annonce introuvable.
        </div>

    <% } else { %>

        <div class="form-card p-4">

            <% if (request.getAttribute("erreur") != null) { %>
                <div class="alert alert-danger">
                    <%= Encode.forHtml(
                            request
                                    .getAttribute("erreur")
                                    .toString()
                    ) %>
                </div>
            <% } %>

            <form
                method="post"
                action="<%= request.getContextPath() %>/objet">

                <input
                    type="hidden"
                    name="action"
                    value="modifier">

                <input
                    type="hidden"
                    name="id"
                    value="<%= objet.getId() %>">

                <div class="mb-3">
                    <label class="form-label">
                        Titre
                    </label>

                    <input
                        class="form-control"
                        name="titre"
                        value="<%= Encode.forHtmlAttribute(
                                objet.getTitre()
                        ) %>"
                        required
                        minlength="3"
                        maxlength="255">
                </div>

                <div class="mb-3">
                    <label class="form-label">
                        Description
                    </label>

                    <textarea
                        class="form-control"
                        name="description"
                        rows="4"
                        maxlength="2000"><%= Encode.forHtml(
                                objet.getDescription()
                        ) %></textarea>
                </div>

                <div class="mb-3">
                    <label class="form-label d-block">
                        Type
                    </label>

                    <div class="btn-group" role="group">

                        <input
                            class="btn-check"
                            type="radio"
                            name="type"
                            id="typePerdue"
                            value="perdue"
                            <%= "perdue".equals(
                                    objet.getType()
                            ) ? "checked" : "" %>
                            required>

                        <label
                            class="btn btn-outline-primary"
                            for="typePerdue">
                            Perdu
                        </label>

                        <input
                            class="btn-check"
                            type="radio"
                            name="type"
                            id="typeTrouve"
                            value="trouve"
                            <%= "trouve".equals(
                                    objet.getType()
                            ) ? "checked" : "" %>
                            required>

                        <label
                            class="btn btn-outline-primary"
                            for="typeTrouve">
                            Trouvé
                        </label>

                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label">
                        Localisation
                    </label>

                    <input
                        class="form-control"
                        name="localisation"
                        value="<%= Encode.forHtmlAttribute(
                                objet.getLocalisation()
                        ) %>"
                        maxlength="255">
                </div>

                <input
                    type="hidden"
                    name="_csrf"
                    value="<%= session.getAttribute("csrfToken") %>">

                <div class="d-flex gap-2">

                    <button
                        type="submit"
                        class="btn btn-primary">

                        <i class="fa-solid fa-floppy-disk me-1"></i>
                        Enregistrer
                    </button>

                    <a
                        class="btn btn-outline-secondary"
                        href="<%= request.getContextPath() %>/objet?action=mes-annonces">

                        Annuler
                    </a>

                </div>

            </form>
        </div>

    <% } %>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>