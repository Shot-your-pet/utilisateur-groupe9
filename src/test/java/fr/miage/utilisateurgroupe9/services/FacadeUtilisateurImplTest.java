package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.repository.UtilisateurRepository;
import fr.miage.utilisateurgroupe9.model.exceptions.UtilisateurInexistantException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacadeUtilisateurImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RabbitEventsSender keycloakEventsSender;

    @InjectMocks
    private FacadeUtilisateurImpl facadeUtilisateurImpl;

    private UUID userId;
    private Utilisateur dummyUtilisateur;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        dummyUtilisateur = new Utilisateur(userId, "pseudo", "nom", "prenom", "email@test.com", LocalDate.now());
        dummyUtilisateur.setDateCreation(LocalDateTime.now());
        dummyUtilisateur.setIdAvatar(100L);
    }

    @Test
    public void testCreerUtilisateurOK() {
        CreerUtilisateurDTO creerDTO = new CreerUtilisateurDTO(UUID.randomUUID(), "pseudo", "lo", "j","d", LocalDate.now());

        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> (Utilisateur) invocation.getArgument(0));

        UtilisateurDTO result = facadeUtilisateurImpl.creerUtilisateur(creerDTO);

        assertThat(result).isNotNull();
        Assertions.assertThat(result.pseudo()).isEqualTo("pseudo");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    public void testModifierUtilisateurOK() throws UtilisateurInexistantException {
        ModifierUtilisateurDTO modifierDTO = new ModifierUtilisateurDTO("nouveauPseudo","j","j","j");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> (Utilisateur) invocation.getArgument(0));

        UtilisateurDTO result = facadeUtilisateurImpl.modifierUtilisateur(userId, modifierDTO);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.pseudo()).isEqualTo("nouveauPseudo");
        verify(utilisateurRepository, times(1)).findById(userId);
        verify(utilisateurRepository, times(1)).save(dummyUtilisateur);
    }

    @Test
    public void testModifierUtilisateur_NotFoundKO() {
        ModifierUtilisateurDTO modifierDTO = new ModifierUtilisateurDTO("j","j","j","j");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UtilisateurInexistantException.class, () -> {
            facadeUtilisateurImpl.modifierUtilisateur(userId, modifierDTO);
        });
    }

    @Test
    public void testSupprimerUtilisateurOK() throws UtilisateurInexistantException {
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));

        facadeUtilisateurImpl.supprimerUtilisateur(userId);

        verify(utilisateurRepository, times(1)).delete(dummyUtilisateur);
    }

    @Test
    public void testSupprimerUtilisateur_NotFoundKO() {
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UtilisateurInexistantException.class, () -> {
            facadeUtilisateurImpl.supprimerUtilisateur(userId);
        });
    }

//    @Test
//    public void testConsulterUtilisateurOK() throws UtilisateurInexistantException {
//        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));
//
//        UtilisateurDTO result = facadeUtilisateurImpl.consulterUtilisateur(userId);
//
//        Assertions.assertThat(result).isNotNull();
//        verify(utilisateurRepository, times(1)).findById(userId);
//        // Capturing the call to keycloakEventsSender.send
//        ArgumentCaptor<UUID> capEventId = ArgumentCaptor.forClass(UUID.class);
//        ArgumentCaptor<UUID> capUserId = ArgumentCaptor.forClass(UUID.class);
//        ArgumentCaptor<Utilisateur> capUtilisateur = ArgumentCaptor.forClass(Utilisateur.class);
//        verify(keycloakEventsSender, times(1))
//                .send(capEventId.capture(), capUserId.capture(), capUtilisateur.capture());
//        Assertions.assertThat(capUserId.getValue()).isEqualTo(userId);
//    }

    @Test
    public void testModifierAvatarOK() throws UtilisateurInexistantException {
        Long newAvatarId = 200L;
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> (Utilisateur) invocation.getArgument(0));

        facadeUtilisateurImpl.modifierAvatar(userId, newAvatarId);

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository, times(1)).save(captor.capture());
        Assertions.assertThat(captor.getValue().getIdAvatar()).isEqualTo(newAvatarId);
    }

    @Test
    public void testGetProfileUtilisateurOK() throws UtilisateurInexistantException {
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));

        ProfileDTO result = facadeUtilisateurImpl.getProfileUtilisateur(userId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.pseudo()).isEqualTo(dummyUtilisateur.getPseudo());
        verify(utilisateurRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUtilisateursOK() {
        Utilisateur utilisateur2 = new Utilisateur(UUID.randomUUID(), "pseudo2", "nom2", "prenom2", "email2@test.com", LocalDate.now());
        List<Utilisateur> utilisateurs = Arrays.asList(dummyUtilisateur, utilisateur2);
        when(utilisateurRepository.findAll()).thenReturn(utilisateurs);

        List<UtilisateurDTO> result = facadeUtilisateurImpl.getUtilisateurs();

        Assertions.assertThat(result.size()).isEqualTo(2);
        verify(utilisateurRepository, times(1)).findAll();
    }
}