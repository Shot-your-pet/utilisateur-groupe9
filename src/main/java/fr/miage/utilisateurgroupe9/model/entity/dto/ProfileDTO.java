package fr.miage.utilisateurgroupe9.model.entity.dto;

import java.io.Serializable;

public record ProfileDTO(
        String pseudo,
        String nom,
        String prenom,
        String email,
        Long idAvatar
) implements Serializable {

}
