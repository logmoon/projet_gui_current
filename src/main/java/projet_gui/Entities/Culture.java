package projet_gui.Entities;

public class Culture {
    private int id;
    private String nom;
    private Double besoinEau;
    private Double besoinNutriments;
    private String imagePath;
    private String description;

    public Culture() {
    }

    public Culture(String nom, Double besoinEau, Double besoinNutriments, String imagePath, String description) {
        this.nom = nom;
        this.besoinEau = besoinEau;
        this.besoinNutriments = besoinNutriments;
        this.imagePath = imagePath;
        this.description = description;
    }

    public Culture(int id, String nom, Double besoinEau, Double besoinNutriments, String imagePath, String description) {
        this(nom, besoinEau, besoinNutriments, imagePath, description);
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
                ", description='" + description + '\'' +
                '}';
    }
}