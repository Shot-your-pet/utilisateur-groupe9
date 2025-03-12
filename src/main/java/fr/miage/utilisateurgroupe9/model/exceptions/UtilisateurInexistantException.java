package fr.miage.utilisateurgroupe9.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UtilisateurInexistantException extends Exception {
    public UtilisateurInexistantException(UUID idUtilisateur) {
        super("L'utilisateur " + idUtilisateur.toString() + " n'existe pas");
    }
}
