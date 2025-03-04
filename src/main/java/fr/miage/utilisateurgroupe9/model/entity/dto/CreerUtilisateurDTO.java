package fr.miage.utilisateurgroupe9.model.entity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreerUtilisateurDTO(
        UUID idKeycloak,
        String pseudo,
        String nom,
        String prenom,
        String email,
        LocalDate dateNaissance
) {
}
