package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;

import java.util.List;
import java.util.UUID;

public interface FacadeUtilisateurAdmin {

    public Utilisateur creerUtilisateur(CreerUtilisateurDTO creerUtilisateurDTO, UUID idKeycloakAdmin);
    public Utilisateur modifierUtilisateur(UUID idKeycloakUtilisateur, ModifierUtilisateurDTO modifierUtilisateurDTO, UUID idKeycloakAdmin) throws Exception;
    public void supprimerUtilisateur(UUID idKeycloakUtilisateur, UUID idKeycloakAdmin) throws Exception;
    public Utilisateur consulterUtilisateur(String pseudo);
    public List<Utilisateur> listerUtilisateurs(UUID idKeycloakAdmin);
}
