package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.repository.UtilisateurRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacadeUtilisateurAdminImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private FacadeUtilisateurAdminImpl facadeUtilisateurAdminImpl;

    private UUID userId;
    private UUID adminId;
    private Utilisateur dummyUtilisateur;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        dummyUtilisateur = new Utilisateur(userId, "pseudo", "nom", "prenom", "email@test.com", LocalDate.now());
    }

    @Test
    public void testCreerUtilisateurOK() {
        CreerUtilisateurDTO creerDTO = new CreerUtilisateurDTO(UUID.randomUUID(), "pseudo", "e", "ol", "dd", LocalDate.now());

        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur createdUser = facadeUtilisateurAdminImpl.creerUtilisateur(creerDTO, adminId);

        Assertions.assertThat(createdUser).isNotNull();
        Assertions.assertThat(createdUser.getPseudo()).isEqualTo("pseudo");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    public void testModifierUtilisateurOK() throws Exception {
        ModifierUtilisateurDTO modifierDTO = new ModifierUtilisateurDTO("newPseudo", "newNom", "pojp", "new@email.com");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur updatedUser = facadeUtilisateurAdminImpl.modifierUtilisateur(userId, modifierDTO, adminId);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getPseudo()).isEqualTo("newPseudo");
        Assertions.assertThat(updatedUser.getNom()).isEqualTo("newNom");
        Assertions.assertThat(updatedUser.getPrenom()).isEqualTo("pojp");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("new@email.com");
        verify(utilisateurRepository, times(1)).findById(userId);
        verify(utilisateurRepository, times(1)).save(dummyUtilisateur);
    }

    @Test
    public void testModifierUtilisateur_NotFoundKO() {
        ModifierUtilisateurDTO modifierDTO = new ModifierUtilisateurDTO("newPseudo", "newNom", "pojp", "new@email.com");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = Assertions.catchThrowableOfType(() ->
                facadeUtilisateurAdminImpl.modifierUtilisateur(userId, modifierDTO, adminId), Exception.class);
        Assertions.assertThat(exception).hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    public void testSupprimerUtilisateurOK() throws Exception {
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(dummyUtilisateur));
        doNothing().when(utilisateurRepository).delete(dummyUtilisateur);

        facadeUtilisateurAdminImpl.supprimerUtilisateur(userId, adminId);

        verify(utilisateurRepository, times(1)).findById(userId);
        verify(utilisateurRepository, times(1)).delete(dummyUtilisateur);
    }

    @Test
    public void testSupprimerUtilisateur_NotFoundKO() {
        when(utilisateurRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = Assertions.catchThrowableOfType(() ->
                facadeUtilisateurAdminImpl.supprimerUtilisateur(userId, adminId), Exception.class);
        Assertions.assertThat(exception).hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    public void testConsulterUtilisateurOK() {
        // consulterUtilisateur expects a pseudo as parameter.
        when(utilisateurRepository.findByPseudo("pseudo")).thenReturn(Optional.of(dummyUtilisateur));

        Utilisateur consultedUser = facadeUtilisateurAdminImpl.consulterUtilisateur("pseudo");

        Assertions.assertThat(consultedUser).isNotNull();
        Assertions.assertThat(consultedUser.getPseudo()).isEqualTo("pseudo");
        verify(utilisateurRepository, times(1)).findByPseudo("pseudo");
    }

    @Test
    public void testListerUtilisateursOK() {
        Utilisateur utilisateur2 = new Utilisateur(UUID.randomUUID(), "pseudo2", "nom2", "prenom2", "email2@test.com", LocalDate.now());
        List<Utilisateur> utilisateurs = Arrays.asList(dummyUtilisateur, utilisateur2);
        when(utilisateurRepository.findAll()).thenReturn(utilisateurs);

        List<Utilisateur> result = facadeUtilisateurAdminImpl.listerUtilisateurs(adminId);

        Assertions.assertThat(result.size()).isEqualTo(2);
        verify(utilisateurRepository, times(1)).findAll();
    }
}