<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Publier - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-plus me-2"></i>Publier une annonce</h1>
        <p class="lead">Ajoutez un objet perdu ou trouvé avec les détails utiles.</p>
    </div>
</header>
<main class="container pb-5 narrow-page">
    <div class="form-card p-4">
        <h2 class="h4 mb-4">Informations de l'objet</h2>
        <% if (request.getAttribute("erreur") != null) { %><div class="alert alert-danger"><%= request.getAttribute("erreur") %></div><% } %>
        <form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/objet?action=ajouter">
            <div class="mb-3">
                <label class="form-label">Titre</label>
                <input class="form-control" name="titre" required minlength="3">
            </div>
            <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea class="form-control" name="description" rows="4"></textarea>
            </div>
            <div class="mb-3">
                <label class="form-label d-block">Type</label>
                <div class="btn-group" role="group">
                    <input class="btn-check" type="radio" name="type" id="typePerdue" value="perdue" required>
                    <label class="btn btn-outline-primary" for="typePerdue">Perdu</label>
                    <input class="btn-check" type="radio" name="type" id="typeTrouve" value="trouve" required>
                    <label class="btn btn-outline-primary" for="typeTrouve">Trouvé</label>
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Localisation</label>
                <input class="form-control" name="localisation">
            </div>
            <div class="mb-4">
                <label class="form-label">Image</label>
                <input class="form-control" type="file" name="image" accept="image/*">
            </div>
            <input
                type="hidden"
                name="_csrf"
                value="<%= session.getAttribute("csrfToken") %>">
            <button class="btn btn-primary w-100" type="submit">Publier</button>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

