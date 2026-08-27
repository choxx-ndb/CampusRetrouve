<%@ page import="java.util.List" %>
<%@ page import="modele.Objet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% List<Objet> objets = (List<Objet>) request.getAttribute("objets"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Objets - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-boxes-stacked me-2"></i>Modération des annonces</h1>
        <p class="lead">Consulter et supprimer les annonces inappropriées.</p>
    </div>
</header>
<main class="container pb-5">
    <div class="panel accent-card p-3 table-responsive">
        <table class="table align-middle">
            <thead><tr><th>Titre</th><th>Auteur</th><th>Type</th><th>Statut</th><th>Actions</th></tr></thead>
            <tbody>
            <% if (objets == null || objets.isEmpty()) { %>
                <tr><td colspan="5" class="text-muted">Aucun objet.</td></tr>
            <% } else { for (Objet o : objets) { %>
                <tr>
                    <td><%= o.getTitre() %></td>
                    <td><%= o.getProprietaireNom() %></td>
                    <td><%= o.getType() %></td>
                    <td><span class="badge badge-status"><%= o.getStatus() %></span></td>
                    <td class="d-flex flex-wrap gap-2">
                        <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/objet?action=detail&id=<%= o.getId() %>">Détails</a>
                        <form method="post"
                              action="<%= request.getContextPath() %>/admin"
                              onsubmit="return confirm('Supprimer cette annonce?')">

                            <input type="hidden" name="action" value="supprimer-objet">
                            <input type="hidden" name="id" value="<%= o.getId() %>">
                            <input type="hidden"
                                   name="_csrf"
                                   value="<%= session.getAttribute("csrfToken") %>">

                            <button class="btn btn-sm btn-danger"
                                    type="submit">
                                Supprimer
                            </button>
                        </form>
                    </td>
                </tr>
            <% }} %>
            </tbody>
        </table>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

