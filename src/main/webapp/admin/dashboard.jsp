<%@ page import="java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("stats"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard Admin - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-chart-line me-2"></i>Dashboard Admin</h1>
        <p class="lead">Vue globale des utilisateurs, annonces et réclamations.</p>
    </div>
</header>
<main class="container pb-5">
    <div class="row g-3 mb-4">
        <div class="col-md-3"><div class="stat-card accent-card"><span><i class="fa-solid fa-users me-1"></i>Utilisateurs</span><strong><%= stats.get("utilisateurs") %></strong></div></div>
        <div class="col-md-3"><div class="stat-card accent-card"><span><i class="fa-solid fa-user-shield me-1"></i>Admins</span><strong><%= stats.get("admins") %></strong></div></div>
        <div class="col-md-3"><div class="stat-card accent-card"><span><i class="fa-solid fa-boxes-stacked me-1"></i>Objets</span><strong><%= stats.get("objets") %></strong></div></div>
        <div class="col-md-3"><div class="stat-card accent-card"><span><i class="fa-solid fa-hourglass-half me-1"></i>En attente</span><strong><%= stats.get("reclamationsEnAttente") %></strong></div></div>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a class="btn btn-primary" href="<%= request.getContextPath() %>/admin?action=utilisateurs">Gérer utilisateurs</a>
        <a class="btn btn-outline-primary" href="<%= request.getContextPath() %>/admin?action=reclamations">Supervision</a>
        <a class="btn btn-outline-primary" href="<%= request.getContextPath() %>/admin?action=objets">Modérer</a>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

