package projet_gui.Entities;

public class Culture {
    private int id;
    private String nom;
    private Double besoinEau;
    private Double besoinNutriments;
    private Statut statut;
    private String imagePath;
    private Parcelle parcelle;
    private String description;

    public enum Statut {
        HEALTHY, SICK, HARVESTED, FAILED
    }

    public Culture() {
    }

    public Culture(String nom, Double besoinEau, Double besoinNutriments, Statut statut, String imagePath, Parcelle parcelle, String description) {
        this.nom = nom;
        this.besoinEau = besoinEau;
        this.besoinNutriments = besoinNutriments;
        this.statut = statut;
        this.imagePath = imagePath;
        this.parcelle = parcelle;
        this.description = description;
    }

    public Culture(int id, String nom, Double besoinEau, Double besoinNutriments, Statut statut, String imagePath, Parcelle parcelle, String description) {
        this(nom, besoinEau, besoinNutriments, statut, imagePath, parcelle, description);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Crop name cannot be empty");
        }
        this.nom = nom;
    }

    public Double getBesoinEau() { return besoinEau; }
    public void setBesoinEau(Double besoinEau) {
        if (besoinEau == null || besoinEau < 0) {
            throw new IllegalArgumentException("Water requirement must be a positive number");
        }
        this.besoinEau = besoinEau;
    }

    public Double getBesoinNutriments() { return besoinNutriments; }
    public void setBesoinNutriments(Double besoinNutriments) {
        if (besoinNutriments == null || besoinNutriments < 0) {
            throw new IllegalArgumentException("Nutrient requirement must be a positive number");
        }
        this.besoinNutriments = besoinNutriments;
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public Parcelle getParcelle() { return parcelle; }
    public void setParcelle(Parcelle parcelle) { this.parcelle = parcelle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Culture{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", besoinEau=" + besoinEau +
                ", besoinNutriments=" + besoinNutriments +
                ", imagePath='" + imagePath + '\'' +
                ", statut=" + statut +
                ", parcelle=" + parcelle +
                ", description='" + description + '\'' +
                '}';
    }
}