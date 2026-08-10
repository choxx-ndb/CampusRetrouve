<%@ page import="java.util.List" %>
<%@ page import="modele.Reclamation" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<Reclamation> envoyees = (List<Reclamation>) request.getAttribute("envoyees");
    List<Reclamation> recues = (List<Reclamation>) request.getAttribute("recues");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mes réclamations - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-clipboard-list me-2"></i>Mes réclamations</h1>
        <p class="lead">Suivez vos demandes envoyées et les demandes reçues sur vos annonces.</p>
    </div>
</header>
<main class="container pb-5">
    <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#envoyees" type="button">Mes demandes envoyées</button></li>
        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#recues" type="button">Demandes reçues</button></li>
    </ul>
    <div class="tab-content panel accent-card p-3 border-top-0">
        <div class="tab-pane fade show active" id="envoyees">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead><tr><th>Objet</th><th>Statut</th><th>Actions</th></tr></thead>
                    <tbody>
                    <% if (envoyees == null || envoyees.isEmpty()) { %>
                        <tr><td colspan="3" class="text-muted">Aucune demande envoyée.</td></tr>
                    <% } else { for (Reclamation r : envoyees) { %>
                        <tr>
                            <td><%= r.getObjetTitre() %></td>
                            <td><span class="badge badge-status"><%= r.getStatus() %></span></td>
                            <td><a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/reclamation?action=detail&id=<%= r.getId() %>">Ouvrir la discussion</a></td>
                        </tr>
                    <% }} %>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="tab-pane fade" id="recues">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead><tr><th>Objet</th><th>Demandeur</th><th>Statut</th><th>Actions</th></tr></thead>
                    <tbody>
                    <% if (recues == null || recues.isEmpty()) { %>
                        <tr><td colspan="4" class="text-muted">Aucune demande reçue.</td></tr>
                    <% } else { for (Reclamation r : recues) { %>
                        <tr>
                            <td><%= r.getObjetTitre() %></td>
                            <td><%= r.getDemandeurNom() %></td>
                            <td><span class="badge badge-status"><%= r.getStatus() %></span></td>
                            <td class="d-flex flex-wrap gap-2">
                                <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/reclamation?action=detail&id=<%= r.getId() %>">Discussion</a>
                                <% if ("en_attente".equals(r.getStatus())) { %>
                                    <a class="btn btn-sm btn-success" href="<%= request.getContextPath() %>/reclamation?action=traiter&id=<%= r.getId() %>&decision=approuve">Approuver</a>
                                    <a class="btn btn-sm btn-danger" href="<%= request.getContextPath() %>/reclamation?action=traiter&id=<%= r.getId() %>&decision=rejete">Refuser</a>
                                <% } %>
                            </td>
                        </tr>
                    <% }} %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



