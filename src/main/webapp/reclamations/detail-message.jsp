<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modele.MessageReclamation" %>
<%@ page import="modele.Objet" %>
<%@ page import="modele.Reclamation" %>
<%
    // Force l'encodage UTF-8 au niveau de la requête et de la réponse Tomcat
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    Reclamation reclamation = (Reclamation) request.getAttribute("reclamation");
    Objet objet = (Objet) request.getAttribute("objet");
    List<MessageReclamation> messages = (List<MessageReclamation>) request.getAttribute("messages");
    Integer userId = (Integer) session.getAttribute("userId");
    boolean proprietaire = userId != null && reclamation != null && userId == reclamation.getProprietaireId();
    boolean admin = "admin".equals(session.getAttribute("userRole"));
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Discussion - CampusRetrouve</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/navbar.jsp"/>

<header class="page-header">
    <div class="container">
        <h1><i class="fa-solid fa-comments me-2"></i>Discussion</h1>
        <p class="lead">Échange privé autour de la réclamation.</p>
    </div>
</header>

<main class="container pb-5">
    <div class="row g-4">
        <div class="col-lg-4">
            <div class="panel accent-card p-4">
                <img class="detail-image mb-3" src="<%= request.getContextPath() %>/<%= objet.getImagePath() %>" alt="Objet" onerror="this.src='https://placehold.co/300x200?text=Pas+d+image'">
                <h1 class="h4"><%= objet.getTitre() %></h1>
                <p class="text-muted"><%= objet.getDescription() %></p>
                <p><strong>Lieu:</strong> <%= objet.getLocalisation() %></p>
                <p><strong>Propriétaire:</strong> <%= reclamation.getProprietaireNom() %></p>
                <p><strong>Statut:</strong> <span class="badge bg-warning text-dark"><%= reclamation.getStatus() %></span></p>

                <% if (proprietaire && "en_attente".equals(reclamation.getStatus())) { %>
                    <div class="d-grid gap-2">

                        <form method="post" action="<%= request.getContextPath() %>/reclamation">
                            <input type="hidden" name="action" value="traiter">
                            <input type="hidden" name="id" value="<%= reclamation.getId() %>">
                            <input type="hidden" name="decision" value="approuve">
                            <input type="hidden" name="_csrf" value="<%= session.getAttribute("csrfToken") %>">
                            <button class="btn btn-success" type="submit">Approuver</button>
                        </form>

                        <form method="post" action="<%= request.getContextPath() %>/reclamation">
                            <input type="hidden" name="action" value="traiter">
                            <input type="hidden" name="id" value="<%= reclamation.getId() %>">
                            <input type="hidden" name="decision" value="rejete">
                            <input type="hidden" name="_csrf" value="<%= session.getAttribute("csrfToken") %>">
                            <button class="btn btn-danger" type="submit">Refuser</button>
                        </form>

                    </div>
                <% } %>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="panel p-4">
                <h2 class="h3 mb-3">Discussion</h2>

                <div class="chat-box mb-3" style="max-height: 400px; overflow-y: auto;">
                    <% if (messages == null || messages.isEmpty()) { %>
                        <p class="text-muted">Aucun message.</p>
                    <% } else { for (MessageReclamation m : messages) { boolean mine = userId != null && userId == m.getExpediteurId(); %>
                        <div class="message-row <%= mine ? "mine" : "" %>">
                            <div class="message-bubble">
                                <div class="small fw-semibold"><%= m.getExpediteurNom() %></div>
                                <div><%= m.getContenu() %></div>
                                <div class="small opacity-75 mt-1"><%= m.getDateEnvoi() %></div>
                            </div>
                        </div>
                    <% }} %>
                </div>

                <% if (!admin) { %>
                    <form method="post" action="<%= request.getContextPath() %>/reclamation?action=envoyer-message">
                        <input type="hidden" name="reclamationId" value="<%= reclamation.getId() %>">

                        <div class="mb-3">
                            <textarea class="form-control" name="contenu" rows="3" required placeholder="Écrire un message"></textarea>
                        </div>

                        <input
                            type="hidden"
                            name="_csrf"
                            value="<%= session.getAttribute("csrfToken") %>">

                        <button class="btn btn-primary" type="submit">Envoyer</button>
                    </form>
                <% } %>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
