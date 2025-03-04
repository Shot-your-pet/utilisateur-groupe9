package fr.miage.utilisateurgroupe9.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Service
public class KeycloakEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakEventListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEYCLOAK_QUEUE = "keycloak_events";
    private static final String UPDATE_AVATAR_QUEUE = "update_avatar";
    private static final String GET_INFOS_UTILISATEUR_QUEUE = "infos_utilisateur";
    private final FacadeUtilisateurAdmin facadeUtilisateurAdmin;
    private final FacadeUtilisateur facadeUtilisateur;

    public KeycloakEventListener(FacadeUtilisateurAdmin facadeUtilisateurAdmin, FacadeUtilisateur facadeUtilisateur) {
        this.facadeUtilisateurAdmin = facadeUtilisateurAdmin;
        this.facadeUtilisateur = facadeUtilisateur;
    }


    @RabbitListener(queues = KEYCLOAK_QUEUE)
    public void receiveMessage(String message) {
        try {
            // Convertir le message JSON en une Map
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
            System.out.println("Message reçu: " + payload);

            // Vérifier si c'est un événement utilisateur (propriété "eventType") ou admin ("operationType")
            if (payload.containsKey("eventType")) {
                String eventType = (String) payload.get("eventType");

                switch (eventType) {
                    case "REGISTER":
                        CreerUtilisateurDTO creerUtilisateurDTO = new CreerUtilisateurDTO(
                                (UUID) payload.get("userId"),
                                (String) payload.get("username"),
                                (String) payload.get("firstName"),
                                (String) payload.get("lastName"),
                                (String) payload.get("email"),
                                (LocalDate) payload.get("dateNaissance")
                        );
                        this.facadeUtilisateur.creerUtilisateur(creerUtilisateurDTO);
                        LOG.trace("Evenement keycloak de création d'utilisateur: {}", eventType);
                        break;
                    case "UPDATE_PROFILE":
                        ModifierUtilisateurDTO modifierUtilisateurDTO = new ModifierUtilisateurDTO(
                                (String) payload.get("username"),
                                (String) payload.get("firstName"),
                                (String) payload.get("lastName"),
                                (String) payload.get("email")
                        );
                        this.facadeUtilisateur.modifierUtilisateur((UUID) payload.get("userId"), modifierUtilisateurDTO);
                        LOG.trace("Evenement keycloak de modification d'utilisateur: {}", eventType);
                        break;
                    case "DELETE":
                        this.facadeUtilisateur.supprimerUtilisateur((UUID) payload.get("userId"));
                        LOG.trace("Evenement keycloak de suppresion d'utilisateur: {}", eventType);
                        break;
                    default:
                        LOG.trace("Événement utilisateur non pris en charge: {}", eventType);
                }
            } else if (payload.containsKey("operationType")) {
                String operationType = (String) payload.get("operationType");
                Object repObj = payload.get("representation");
                Map<String, Object> createRepresentation;
                if (repObj instanceof String) {
                    createRepresentation = objectMapper.readValue((String) repObj, new TypeReference<Map<String, Object>>() {});
                } else {
                    createRepresentation = objectMapper.convertValue(repObj, new TypeReference<Map<String, Object>>() {});
                }
                LOG.trace("Rpz : {}", createRepresentation);
                switch (operationType) {
                    case "CREATE":
                        CreerUtilisateurDTO creerUtilisateurDTO = new CreerUtilisateurDTO(
                                UUID.fromString(payload.get("resourcePath").toString().split("/")[1]),
                                (String) createRepresentation.get("username"),
                                (String) createRepresentation.get("firstName"),
                                (String) createRepresentation.get("lastName"),
                                (String) createRepresentation.get("email"),
                                LocalDate.now()
//                                LocalDate.parse((String) createRepresentation.get("dateNaissance"))
                        );
                        this.facadeUtilisateurAdmin.creerUtilisateur(creerUtilisateurDTO, randomUUID());
                        LOG.trace("Evenement admin keycloak de création d'utilisateur: {}", operationType);
                        break;
                    case "UPDATE":
                        ModifierUtilisateurDTO modifierUtilisateurDTO = new ModifierUtilisateurDTO(
                                (String) createRepresentation.get("username"),
                                (String) createRepresentation.get("firstName"),
                                (String) createRepresentation.get("lastName"),
                                (String) createRepresentation.get("email")
                        );
                        this.facadeUtilisateurAdmin.modifierUtilisateur(UUID.fromString(payload.get("resourcePath").toString().split("/")[1]), modifierUtilisateurDTO, randomUUID());
                        LOG.trace("Evenement admin keycloak de modification d'utilisateur: {}", operationType);
                        break;
                    case "DELETE":
                        this.facadeUtilisateurAdmin.supprimerUtilisateur(UUID.fromString(payload.get("resourcePath").toString().split("/")[1]), randomUUID());
                        LOG.trace("Evenement admin keycloak de suppression d'utilisateur: {}", operationType);
                        break;
                    default:
                        LOG.trace("Événement admin non pris en charge: {}", operationType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public record ImageInfo(UUID idKeycloak, UUID idImage) implements Serializable {
    }

    @RabbitListener(queues = UPDATE_AVATAR_QUEUE)
    public void modifierAvatar(ImageInfo imageInfo) throws Exception {
        LOG.info("Modification de l'avatar de l'utilisateur : {}", imageInfo);
        this.facadeUtilisateur.modifierAvatar(imageInfo.idKeycloak(), imageInfo.idImage());
    }

    @RabbitListener(queues = GET_INFOS_UTILISATEUR_QUEUE)
    public void getInfosUtilisateur(UUID idKeycloak) {
        this.facadeUtilisateur.consulterUtilisateur(idKeycloak);
    }
}
