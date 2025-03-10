package fr.miage.utilisateurgroupe9.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Utilisateur {

    @Id
    @Column(unique = true)
    private UUID idKeycloak;

    @Column(unique = true)
    private String pseudo;
    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;
    private LocalDate dateNaissance;
    private LocalDateTime dateCreation;
    private Long idAvatar;

    public Utilisateur() {
    }

    public Utilisateur(UUID idKeycloak, String pseudo, String nom, String prenom, String email, LocalDate dateNaissance) {
        this.idKeycloak = idKeycloak;
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.idAvatar = null;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Long getIdAvatar() {
        return idAvatar;
    }

    public void setIdAvatar(Long idAvatar) {
        this.idAvatar = idAvatar;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public UUID getIdKeycloak() {
        return idKeycloak;
    }

    public void setIdKeycloak(UUID idKeycloak) {
        this.idKeycloak = idKeycloak;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idKeycloak=" + idKeycloak +
                ", pseudo='" + pseudo + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", dateCreation=" + dateCreation +
                ", idAvatar=" + idAvatar +
                '}';
    }
}
