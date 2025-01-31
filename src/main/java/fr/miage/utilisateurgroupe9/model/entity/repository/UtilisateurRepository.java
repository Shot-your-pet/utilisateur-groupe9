package fr.miage.utilisateurgroupe9.model.entity.repository;

import fr.miage.utilisateurgroupe9.model.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
}
