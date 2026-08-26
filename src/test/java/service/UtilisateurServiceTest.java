package service;

import modele.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UtilisateurRepository;
import security.PasswordHasher;

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

    @Mock
    private PasswordHasher passwordHasher;

    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        utilisateurService =
                new UtilisateurService(
                        utilisateurRepository,
                        passwordHasher
                );
    }

    @Test
    void shouldReturnNullWhenCredentialsAreBlank() {
        Utilisateur resultat =
                utilisateurService.authentifier(
                        " ",
                        "CampusUser2026!"
                );

        assertNull(resultat);
        verifyNoInteractions(
                utilisateurRepository,
                passwordHasher
        );
    }

    @Test
    void shouldReturnNullWhenEmailDoesNotExist() {
        when(utilisateurRepository.findByEmail(
                "student@umi.ac.ma"
        )).thenReturn(null);

        Utilisateur resultat =
                utilisateurService.authentifier(
                        "student@umi.ac.ma",
                        "CampusUser2026!"
                );

        assertNull(resultat);

        verify(utilisateurRepository)
                .findByEmail("student@umi.ac.ma");

        verifyNoInteractions(passwordHasher);
    }

    @Test
    void shouldReturnNullWhenPasswordIsIncorrect() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMotdepass("stored-hash");

        when(utilisateurRepository.findByEmail(
                "student@umi.ac.ma"
        )).thenReturn(utilisateur);

        when(passwordHasher.matches(
                "WrongPassword2026!",
                "stored-hash"
        )).thenReturn(false);

        Utilisateur resultat =
                utilisateurService.authentifier(
                        "student@umi.ac.ma",
                        "WrongPassword2026!"
                );

        assertNull(resultat);

        verify(passwordHasher).matches(
                "WrongPassword2026!",
                "stored-hash"
        );
    }

    @Test
    void shouldNormalizeEmailAndAuthenticateValidPassword() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMotdepass("stored-hash");

        when(utilisateurRepository.findByEmail(
                "student@umi.ac.ma"
        )).thenReturn(utilisateur);

        when(passwordHasher.matches(
                "CampusUser2026!",
                "stored-hash"
        )).thenReturn(true);

        Utilisateur resultat =
                utilisateurService.authentifier(
                        " Student@UMI.AC.MA ",
                        "CampusUser2026!"
                );

        assertSame(utilisateur, resultat);

        verify(utilisateurRepository)
                .findByEmail("student@umi.ac.ma");

        verify(passwordHasher).matches(
                "CampusUser2026!",
                "stored-hash"
        );
    }

    @Test
    void shouldHashPasswordAndSaveValidRegistration() {
        Utilisateur utilisateur = new Utilisateur(
                " Noureddine ",
                "NOUREDDINE@UMI.AC.MA",
                "CampusUser2026!"
        );

        when(passwordHasher.hash(
                "CampusUser2026!"
        )).thenReturn("bcrypt-hash");

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
                ),
                () -> assertEquals(
                        "bcrypt-hash",
                        utilisateur.getMotdepass()
                )
        );

        verify(utilisateurRepository)
                .emailExiste("noureddine@umi.ac.ma");

        verify(utilisateurRepository)
                .nomExiste("Noureddine");

        verify(passwordHasher)
                .hash("CampusUser2026!");

        verify(utilisateurRepository)
                .add(utilisateur);
    }

    @Test
    void shouldRejectPasswordShorterThanFifteenCharacters() {
        Utilisateur utilisateur = new Utilisateur(
                "Noureddine",
                "noureddine@umi.ac.ma",
                "short"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(utilisateur)
        );

        verifyNoInteractions(
                utilisateurRepository,
                passwordHasher
        );
    }

    @Test
    void shouldRejectPasswordLongerThanBcryptLimit() {
        Utilisateur utilisateur = new Utilisateur(
                "Noureddine",
                "noureddine@umi.ac.ma",
                "a".repeat(73)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.inscrire(utilisateur)
        );

        verifyNoInteractions(
                utilisateurRepository,
                passwordHasher
        );
    }
}
