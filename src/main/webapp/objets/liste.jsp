<%@ page import="java.util.List" %>
<%@ page import="modele.Objet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%
    // Forcer le décodage correct des accents (ex: Annonce publiée)
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    List<Objet> objets = (List<Objet>) request.getAttribute("objets");
    String filtre = (String) request.getAttribute("filtre");
    if (filtre == null) filtre = "tous";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Annonces - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <h1><i class="fa-solid fa-list me-2"></i>Annonces</h1>
            <p class="lead">Objets perdus et trouvés sur le campus.</p>
        </div>
        <a class="btn btn-warning" href="<%= request.getContextPath() %>/objet?action=formulaire">
            <i class="fa-solid fa-plus me-1"></i>Publier
        </a>
    </div>
</header>
<main class="container pb-5">
    <div class="btn-group mb-4">
        <a class="btn <%= "tous".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/objet?action=liste">Tous</a>
        <a class="btn <%= "perdue".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/objet?action=perdue">Perdus</a>
        <a class="btn <%= "trouve".equals(filtre) ? "btn-primary" : "btn-outline-primary" %>" href="<%= request.getContextPath() %>/objet?action=trouve">Trouvés</a>
    </div>
    <div class="row g-4">
        <% if (objets == null || objets.isEmpty()) { %>
            <div class="col-12"><div class="alert alert-info">Aucune annonce trouvée.</div></div>
        <% } else { for (Objet objet : objets) { %>
            <div class="col-md-6 col-xl-4">
                <div class="card object-card accent-card h-100">
                    <img src="<%= request.getContextPath() %>/<%= Encode.forHtmlAttribute(objet.getImagePath()) %>"
                         class="card-img-top object-image" 
                         alt="Image objet"
                         onerror="this.src='https://placehold.co/300x200?text=Image+.jpeg+Introuvable'">
                    <div class="card-body d-flex flex-column">
                        <div class="d-flex justify-content-between gap-2 mb-2">
                            <span class="badge <%= "perdue".equals(objet.getType()) ? "text-bg-danger" : "text-bg-success" %>"><%= Encode.forHtml(objet.getType()) %></span>
                            <span class="badge badge-status"><%= Encode.forHtml(objet.getStatus()) %></span>
                        </div>
                        <h2 class="h5"><%= Encode.forHtml(objet.getTitre()) %></h2>
                        <p class="text-muted small flex-grow-1"><%= Encode.forHtml(objet.getDescription()) %></p>
                        <p class="small mb-3"><strong>Lieu:</strong> <%= Encode.forHtml(objet.getLocalisation()) %></p>
                        <a class="btn btn-outline-primary mt-auto" href="<%= request.getContextPath() %>/objet?action=detail&id=<%= objet.getId() %>">Voir détails</a>
                    </div>
                </div>
            </div>
        <% }} %>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>