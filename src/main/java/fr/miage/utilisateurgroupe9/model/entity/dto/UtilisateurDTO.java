package fr.miage.utilisateurgroupe9.model.entity.dto;

import java.io.Serializable;
import java.util.UUID;

public record UtilisateurDTO(
    UUID idUtilisateur,
    String pseudo,
    Long idAvatar
    ) implements Serializable {
}
