<%@ page import="modele.Objet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% Objet objet = (Objet) request.getAttribute("objet"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Réclamer - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-hand-paper me-2"></i>Réclamer cet objet</h1>
        <p class="lead">Expliquez pourquoi cet objet vous appartient.</p>
    </div>
</header>
<main class="container pb-5">
    <% if (request.getAttribute("erreur") != null) { %><div class="alert alert-danger"><%= request.getAttribute("erreur") %></div><% } %>
    <div class="row g-4">
        <div class="col-lg-5">
            <div class="panel accent-card p-4">
                <img class="detail-image mb-3" src="<%= request.getContextPath() %>/<%= objet.getImagePath() %>" alt="Objet">
                <h1 class="h4"><%= objet.getTitre() %></h1>
                <p class="text-muted"><%= objet.getDescription() %></p>
                <p class="mb-0"><strong>Lieu:</strong> <%= objet.getLocalisation() %></p>
            </div>
        </div>
        <div class="col-lg-7">
            <div class="form-card p-4">
                <h2 class="h3 mb-3">Pourquoi est-ce votre objet?</h2>
                <form method="post" action="<%= request.getContextPath() %>/reclamation?action=creer">
                    <input type="hidden" name="objetId" value="<%= objet.getId() %>">
                    <div class="mb-3">
                        <label class="form-label">Message</label>
                        <textarea class="form-control" name="message" rows="7" required minlength="10"></textarea>
                    </div>
                    <input
                        type="hidden"
                        name="_csrf"
                        value="<%= session.getAttribute("csrfToken") %>">
                    <button class="btn btn-primary" type="submit">Envoyer la demande</button>
                </form>
            </div>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>