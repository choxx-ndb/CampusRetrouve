package service;

import java.util.List;

import modele.Objet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ObjetRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetServiceTest {

    @Mock
    private ObjetRepository objetRepository;

    private ObjetService objetService;

    @BeforeEach
    void setUp() {
        objetService = new ObjetService(objetRepository);
    }

    @Test
    void shouldNormalizeAndPublishValidItem() {
        Objet objet = new Objet(
                " Téléphone ",
                " Écran noir ",
                "perdue",
                " Bibliothèque ",
                "telephone.jpg",
                1
        );

        objet.setStatus("reclame");

        objetService.publierAnnonce(objet);

        assertAll(
                () -> assertEquals(
                        "Téléphone",
                        objet.getTitre()
                ),
                () -> assertEquals(
                        "Écran noir",
                        objet.getDescription()
                ),
                () -> assertEquals(
                        "Bibliothèque",
                        objet.getLocalisation()
                ),
                () -> assertEquals(
                        "disponible",
                        objet.getStatus()
                )
        );

        verify(objetRepository).add(objet);
    }

    @Test
    void shouldRejectInvalidItemTypeWithoutCallingRepository() {
        Objet objet = new Objet(
                "Téléphone",
                "Écran noir",
                "inconnu",
                "Bibliothèque",
                "telephone.jpg",
                1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> objetService.publierAnnonce(objet)
        );

        verifyNoInteractions(objetRepository);
    }

    @Test
    void shouldFindItemsByValidType() {
        List<Objet> objets = List.of(new Objet());

        when(objetRepository.findByType("perdue"))
                .thenReturn(objets);

        List<Objet> resultat =
                objetService.recupererParType("perdue");

        assertSame(objets, resultat);

        verify(objetRepository).findByType("perdue");
        verify(objetRepository, never()).selectAll();
    }

    @Test
    void shouldReturnAllItemsWhenTypeFilterIsInvalid() {
        List<Objet> objets = List.of(new Objet());

        when(objetRepository.selectAll())
                .thenReturn(objets);

        List<Objet> resultat =
                objetService.recupererParType("inconnu");

        assertSame(objets, resultat);

        verify(objetRepository).selectAll();
        verify(objetRepository, never())
                .findByType(anyString());
    }

    @Test
    void shouldUpdateValidStatus() {
        objetService.changerStatut(42, "reclame");

        verify(objetRepository)
                .updateStatus(42, "reclame");
    }

    @Test
    void shouldRejectInvalidStatusWithoutCallingRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> objetService.changerStatut(42, "inconnu")
        );

        verifyNoInteractions(objetRepository);
    }
}
