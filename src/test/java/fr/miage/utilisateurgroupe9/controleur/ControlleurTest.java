package fr.miage.utilisateurgroupe9.controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;
import fr.miage.utilisateurgroupe9.services.FacadeUtilisateur;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controlleur.class)
@Import(ControlleurTest.MockFacadeImageConfiguration.class)
public class ControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacadeUtilisateur facadeUtilisateur;

    @TestConfiguration
    static class MockFacadeImageConfiguration {
        @Bean
        public FacadeUtilisateur facadeUtilisateur() {
            return Mockito.mock(FacadeUtilisateur.class);
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    private final String USER_UUID = "11111111-1111-1111-1111-111111111111";

    private RequestPostProcessor validJwt() {
        return jwt().jwt(Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", USER_UUID)
                .build());
    }

    @Test
    public void testModifierAvatarOK() throws Exception {
        Long imageId = 123L;
        String jsonBody = objectMapper.writeValueAsString(new Controlleur.ImageInfoDTO(imageId));

        mockMvc.perform(put("/utilisateurs/avatar")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(facadeUtilisateur, times(1))
                .modifierAvatar(UUID.fromString(USER_UUID), imageId);
    }

    @Test
    public void testGetProfileOK() throws Exception {
        ProfileDTO dummyProfile = new ProfileDTO("pseudo", "nom", "prenom", "email", 123L);
        when(facadeUtilisateur.getProfileUtilisateur(UUID.fromString(USER_UUID))).thenReturn(dummyProfile);

        mockMvc.perform(get("/utilisateurs/profile")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Succès")))
                .andExpect(jsonPath("$.contenu", notNullValue()))
                .andExpect(jsonPath("$.contenu.pseudo", is("pseudo")))
                .andExpect(jsonPath("$.contenu.nom", is("nom")))
                .andExpect(jsonPath("$.contenu.prenom", is("prenom")))
                .andExpect(jsonPath("$.contenu.email", is("email")))
                .andExpect(jsonPath("$.contenu.idAvatar", is(123)));

        verify(facadeUtilisateur, times(1))
                .getProfileUtilisateur(UUID.fromString(USER_UUID));
    }

    @Test
    public void testGetUtilisateursOK() throws Exception {
        UtilisateurDTO dto1 = new UtilisateurDTO(UUID.randomUUID(), "pseudo1", 123L);
        UtilisateurDTO dto2 = new UtilisateurDTO(UUID.randomUUID(), "pseudo2", 123L);
        List<UtilisateurDTO> utilisateurs = Arrays.asList(dto1, dto2);
        when(facadeUtilisateur.getUtilisateurs()).thenReturn(utilisateurs);

        mockMvc.perform(get("/utilisateurs/utilisateurs")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Succès")))
                .andExpect(jsonPath("$.contenu.length()", is(2)));

        verify(facadeUtilisateur, times(1))
                .getUtilisateurs();
    }
}