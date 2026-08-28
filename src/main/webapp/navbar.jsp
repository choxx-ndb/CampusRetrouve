<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%
    String ctx = request.getContextPath();
    Object userId = session.getAttribute("userId");
    String userName = (String) session.getAttribute("userName");
    String userRole = (String) session.getAttribute("userRole");
    boolean isAdmin = "admin".equals(userRole);
%>
<nav class="navbar navbar-expand-lg navbar-dark umi-navbar sticky-top">
    <div class="container-fluid px-lg-5">
        <a class="navbar-brand fw-bold" href="<%= ctx %>/index.jsp">
            <i class="fa-solid fa-magnifying-glass me-2"></i>CampusRetrouve
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar" aria-controls="mainNavbar" aria-expanded="false" aria-label="Menu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/objet?action=liste">
                        <i class="fa-solid fa-list me-1"></i>Tous les objets
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/objet?action=perdue">
                        <i class="fa-solid fa-circle-exclamation me-1"></i>Perdus
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= ctx %>/objet?action=trouve">
                        <i class="fa-solid fa-circle-check me-1"></i>Trouvés
                    </a>
                </li>
            </ul>

            <ul class="navbar-nav ms-auto align-items-lg-center">
                <% if (userId != null && userName != null) { %>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fa-solid fa-circle-user me-1"></i><%= Encode.forHtml(userName) %>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li>
                                <a class="dropdown-item" href="<%= ctx %>/objet?action=formulaire">
                                    <i class="fa-solid fa-plus me-2"></i>Ajouter un objet
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="<%= ctx %>/reclamation?action=mes-reclamations">
                                    <i class="fa-solid fa-clipboard-list me-2"></i>Mes réclamations
                                </a>
                            </li>

                            <% if (isAdmin) { %>
                                <li><hr class="dropdown-divider"></li>
                                <li><h6 class="dropdown-header text-orange">Administration</h6></li>
                                <li>
                                    <a class="dropdown-item fw-semibold text-orange" href="<%= ctx %>/admin?action=dashboard">
                                        <i class="fa-solid fa-chart-line me-2"></i>Dashboard Admin
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/admin?action=reclamations">
                                        <i class="fa-solid fa-list-check me-2"></i>Gérer réclamations
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/admin?action=utilisateurs">
                                        <i class="fa-solid fa-users me-2"></i>Gérer utilisateurs
                                    </a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="<%= ctx %>/admin?action=objets">
                                        <i class="fa-solid fa-boxes-stacked me-2"></i>Modérer objets
                                    </a>
                                </li>
                            <% } %>

                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <form
                                    method="post"
                                    action="<%= ctx %>/utilisateur">

                                    <input
                                        type="hidden"
                                        name="action"
                                        value="logout">

                                    <input
                                        type="hidden"
                                        name="_csrf"
                                        value="<%= session.getAttribute("csrfToken") %>">

                                    <button
                                        type="submit"
                                        class="dropdown-item text-danger">

                                        <i class="fa-solid fa-right-from-bracket me-2"></i>
                                        Déconnexion
                                    </button>
                                </form>
                            </li>
                        </ul>
                    </li>
                <% } else { %>
                    <li class="nav-item me-lg-2">
                        <a class="btn btn-outline-light btn-sm" href="<%= ctx %>/utilisateur?action=connexion">
                            <i class="fa-solid fa-right-to-bracket me-1"></i>Connexion
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-warning btn-sm" href="<%= ctx %>/utilisateur?action=inscription">
                            <i class="fa-solid fa-user-plus me-1"></i>Inscription
                        </a>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>
