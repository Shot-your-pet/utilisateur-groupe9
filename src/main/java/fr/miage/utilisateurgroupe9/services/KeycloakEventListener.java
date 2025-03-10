package fr.miage.utilisateurgroupe9.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Service
public class KeycloakEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakEventListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KEYCLOAK_QUEUE = "keycloak.keycloak_events";
    private static final String UPDATE_AVATAR_QUEUE = "images.update_avatar";
    private static final String GET_INFOS_UTILISATEUR_QUEUE = "utilisateurs.infos_utilisateur";
    private final FacadeUtilisateurAdmin facadeUtilisateurAdmin;
    private final FacadeUtilisateur facadeUtilisateur;

    public KeycloakEventListener(FacadeUtilisateurAdmin facadeUtilisateurAdmin, FacadeUtilisateur facadeUtilisateur) {
        this.facadeUtilisateurAdmin = facadeUtilisateurAdmin;
        this.facadeUtilisateur = facadeUtilisateur;
    }


    @RabbitListener(queues = KEYCLOAK_QUEUE)
    public void receiveMessage(Map<String, Object> payload) {
        try {
            // Convertir le message JSON en une Map
//            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});
            LOG.info("Message reçu: {}", payload);

            // Vérifier si c'est un événement utilisateur (propriété "eventType") ou admin ("operationType")
            if (payload.containsKey("eventType")) {
                String eventType = (String) payload.get("eventType");
                Map<String, Object> details;
                switch (eventType) {
                    case "REGISTER":
                        // Les informations de l'utilisateur sont dans "details"
                        details = (Map<String, Object>) payload.get("details");

                        String username = (String) payload.get("username");
                        if (username == null && details != null) {
                            username = (String) details.get("username");
                        }

                        String firstName = (String) payload.get("firstName");
                        if (firstName == null && details != null) {
                            firstName = (String) details.get("firstName");
                            if(firstName == null) {
                                firstName = (String) details.get("first_name");
                            }
                        }

                        String lastName = (String) payload.get("lastName");
                        if (lastName == null && details != null) {
                            lastName = (String) details.get("lastName");
                            if(lastName == null) {
                                lastName = (String) details.get("last_name");
                            }
                        }

                        String email = (String) payload.get("email");
                        if (email == null && details != null) {
                            email = (String) details.get("email");
                        }

                        String dateNaissanceStr = (String) payload.get("dateNaissance");
                        if (dateNaissanceStr == null && details != null) {
                            dateNaissanceStr = (String) details.get("dateNaissance");
                        }
                        LocalDate dateNaissance = null;
                        if (dateNaissanceStr != null && !dateNaissanceStr.trim().isEmpty()) {
                            dateNaissance = LocalDate.parse(dateNaissanceStr);
                        }

                        CreerUtilisateurDTO creerUtilisateurDTO = new CreerUtilisateurDTO(
                                UUID.fromString((String) payload.get("userId")),
                                username,
                                firstName,
                                lastName,
                                email,
                                dateNaissance
                        );
                        facadeUtilisateur.creerUtilisateur(creerUtilisateurDTO);
                        LOG.trace("Evenement keycloak de création d'utilisateur terminé: {}", eventType);
                        break;

                    case "UPDATE_PROFILE":
                        details = (Map<String, Object>) payload.get("details");

                        // Récupération de l'username (si disponible au niveau racine)
                        String updatedUsername = (String) payload.get("username");

                        // Pour le prénom, on privilégie la valeur mise à jour dans "details" sinon le champ au niveau racine
                        String updatedFirstName = details != null && details.get("updated_first_name") != null
                                ? (String) details.get("updated_first_name")
                                : (String) payload.get("firstName");

                        // Pour le nom, on fait de même (ici on cherche "updated_last_name" si disponible)
                        String updatedLastName = details != null && details.get("updated_last_name") != null
                                ? (String) details.get("updated_last_name")
                                : (String) payload.get("lastName");

                        // Pour l'email, on recherche également une valeur mise à jour dans "details"
                        String updatedEmail = details != null && details.get("updated_email") != null
                                ? (String) details.get("updated_email")
                                : (String) payload.get("email");

                        ModifierUtilisateurDTO modifierUtilisateurDTO = new ModifierUtilisateurDTO(
                                updatedUsername,
                                updatedLastName,
                                updatedFirstName,
                                updatedEmail
                        );
                        facadeUtilisateur.modifierUtilisateur(
                                UUID.fromString((String) payload.get("userId")),
                                modifierUtilisateurDTO
                        );
                        LOG.trace("Evenement keycloak de modification d'utilisateur terminé: {}", eventType);
                        break;


                    case "DELETE":
                        facadeUtilisateur.supprimerUtilisateur(UUID.fromString((String) payload.get("userId")));
                        LOG.trace("Evenement keycloak de suppression d'utilisateur terminé: {}", eventType);
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
                        LOG.trace("Evenement admin keycloak de création d'utilisateur terminé: {}", operationType);
                        break;
                    case "UPDATE":
                        ModifierUtilisateurDTO modifierUtilisateurDTO = new ModifierUtilisateurDTO(
                                (String) createRepresentation.get("username"),
                                (String) createRepresentation.get("lastName"),
                                (String) createRepresentation.get("firstName"),
                                (String) createRepresentation.get("email")
                        );
                        this.facadeUtilisateurAdmin.modifierUtilisateur(UUID.fromString(payload.get("resourcePath").toString().split("/")[1]), modifierUtilisateurDTO, randomUUID());
                        LOG.trace("Evenement admin keycloak de modification d'utilisateur terminé: {}", operationType);
                        break;
                    case "DELETE":
                        this.facadeUtilisateurAdmin.supprimerUtilisateur(UUID.fromString(payload.get("resourcePath").toString().split("/")[1]), randomUUID());
                        LOG.trace("Evenement admin keycloak de suppression d'utilisateur terminé: {}", operationType);
                        break;
                    default:
                        LOG.trace("Événement admin non pris en charge: {}", operationType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public record ImageInfo(UUID idKeycloak, Long idImage) implements Serializable {
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
