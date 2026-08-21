package service;

import modele.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UtilisateurRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        utilisateurService =
                new UtilisateurService(utilisateurRepository);
    }

    @Test
    void shouldReturnNullWhenCredentialsAreBlank() {
        Utilisateur resultat =
                utilisateurService.authentifier(" ", "secret123");

        assertNull(resultat);
        verifyNoInteractions(utilisateurRepository);
    }

    @Test
    void shouldNormalizeEmailBeforeAuthentication() {
        Utilisateur utilisateur = new Utilisateur();

        when(utilisateurRepository.authentifier(
                "student@umi.ac.ma",
                "secret123"
        )).thenReturn(utilisateur);

        Utilisateur resultat = utilisateurService.authentifier(
                " Student@UMI.AC.MA ",
                "secret123"
        );

        assertSame(utilisateur, resultat);

        verify(utilisateurRepository).authentifier(
                "student@umi.ac.ma",
                "secret123"
        );
    }

    @Test
    void shouldNormalizeAndSaveValidRegistration() {
        Utilisateur utilisateur = new Utilisateur(
                " Noureddine ",
                "NOUREDDINE@UMI.AC.MA",
                "secret123"
        );

        utilisateurService.inscrire(utilisateur);

        assertAll(
                () -> assertEquals(
                        "Noureddine",
                        utilisateur.getNom()
                ),
                () -> assertEquals(
                        "noureddine@umi.ac.ma",
                        utilisateur.getEmail()
                ),
                () -> assertEquals(
                        "user",
                        utilisateur.getRole()
                )
        );

        verify(utilisateurRepository)
                .emailExiste("noureddine@umi.ac.ma");
        verify(utilisateurRepository)
                .nomExiste("Noureddine");
        verify(utilisateurRepository).add(utilisateur);
    }

    @Test
    void shouldRejectPasswordShorterThanSixCharacters() {
        Utilisateur utilisateur = new Utilisateur(
                "Noureddine",
                "noureddine@umi.ac.ma",
                "12345"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(utilisateur)
        );

        verifyNoInteractions(utilisateurRepository);
    }
}
