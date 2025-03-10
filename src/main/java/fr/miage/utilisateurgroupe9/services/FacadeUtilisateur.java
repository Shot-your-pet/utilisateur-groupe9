package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;

import java.util.UUID;

public interface FacadeUtilisateur {

    public Utilisateur creerUtilisateur(CreerUtilisateurDTO creerUtilisateurDTO);
    public Utilisateur modifierUtilisateur(UUID idKeycloakUtilisateur, ModifierUtilisateurDTO modifierUtilisateurDTO) throws Exception;
    public void supprimerUtilisateur(UUID idKeycloakUtilisateur) throws Exception;
    public Utilisateur consulterUtilisateur(UUID idKeycloak);
    public void modifierAvatar(UUID idKeycloakUtilisateur, Long idImage) throws Exception;
}
