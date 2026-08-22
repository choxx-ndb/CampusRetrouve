package service;

import modele.MessageReclamation;
import modele.Objet;
import modele.Reclamation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ObjetRepository;
import repository.ReclamationRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReclamationServiceTest {

    @Mock
    private ReclamationRepository reclamationRepository;

    @Mock
    private ObjetRepository objetRepository;

    private ReclamationService reclamationService;

    @BeforeEach
    void setUp() {
        reclamationService = new ReclamationService(
                reclamationRepository,
                objetRepository
        );
    }

    @Test
    void shouldCreateClaimAndReserveAvailableItem() {
        Reclamation reclamation = new Reclamation(
                42,
                7,
                "  J'ai retrouvé cet objet.  "
        );

        Objet objet = creerObjet(42, 99, "disponible");

        when(objetRepository.getById(42))
                .thenReturn(objet);

        doAnswer(invocation -> {
            Reclamation reclamationEnregistree =
                    invocation.getArgument(0);
            reclamationEnregistree.setId(100);
            return null;
        }).when(reclamationRepository).add(reclamation);

        reclamationService.creerReclamation(reclamation);

        ArgumentCaptor<MessageReclamation> messageCaptor =
                ArgumentCaptor.forClass(MessageReclamation.class);

        InOrder ordre = inOrder(
                objetRepository,
                reclamationRepository
        );

        ordre.verify(objetRepository).getById(42);
        ordre.verify(reclamationRepository).add(reclamation);
        ordre.verify(reclamationRepository)
                .ajouterMessage(messageCaptor.capture());
        ordre.verify(objetRepository)
                .updateStatus(42, "reclame");

        MessageReclamation premierMessage =
                messageCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        "en_attente",
                        reclamation.getStatus()
                ),
                () -> assertEquals(
                        "J'ai retrouvé cet objet.",
                        reclamation.getMessage()
                ),
                () -> assertEquals(
                        100,
                        premierMessage.getReclamationId()
                ),
                () -> assertEquals(
                        7,
                        premierMessage.getExpediteurId()
                ),
                () -> assertEquals(
                        "J'ai retrouvé cet objet.",
                        premierMessage.getContenu()
                )
        );
    }

    @Test
    void shouldRejectClaimOnOwnItem() {
        Reclamation reclamation = new Reclamation(
                42,
                7,
                "Mon objet"
        );

        Objet objet = creerObjet(42, 7, "disponible");

        when(objetRepository.getById(42))
                .thenReturn(objet);

        assertThrows(
                IllegalArgumentException.class,
                () -> reclamationService.creerReclamation(
                        reclamation
                )
        );

        verify(objetRepository).getById(42);
        verify(objetRepository, never())
                .updateStatus(anyInt(), anyString());
        verifyNoInteractions(reclamationRepository);
    }

    @Test
    void shouldRejectClaimWhenItemIsUnavailable() {
        Reclamation reclamation = new Reclamation(
                42,
                7,
                "Je souhaite récupérer cet objet"
        );

        Objet objet = creerObjet(42, 99, "reclame");

        when(objetRepository.getById(42))
                .thenReturn(objet);

        assertThrows(
                IllegalArgumentException.class,
                () -> reclamationService.creerReclamation(
                        reclamation
                )
        );

        verify(objetRepository).getById(42);
        verify(objetRepository, never())
                .updateStatus(anyInt(), anyString());
        verifyNoInteractions(reclamationRepository);
    }

    @Test
    void shouldSaveTrimmedMessageForParticipant() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        when(reclamationRepository.getById(15))
                .thenReturn(reclamation);

        reclamationService.enregistrerMessage(
                15,
                7,
                "  Bonjour, pouvez-vous me contacter ?  "
        );

        ArgumentCaptor<MessageReclamation> messageCaptor =
                ArgumentCaptor.forClass(MessageReclamation.class);

        verify(reclamationRepository).getById(15);
        verify(reclamationRepository)
                .ajouterMessage(messageCaptor.capture());
        verifyNoInteractions(objetRepository);

        MessageReclamation message = messageCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        15,
                        message.getReclamationId()
                ),
                () -> assertEquals(
                        7,
                        message.getExpediteurId()
                ),
                () -> assertEquals(
                        "Bonjour, pouvez-vous me contacter ?",
                        message.getContenu()
                )
        );
    }

    @Test
    void shouldRejectMessageFromNonParticipant() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        when(reclamationRepository.getById(15))
                .thenReturn(reclamation);

        assertThrows(
                IllegalArgumentException.class,
                () -> reclamationService.enregistrerMessage(
                        15,
                        55,
                        "Message interdit"
                )
        );

        verify(reclamationRepository).getById(15);
        verify(reclamationRepository, never())
                .ajouterMessage(any(MessageReclamation.class));
        verifyNoInteractions(objetRepository);
    }

    @Test
    void shouldApproveClaimAndMarkItemAsReturned() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        when(reclamationRepository.getById(15))
                .thenReturn(reclamation);

        reclamationService.traiterReclamation(
                15,
                99,
                "approuve"
        );

        InOrder ordre = inOrder(
                reclamationRepository,
                objetRepository
        );

        ordre.verify(reclamationRepository).getById(15);
        ordre.verify(reclamationRepository).update(reclamation);
        ordre.verify(objetRepository)
                .updateStatus(42, "restitue");

        assertEquals("approuve", reclamation.getStatus());
    }

    @Test
    void shouldRejectClaimAndReleaseItem() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        when(reclamationRepository.getById(15))
                .thenReturn(reclamation);

        reclamationService.traiterReclamation(
                15,
                99,
                "rejete"
        );

        InOrder ordre = inOrder(
                reclamationRepository,
                objetRepository
        );

        ordre.verify(reclamationRepository).getById(15);
        ordre.verify(reclamationRepository).update(reclamation);
        ordre.verify(objetRepository)
                .updateStatus(42, "disponible");

        assertEquals("rejete", reclamation.getStatus());
    }

    @Test
    void shouldRejectInvalidDecisionWithoutUpdatingRepositories() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        when(reclamationRepository.getById(15))
                .thenReturn(reclamation);

        assertThrows(
                IllegalArgumentException.class,
                () -> reclamationService.traiterReclamation(
                        15,
                        99,
                        "annule"
                )
        );

        assertEquals("en_attente", reclamation.getStatus());

        verify(reclamationRepository).getById(15);
        verify(reclamationRepository, never())
                .update(reclamation);
        verifyNoInteractions(objetRepository);
    }

    @Test
    void shouldAuthorizeOnlyAdminRequesterOrOwnerToViewDiscussion() {
        Reclamation reclamation = creerReclamation(
                15,
                42,
                7,
                99,
                "en_attente"
        );

        assertAll(
                () -> assertTrue(
                        reclamationService.peutVoirDiscussion(
                                reclamation,
                                55,
                                "admin"
                        )
                ),
                () -> assertTrue(
                        reclamationService.peutVoirDiscussion(
                                reclamation,
                                7,
                                "utilisateur"
                        )
                ),
                () -> assertTrue(
                        reclamationService.peutVoirDiscussion(
                                reclamation,
                                99,
                                "utilisateur"
                        )
                ),
                () -> assertFalse(
                        reclamationService.peutVoirDiscussion(
                                reclamation,
                                55,
                                "utilisateur"
                        )
                ),
                () -> assertFalse(
                        reclamationService.peutVoirDiscussion(
                                null,
                                7,
                                "utilisateur"
                        )
                )
        );

        verifyNoInteractions(
                reclamationRepository,
                objetRepository
        );
    }

    private Objet creerObjet(
            int id,
            int proprietaireId,
            String status) {
        Objet objet = new Objet();
        objet.setId(id);
        objet.setProprietaireId(proprietaireId);
        objet.setStatus(status);
        return objet;
    }

    private Reclamation creerReclamation(
            int id,
            int objetId,
            int utilisateurId,
            int proprietaireId,
            String status) {
        Reclamation reclamation = new Reclamation();
        reclamation.setId(id);
        reclamation.setObjetId(objetId);
        reclamation.setUtilisateurId(utilisateurId);
        reclamation.setProprietaireId(proprietaireId);
        reclamation.setMessage("Message");
        reclamation.setStatus(status);
        return reclamation;
    }
}
