package projet_gui.Entities;

import java.sql.Timestamp;

public class Parcelle {
    private int id;
    private String nom;
    private Double longueur;
    private Double largeur;
    private String localisationCity;
    private Utilisateur proprietaire;
    private String imagePath;
    private Timestamp dateCreation;

    public Parcelle() {
    }

    public Parcelle(String nom, Double longueur, Double largeur, String localisationCity, 
                   Utilisateur proprietaire, String imagePath) {
        this.nom = nom;
        this.longueur = longueur;
        this.largeur = largeur;
        this.localisationCity = localisationCity;
        this.proprietaire = proprietaire;
        this.imagePath = imagePath;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be empty");
        }
        this.nom = nom;
    }

    public Double getLongueur() { return longueur; }
    public void setLongueur(Double longeur) {
        if (longeur == null || longeur <= 0) {
            throw new IllegalArgumentException("Surface area must be positive");
        }
        this.longueur = longeur;
    }

    public Double getLargeur() { return largeur; }
    public void setLargeur(Double largeur) {
        if (largeur == null || largeur <= 0) {
            throw new IllegalArgumentException("Area must be positive");
        }
        this.largeur = largeur;
    }

    public String getLocalisationCity() { return localisationCity; }
    public void setLocalisationCity(String localisationCity) {
        if (localisationCity == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.localisationCity = localisationCity;
    }

    public Utilisateur getProprietaire() { return proprietaire; }
    public void setProprietaire(Utilisateur proprietaire) {
        if (proprietaire == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.proprietaire = proprietaire;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }


    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Parcelle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", longueur=" + longueur +
                ", largeur=" + largeur +
                ", localisation=" + localisationCity +
                ", proprietaire=" + proprietaire +
                ", imagePath='" + imagePath + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}