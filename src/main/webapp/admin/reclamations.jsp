<%@ page import="java.util.List" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="modele.Reclamation" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<Reclamation> reclamations = (List<Reclamation>) request.getAttribute("reclamations");
    String filtre = (String) request.getAttribute("filtre");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Réclamations - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-list-check me-2"></i>Supervision des réclamations</h1>
        <p class="lead">Lecture et audit des échanges entre utilisateurs.</p>
    </div>
</header>
<main class="container pb-5">
    <div class="btn-group mb-4">
        <a class="btn <%= "toutes".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/admin?action=reclamations">Toutes</a>
        <a class="btn <%= "en_attente".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/admin?action=reclamations&status=en_attente">En attente</a>
        <a class="btn <%= "approuve".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/admin?action=reclamations&status=approuve">Approuvées</a>
        <a class="btn <%= "rejete".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/admin?action=reclamations&status=rejete">Refusées</a>
    </div>
    <div class="panel accent-card p-3 table-responsive">
        <table class="table align-middle">
            <thead><tr><th>Objet</th><th>Demandeur</th><th>Propriétaire</th><th>Statut</th><th>Actions</th></tr></thead>
            <tbody>
            <% if (reclamations == null || reclamations.isEmpty()) { %>
                <tr><td colspan="5" class="text-muted">Aucune réclamation.</td></tr>
            <% } else { for (Reclamation r : reclamations) { %>
                <tr>
                    <td><%= Encode.forHtml(r.getObjetTitre()) %></td>
                    <td><%= Encode.forHtml(r.getDemandeurNom()) %></td>
                    <td><%= Encode.forHtml(r.getProprietaireNom()) %></td>
                    <td><span class="badge badge-status"><%= Encode.forHtml(r.getStatus()) %></span></td>
                    <td><a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/reclamation?action=detail&id=<%= r.getId() %>">Voir le tchat</a></td>
                </tr>
            <% }} %>
            </tbody>
        </table>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

