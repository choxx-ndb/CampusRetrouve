<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<main class="container py-5 narrow-page">
    <div class="form-card p-4">
        <h1 class="h3 mb-4"><i class="fa-solid fa-right-to-bracket me-2 text-orange"></i>Connexion</h1>
        <% if (request.getAttribute("erreur") != null) { %><div class="alert alert-danger"><%= request.getAttribute("erreur") %></div><% } %>
        <% if (request.getAttribute("success") != null) { %><div class="alert alert-success"><%= request.getAttribute("success") %></div><% } %>
        <form method="post" action="<%= request.getContextPath() %>/utilisateur?action=authentifier">
            <div class="mb-3">
                <label class="form-label">Email</label>
                <input class="form-control" type="email" name="email" required>
            </div>
            <input
                type="hidden"
                name="_csrf"
                value="<%= session.getAttribute("csrfToken") %>">
            <div class="mb-3">
                <label class="form-label">Mot de passe</label>
                <input class="form-control" type="password" name="motdepass" required>
            </div>
            <button class="btn btn-primary w-100" type="submit">Se connecter</button>
        </form>
        <p class="mt-3 mb-0 text-center">Pas encore inscrit? <a href="<%= request.getContextPath() %>/utilisateur?action=inscription">Créer un compte</a></p>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

