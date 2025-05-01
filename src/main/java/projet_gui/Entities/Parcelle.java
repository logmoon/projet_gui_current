package projet_gui.Entities;

import java.sql.Timestamp;

public class Parcelle {
    private int id;
    private String nom;
    private Double superficie;
    private Double area;
    private Localisation localisation;
    private Utilisateur proprietaire;
    private String imagePath;
    private String statut;
    private Timestamp dateCreation;
    private Timestamp derniereMiseAJour;

    public static final String STATUT_NORMAL = "NORMAL";
    public static final String STATUT_NEEDS_ATTENTION = "NEEDS_ATTENTION";
    public static final String STATUT_CRITICAL = "CRITICAL";

    public Parcelle() {
    }

    public Parcelle(String nom, Double superficie, Double area, Localisation localisation, 
                   Utilisateur proprietaire, String imagePath, String statut) {
        this.nom = nom;
        this.superficie = superficie;
        this.area = area;
        this.localisation = localisation;
        this.proprietaire = proprietaire;
        this.imagePath = imagePath;
        this.statut = statut;
    }

    public Parcelle(int id, String nom, Double superficie, Double area, Localisation localisation,
                   Utilisateur proprietaire, String imagePath, String statut, 
                   Timestamp dateCreation, Timestamp derniereMiseAJour) {
        this(nom, superficie, area, localisation, proprietaire, imagePath, statut);
        this.id = id;
        this.dateCreation = dateCreation;
        this.derniereMiseAJour = derniereMiseAJour;
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

    public Double getSuperficie() { return superficie; }
    public void setSuperficie(Double superficie) {
        if (superficie == null || superficie <= 0) {
            throw new IllegalArgumentException("Surface area must be positive");
        }
        this.superficie = superficie;
    }

    public Double getArea() { return area; }
    public void setArea(Double area) {
        if (area == null || area <= 0) {
            throw new IllegalArgumentException("Area must be positive");
        }
        this.area = area;
    }

    public Localisation getLocalisation() { return localisation; }
    public void setLocalisation(Localisation localisation) {
        if (localisation == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.localisation = localisation;
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

    public String getStatut() { return statut; }
    public void setStatut(String statut) {
        if (statut != null && !statut.equals(STATUT_NORMAL) && 
            !statut.equals(STATUT_NEEDS_ATTENTION) && !statut.equals(STATUT_CRITICAL)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.statut = statut;
    }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public Timestamp getDerniereMiseAJour() { return derniereMiseAJour; }
    public void setDerniereMiseAJour(Timestamp derniereMiseAJour) { this.derniereMiseAJour = derniereMiseAJour; }

    @Override
    public String toString() {
        return "Parcelle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", superficie=" + superficie +
                ", area=" + area +
                ", localisation=" + localisation +
                ", proprietaire=" + proprietaire +
                ", imagePath='" + imagePath + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCreation=" + dateCreation +
                ", derniereMiseAJour=" + derniereMiseAJour +
                '}';
    }
}