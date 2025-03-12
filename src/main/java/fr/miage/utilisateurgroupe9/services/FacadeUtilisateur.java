package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.exceptions.UtilisateurInexistantException;

import java.util.List;
import java.util.UUID;

public interface FacadeUtilisateur {

    UtilisateurDTO creerUtilisateur(CreerUtilisateurDTO creerUtilisateurDTO);
    UtilisateurDTO modifierUtilisateur(UUID idKeycloakUtilisateur, ModifierUtilisateurDTO modifierUtilisateurDTO) throws UtilisateurInexistantException;
    void supprimerUtilisateur(UUID idKeycloakUtilisateur) throws UtilisateurInexistantException;
    UtilisateurDTO consulterUtilisateur(UUID idKeycloak) throws UtilisateurInexistantException;
    void modifierAvatar(UUID idKeycloakUtilisateur, Long idImage) throws UtilisateurInexistantException;
    ProfileDTO getProfileUtilisateur(UUID idKeycloak) throws UtilisateurInexistantException;
    List<UtilisateurDTO> getUtilisateurs();
}
