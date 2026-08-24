package service;

import java.util.List;
import java.util.Map;

import modele.Objet;
import modele.Reclamation;
import modele.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ObjetRepository;
import repository.ReclamationRepository;
import repository.UtilisateurRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ObjetRepository objetRepository;

    @Mock
    private ReclamationRepository reclamationRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(
                utilisateurRepository,
                objetRepository,
                reclamationRepository
        );
    }

    @Test
    void shouldBuildGlobalStatisticsFromRepositories() {
        when(utilisateurRepository.countAll()).thenReturn(12);
        when(utilisateurRepository.countAdmins()).thenReturn(2);
        when(objetRepository.countAll()).thenReturn(30);
        when(reclamationRepository.countEnAttente())
                .thenReturn(4);

        Map<String, Integer> resultat =
                adminService.getStatistiquesGlobales();

        assertEquals(
                Map.of(
                        "utilisateurs", 12,
                        "admins", 2,
                        "objets", 30,
                        "reclamationsEnAttente", 4
                ),
                resultat
        );

        verify(utilisateurRepository).countAll();
        verify(utilisateurRepository).countAdmins();
        verify(objetRepository).countAll();
        verify(reclamationRepository).countEnAttente();
    }

    @Test
    void shouldListUsersFromRepository() {
        List<Utilisateur> utilisateurs =
                List.of(new Utilisateur());

        when(utilisateurRepository.selectAll())
                .thenReturn(utilisateurs);

        List<Utilisateur> resultat =
                adminService.listerUtilisateurs();

        assertSame(utilisateurs, resultat);

        verify(utilisateurRepository).selectAll();
    }

    @Test
    void shouldListItemsFromRepository() {
        List<Objet> objets =
                List.of(new Objet());

        when(objetRepository.selectAll())
                .thenReturn(objets);

        List<Objet> resultat =
                adminService.listerObjets();

        assertSame(objets, resultat);

        verify(objetRepository).selectAll();
    }

    @Test
    void shouldListClaimsFromRepository() {
        List<Reclamation> reclamations =
                List.of(new Reclamation());

        when(reclamationRepository.selectAll())
                .thenReturn(reclamations);

        List<Reclamation> resultat =
                adminService.listerReclamations();

        assertSame(reclamations, resultat);

        verify(reclamationRepository).selectAll();
    }

    @Test
    void shouldPromoteUserToAdmin() {
        adminService.promouvoirEnAdmin(42);

        verify(utilisateurRepository)
                .promouvoirEnAdmin(42);
    }

    @Test
    void shouldDemoteAdminToUser() {
        adminService.retrograderEnUser(42);

        verify(utilisateurRepository)
                .retrograderEnUser(42);
    }

    @Test
    void shouldDeleteAnotherUser() {
        adminService.supprimerUtilisateur(42, 7);

        verify(utilisateurRepository).delete(42);
    }

    @Test
    void shouldRejectDeletingCurrentAdmin() {
        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.supprimerUtilisateur(
                        42,
                        42
                )
        );

        verify(utilisateurRepository, never())
                .delete(anyInt());

        verifyNoInteractions(
                objetRepository,
                reclamationRepository
        );
    }

    @Test
    void shouldDeleteItem() {
        adminService.supprimerAnnonce(42);

        verify(objetRepository).delete(42);
    }
}