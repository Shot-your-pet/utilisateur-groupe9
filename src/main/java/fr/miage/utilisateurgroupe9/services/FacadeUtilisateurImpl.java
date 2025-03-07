package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FacadeUtilisateurImpl implements FacadeUtilisateur{

    private final UtilisateurRepository utilisateurRepository;
    private final RabbitEventsSender keycloakEventsSender;

    private static final Logger LOG = LoggerFactory.getLogger(FacadeUtilisateurImpl.class);

    public FacadeUtilisateurImpl(UtilisateurRepository utilisateurRepository, RabbitEventsSender keycloakEventsSender) {
        this.utilisateurRepository = utilisateurRepository;
        this.keycloakEventsSender = keycloakEventsSender;
    }

    @Override
    public Utilisateur creerUtilisateur(CreerUtilisateurDTO creerUtilisateurDTO) {
        // Pas de vérification de si un utilisateur existe déjà avec le pseudo et email car attribut unique
        Utilisateur utilisateur = new Utilisateur(
                creerUtilisateurDTO.idKeycloak(),
                creerUtilisateurDTO.pseudo(),
                creerUtilisateurDTO.nom(),
                creerUtilisateurDTO.prenom(),
                creerUtilisateurDTO.email(),
                creerUtilisateurDTO.dateNaissance());
        LOG.trace("Création de l'utilisateur : {}", utilisateur);
        return this.utilisateurRepository.save(utilisateur);
        // TODO : envoyer email de confirmation, de bienvenu etc ...
    }

    @Override
    public Utilisateur modifierUtilisateur(UUID idKeycloak, ModifierUtilisateurDTO modifierUtilisateurDTO) throws Exception {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        if (modifierUtilisateurDTO.pseudo() != null) {
            utilisateur.setPseudo(modifierUtilisateurDTO.pseudo());
        }
        if (modifierUtilisateurDTO.nom() != null) {
            utilisateur.setNom(modifierUtilisateurDTO.nom());
        }
        if (modifierUtilisateurDTO.prenom() != null) {
            utilisateur.setPrenom(modifierUtilisateurDTO.prenom());
        }
        if (modifierUtilisateurDTO.email() != null) {
            utilisateur.setEmail(modifierUtilisateurDTO.email());
        }
        LOG.trace("Modification de l'utilisateur : {}", utilisateur);
        return this.utilisateurRepository.save(utilisateur);
        // TODO : Notifier les autres services du changement si besoin

    }

    @Override
    public void supprimerUtilisateur(UUID idKeycloak) throws Exception {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        this.utilisateurRepository.delete(utilisateur);
        LOG.trace("Suppression de l'utilisateur : {}", utilisateur);
        // TODO : Notifier les autres services du changement si besoin
    }

    @Override
    public Utilisateur consulterUtilisateur(UUID idKeycloak) {
        LOG.trace("Consultation de l'utilisateur : {}", idKeycloak);
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow();
        this.keycloakEventsSender.send(UUID.randomUUID(), idKeycloak, utilisateur);
        return utilisateur;
    }

    @Override
    public void modifierAvatar(UUID idKeycloak, UUID idImage) throws Exception {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        utilisateur.setIdAvatar(idImage);
        this.utilisateurRepository.save(utilisateur);
    }
}
