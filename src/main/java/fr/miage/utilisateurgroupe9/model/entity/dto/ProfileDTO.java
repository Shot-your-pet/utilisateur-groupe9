package fr.miage.utilisateurgroupe9.model.entity.dto;

public record ProfileDTO(
        String pseudo,
        String nom,
        String prenom,
        String email,
        Long idAvatar
) {

}
