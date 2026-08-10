<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Vérification de la session utilisateur
    boolean connecte = (session != null && session.getAttribute("userId") != null);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<main class="hero">
    <div class="container py-5">
        <div class="row align-items-center g-4">
            <div class="col-lg-7">
                <span class="badge text-bg-warning mb-3">Faculté des sciences Meknès</span>
                <h1 class="display-4 fw-bold">CampusRetrouve</h1>
                <p class="lead text-muted">Publiez, retrouvez et réclamez les objets perdus ou trouvés sur le campus avec une messagerie sécurisée.</p>
                <div class="d-flex flex-wrap gap-2 mt-4">
                    <a class="btn btn-primary btn-lg" href="<%= request.getContextPath() %>/objet?action=liste">Voir les annonces</a>
                    <a class="btn btn-outline-primary btn-lg" href="<%= request.getContextPath() %>/objet?action=formulaire">Publier une annonce</a>
                </div>
            </div>
            <div class="col-lg-5">
                <div class="panel p-4">
                    <h2 class="h4 mb-3">Actions rapides</h2>
                    <div class="d-grid gap-2">
                        <a class="btn btn-light text-start" href="<%= request.getContextPath() %>/objet?action=perdue">Objets perdus</a>
                        <a class="btn btn-light text-start" href="<%= request.getContextPath() %>/objet?action=trouve">Objets trouvés</a>
                        
                        <%-- Le bouton s'affiche uniquement si l'utilisateur est connecté --%>
                        <% if (connecte) { %>
                            <a class="btn btn-light text-start" href="<%= request.getContextPath() %>/reclamation?action=mes-reclamations">Mes réclamations</a>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
