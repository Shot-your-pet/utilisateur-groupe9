package fr.miage.utilisateurgroupe9.controleur;

import fr.miage.utilisateurgroupe9.services.FacadeUtilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/utilisateurs")
public class Controlleur {

    private final FacadeUtilisateur facadeUtilisateur;

    public Controlleur(FacadeUtilisateur facadeUtilisateur) {
        this.facadeUtilisateur = facadeUtilisateur;
    }

    public record ImageInfoDTO(Long idImage){}

    @PutMapping("/avatar")
    public ResponseEntity<Void> modifierAvatar(Authentication authentication, @RequestBody ImageInfoDTO imageInfoDTO){
        this.facadeUtilisateur.modifierAvatar(UUID.fromString(authentication.getName()), imageInfoDTO.idImage());
        return ResponseEntity.ok().build();

    }
}
