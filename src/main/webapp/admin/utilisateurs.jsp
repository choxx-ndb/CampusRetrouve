<%@ page import="java.util.List" %>
<%@ page import="modele.Utilisateur" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs"); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Utilisateurs - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/navbar.jsp"/>
<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-users me-2"></i>Gestion des utilisateurs</h1>
        <p class="lead">Promouvoir, rétrograder ou supprimer des comptes.</p>
    </div>
</header>
<main class="container pb-5">
    <div class="panel accent-card p-3 table-responsive">
        <table class="table align-middle">
            <thead><tr><th>ID</th><th>Nom</th><th>Email</th><th>Rôle</th><th>Actions</th></tr></thead>
            <tbody>
            <% for (Utilisateur u : utilisateurs) { %>
                <tr>
                    <td><%= u.getId() %></td>
                    <td><%= u.getNom() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><span class="badge badge-status"><%= u.getRole() %></span></td>
                    <td class="d-flex flex-wrap gap-2">

                        <% if ("user".equals(u.getRole())) { %>

                            <form method="post"
                                  action="<%= request.getContextPath() %>/admin">

                                <input type="hidden" name="action" value="promouvoir">
                                <input type="hidden" name="id" value="<%= u.getId() %>">
                                <input type="hidden"
                                       name="_csrf"
                                       value="<%= session.getAttribute("csrfToken") %>">

                                <button class="btn btn-sm btn-primary"
                                        type="submit">
                                    Promouvoir
                                </button>
                            </form>

                        <% } else { %>

                            <form method="post"
                                  action="<%= request.getContextPath() %>/admin">

                                <input type="hidden" name="action" value="retrograder">
                                <input type="hidden" name="id" value="<%= u.getId() %>">
                                <input type="hidden"
                                       name="_csrf"
                                       value="<%= session.getAttribute("csrfToken") %>">

                                <button class="btn btn-sm btn-warning"
                                        type="submit">
                                    Rétrograder
                                </button>
                            </form>

                        <% } %>

                        <form method="post"
                              action="<%= request.getContextPath() %>/admin"
                              onsubmit="return confirm('Supprimer cet utilisateur?')">

                            <input type="hidden" name="action" value="supprimer-user">
                            <input type="hidden" name="id" value="<%= u.getId() %>">
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
            <% } %>
            </tbody>
        </table>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
