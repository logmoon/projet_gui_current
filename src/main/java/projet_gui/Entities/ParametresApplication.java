package projet_gui.Entities;

public class ParametresApplication {
    private int id;
    private String cle;
    private String valeur;
    private String description;
    private Boolean modifiable;

    public ParametresApplication() {
    }

    public ParametresApplication(String cle, String valeur, String description, Boolean modifiable) {
        this.cle = cle;
        this.valeur = valeur;
        this.description = description;
        this.modifiable = modifiable;
    }

    public ParametresApplication(int id, String cle, String valeur, String description, Boolean modifiable) {
        this(cle, valeur, description, modifiable);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCle() { return cle; }
    public void setCle(String cle) {
        if (cle == null || cle.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        this.cle = cle;
    }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
        this.valeur = valeur;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getModifiable() { return modifiable; }
    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable != null ? modifiable : true;
    }

    @Override
    public String toString() {
        return "ParametresApplication{" +
                "id=" + id +
                ", cle='" + cle + '\'' +
                ", valeur='" + valeur + '\'' +
                ", description='" + description + '\'' +
                ", modifiable=" + modifiable +
                '}';
    }
}