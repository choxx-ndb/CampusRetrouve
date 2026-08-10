<%@ page import="modele.Objet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% Objet objet = (Objet) request.getAttribute("objet"); Integer userId = (Integer) session.getAttribute("userId"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Détail - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-box-open me-2"></i>Détail de l'objet</h1>
        <p class="lead">Consultez les informations et ouvrez une réclamation si nécessaire.</p>
    </div>
</header>
<main class="container pb-5">
    <% if (objet == null) { %>
        <div class="alert alert-danger">Objet introuvable.</div>
    <% } else { %>
        <div class="row g-4">
            <div class="col-lg-6">
                <img class="detail-image" src="<%= request.getContextPath() %>/<%= objet.getImagePath() %>" alt="Image objet">
            </div>
            <div class="col-lg-6">
                <div class="panel accent-card p-4">
                    <span class="badge <%= "perdue".equals(objet.getType()) ? "text-bg-danger" : "text-bg-success" %> mb-2"><%= objet.getType() %></span>
                    <h1 class="h2"><%= objet.getTitre() %></h1>
                    <p class="text-muted"><%= objet.getDescription() %></p>
                    <dl class="row">
                        <dt class="col-sm-4">Localisation</dt><dd class="col-sm-8"><%= objet.getLocalisation() %></dd>
                        <dt class="col-sm-4">Statut</dt><dd class="col-sm-8"><span class="badge badge-status"><%= objet.getStatus() %></span></dd>
                        <dt class="col-sm-4">Publié par</dt><dd class="col-sm-8"><%= objet.getProprietaireNom() %></dd>
                    </dl>
                    <% if (userId == null) { %>
                        <a class="btn btn-primary" href="<%= request.getContextPath() %>/utilisateur?action=connexion">Se connecter pour réclamer</a>
                    <% } else if ("disponible".equals(objet.getStatus()) && userId != objet.getProprietaireId()) { %>
                        <a class="btn btn-primary" href="<%= request.getContextPath() %>/reclamation?action=creer&id=<%= objet.getId() %>">Réclamer cet objet</a>
                    <% } else { %>
                        <a class="btn btn-outline-primary" href="<%= request.getContextPath() %>/reclamation?action=mes-reclamations">Voir mes discussions</a>
                    <% } %>
                </div>
            </div>
        </div>
    <% } %>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



