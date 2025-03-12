package fr.miage.utilisateurgroupe9.model.mappers;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;

public class UtilisateurMapper {

    public static ProfileDTO toProfileDTO(Utilisateur utilisateur) {
        return new ProfileDTO(
            utilisateur.getPseudo(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getEmail(),
            utilisateur.getIdAvatar()
        );
    }

    public static UtilisateurDTO toUtilisateurDTO(Utilisateur utilisateur) {
        return new UtilisateurDTO(
                utilisateur.getIdKeycloak(),
                utilisateur.getPseudo(),
                utilisateur.getIdAvatar()
        );
    }
}
