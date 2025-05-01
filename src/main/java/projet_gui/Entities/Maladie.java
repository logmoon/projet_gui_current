package projet_gui.Entities;

public class Maladie {
    private int id;
    private String nom;
    private String description;
    private String traitement;
    private String symptomes;
    private String imagePath;

    public Maladie() {
    }

    public Maladie(String nom, String description, String traitement, String symptomes, String imagePath) {
        this.nom = nom;
        this.description = description;
        this.traitement = traitement;
        this.symptomes = symptomes;
        this.imagePath = imagePath;
    }

    public Maladie(int id, String nom, String description, String traitement, String symptomes, String imagePath) {
        this(nom, description, traitement, symptomes, imagePath);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Disease name cannot be empty");
        }
        this.nom = nom;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Disease description cannot be empty");
        }
        this.description = description;
    }

    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) {
        if (traitement == null || traitement.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment cannot be empty");
        }
        this.traitement = traitement;
    }

    public String getSymptomes() { return symptomes; }
    public void setSymptomes(String symptomes) { this.symptomes = symptomes; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return "Maladie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", traitement='" + traitement + '\'' +
                ", symptomes='" + symptomes + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}