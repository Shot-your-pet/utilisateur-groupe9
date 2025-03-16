package fr.miage.utilisateurgroupe9.services;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import fr.miage.utilisateurgroupe9.model.entity.dto.CreerUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ModifierUtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.entity.repository.UtilisateurRepository;
import fr.miage.utilisateurgroupe9.model.exceptions.UtilisateurInexistantException;
import fr.miage.utilisateurgroupe9.model.mappers.UtilisateurMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public UtilisateurDTO creerUtilisateur(CreerUtilisateurDTO creerUtilisateurDTO) {
        // Pas de vérification de si un utilisateur existe déjà avec le pseudo et email car attribut unique
        Utilisateur utilisateur = new Utilisateur(
                creerUtilisateurDTO.idKeycloak(),
                creerUtilisateurDTO.pseudo(),
                creerUtilisateurDTO.nom(),
                creerUtilisateurDTO.prenom(),
                creerUtilisateurDTO.email(),
                creerUtilisateurDTO.dateNaissance());
        LOG.trace("Création de l'utilisateur : {}", utilisateur);
        return UtilisateurMapper.toUtilisateurDTO(this.utilisateurRepository.save(utilisateur));
        // TODO : envoyer email de confirmation, de bienvenu etc ...
    }

    @Override
    public UtilisateurDTO modifierUtilisateur(UUID idKeycloak, ModifierUtilisateurDTO modifierUtilisateurDTO) throws UtilisateurInexistantException {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new UtilisateurInexistantException(idKeycloak));
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
        return UtilisateurMapper.toUtilisateurDTO(this.utilisateurRepository.save(utilisateur));
        // TODO : Notifier les autres services du changement si besoin

    }

    @Override
    public void supprimerUtilisateur(UUID idKeycloak) throws UtilisateurInexistantException {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new UtilisateurInexistantException(idKeycloak));
        this.utilisateurRepository.delete(utilisateur);
        LOG.trace("Suppression de l'utilisateur : {}", utilisateur);
        // TODO : Notifier les autres services du changement si besoin
    }

    @Override
    public UtilisateurDTO consulterUtilisateur(UUID idKeycloak) throws UtilisateurInexistantException {
        LOG.trace("Consultation de l'utilisateur : {}", idKeycloak);
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new UtilisateurInexistantException(idKeycloak));
        return UtilisateurMapper.toUtilisateurDTO(utilisateur);
    }

    @Override
    public void modifierAvatar(UUID idKeycloak, Long idImage) throws UtilisateurInexistantException {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new UtilisateurInexistantException(idKeycloak));
        utilisateur.setIdAvatar(idImage);
        this.utilisateurRepository.save(utilisateur);
    }

    @Override
    public ProfileDTO getProfileUtilisateur(UUID idKeycloak) throws UtilisateurInexistantException {
        Utilisateur utilisateur = this.utilisateurRepository.findById(idKeycloak).orElseThrow(() -> new UtilisateurInexistantException(idKeycloak));
        return UtilisateurMapper.toProfileDTO(utilisateur);
    }

    @Override
    public List<UtilisateurDTO> getUtilisateurs() {
        List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
        return utilisateurs.stream().map(UtilisateurMapper::toUtilisateurDTO).toList();
    }
}
