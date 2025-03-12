package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.exceptions.UtilisateurInexistantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeycloakEventListenerTest {

    @Mock
    private FacadeUtilisateurAdmin facadeUtilisateurAdmin;

    @Mock
    private FacadeUtilisateur facadeUtilisateur;


    @InjectMocks
    private KeycloakEventListener keycloakEventListener;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    public void testReceiveMessage_RegisterEventOK() throws Exception {
        Map<String, Object> details = new HashMap<>();
        details.put("username", "user123");
        details.put("firstName", "John");
        details.put("lastName", "Doe");
        details.put("email", "john.doe@test.com");
        details.put("dateNaissance", "2000-01-01");

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "REGISTER");
        payload.put("details", details);
        payload.put("userId", userId.toString());
        // supply some values at root level in case details are missing
        payload.put("username", "user123");
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("email", "john.doe@test.com");
        payload.put("dateNaissance", "2000-01-01");

        keycloakEventListener.receiveMessage(payload);

        ArgumentCaptor<CreerUtilisateurDTO> captor = ArgumentCaptor.forClass(CreerUtilisateurDTO.class);
        verify(facadeUtilisateur, times(1)).creerUtilisateur(captor.capture());
        CreerUtilisateurDTO dto = captor.getValue();
        assertThat(dto.idKeycloak()).isEqualTo(userId);
        assertThat(dto.pseudo()).isEqualTo("user123");
    }

    @Test
    public void testReceiveMessage_UpdateProfileEventOK() throws Exception {
        Map<String, Object> details = new HashMap<>();
        details.put("updated_first_name", "Jane");
        details.put("updated_last_name", "Smith");
        details.put("updated_email", "jane.smith@test.com");

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "UPDATE_PROFILE");
        payload.put("details", details);
        payload.put("userId", userId.toString());
        // include username at root level
        payload.put("username", "user123");
        payload.put("firstName", "John");
        payload.put("lastName", "Doe");
        payload.put("email", "john.doe@test.com");

        keycloakEventListener.receiveMessage(payload);

        ArgumentCaptor<ModifierUtilisateurDTO> captor = ArgumentCaptor.forClass(ModifierUtilisateurDTO.class);
        verify(facadeUtilisateur, times(1))
                .modifierUtilisateur(eq(userId), captor.capture());
        ModifierUtilisateurDTO dto = captor.getValue();
        assertThat(dto.pseudo()).isEqualTo("user123");
        // updated values take priority over the root values
        assertThat(dto.prenom()).isEqualTo("Jane");
        assertThat(dto.nom()).isEqualTo("Smith");
        assertThat(dto.email()).isEqualTo("jane.smith@test.com");
    }

    @Test
    public void testReceiveMessage_DeleteEventOK() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "DELETE");
        payload.put("userId", userId.toString());

        keycloakEventListener.receiveMessage(payload);

        verify(facadeUtilisateur, times(1)).supprimerUtilisateur(userId);
    }

    @Test
    public void testReceiveMessage_AdminCreateEventOK() throws Exception {
        Map<String, Object> representation = new HashMap<>();
        representation.put("username", "adminUser");
        representation.put("firstName", "Admin");
        representation.put("lastName", "User");
        representation.put("email", "admin.user@test.com");

        Map<String, Object> payload = new HashMap<>();
        payload.put("operationType", "CREATE");
        // simulate resourcePath format: "/{userId}"
        payload.put("resourcePath", "/" + userId);
        payload.put("representation", representation);

        keycloakEventListener.receiveMessage(payload);

        ArgumentCaptor<CreerUtilisateurDTO> captor = ArgumentCaptor.forClass(CreerUtilisateurDTO.class);
        verify(facadeUtilisateurAdmin, times(1))
                .creerUtilisateur(captor.capture(), any(UUID.class));
        CreerUtilisateurDTO dto = captor.getValue();
        assertThat(dto.pseudo()).isEqualTo("adminUser");
    }

    @Test
    public void testReceiveMessage_AdminUpdateEventOK() throws Exception {
        Map<String, Object> representation = new HashMap<>();
        representation.put("username", "adminUserUpdated");
        representation.put("firstName", "Admin");
        representation.put("lastName", "UserUpdated");
        representation.put("email", "updated@test.com");

        Map<String, Object> payload = new HashMap<>();
        payload.put("operationType", "UPDATE");
        payload.put("resourcePath", "/" + userId);
        payload.put("representation", representation);

        keycloakEventListener.receiveMessage(payload);

        ArgumentCaptor<ModifierUtilisateurDTO> captor = ArgumentCaptor.forClass(ModifierUtilisateurDTO.class);
        verify(facadeUtilisateurAdmin, times(1))
                .modifierUtilisateur(eq(userId), captor.capture(), any(UUID.class));
        ModifierUtilisateurDTO dto = captor.getValue();
        assertThat(dto.pseudo()).isEqualTo("adminUserUpdated");
        assertThat(dto.nom()).isEqualTo("UserUpdated");
    }

    @Test
    public void testReceiveMessage_AdminDeleteEventOK() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operationType", "DELETE");
        payload.put("resourcePath", "/" + userId);

        keycloakEventListener.receiveMessage(payload);

        verify(facadeUtilisateurAdmin, times(1))
                .supprimerUtilisateur(eq(userId), any(UUID.class));
    }

    @Test
    public void testModifierAvatarOK() throws Exception {
        // Create an instance of ImageInfo record
        KeycloakEventListener.ImageInfo imageInfo = new KeycloakEventListener.ImageInfo(userId, 555L);

        keycloakEventListener.modifierAvatar(imageInfo);

        verify(facadeUtilisateur, times(1))
                .modifierAvatar(userId, 555L);
    }

    @Test
    public void testGetInfosUtilisateurOK() throws UtilisateurInexistantException {
        keycloakEventListener.getInfosUtilisateur(userId);

        verify(facadeUtilisateur, times(1)).consulterUtilisateur(userId);
    }
}