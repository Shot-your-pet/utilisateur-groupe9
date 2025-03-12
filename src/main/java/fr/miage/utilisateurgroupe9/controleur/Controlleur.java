package fr.miage.utilisateurgroupe9.controleur;

import fr.miage.utilisateurgroupe9.model.entity.dto.ProfileDTO;
import fr.miage.utilisateurgroupe9.model.entity.dto.UtilisateurDTO;
import fr.miage.utilisateurgroupe9.model.exceptions.UtilisateurInexistantException;
import fr.miage.utilisateurgroupe9.services.FacadeUtilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/utilisateurs")
public class Controlleur {

    private final FacadeUtilisateur facadeUtilisateur;

    public Controlleur(FacadeUtilisateur facadeUtilisateur) {
        this.facadeUtilisateur = facadeUtilisateur;
    }

    public record ImageInfoDTO(Long idImage){}
    public record ReponseApi<T>(int code, String message, T contenu){}

    @PutMapping("/avatar")
    public ResponseEntity<Void> modifierAvatar(Authentication authentication, @RequestBody ImageInfoDTO imageInfoDTO) throws Exception {
        this.facadeUtilisateur.modifierAvatar(UUID.fromString(authentication.getName()), imageInfoDTO.idImage());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ReponseApi<ProfileDTO>> getProfile(Authentication authentication) throws UtilisateurInexistantException {
        ProfileDTO profileDTO = this.facadeUtilisateur.getProfileUtilisateur(UUID.fromString(authentication.getName()));
        return ResponseEntity.ok(new ReponseApi<>(200, "Succès", profileDTO));
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<ReponseApi<List<UtilisateurDTO>>> getUtilisateurs(Authentication authentication){
        List<UtilisateurDTO> utilisateurDTOS = this.facadeUtilisateur.getUtilisateurs();
        return ResponseEntity.ok(new ReponseApi<>(200, "Succès", utilisateurDTOS));
    }
}
