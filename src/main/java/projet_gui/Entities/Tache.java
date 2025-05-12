package projet_gui.Entities;

import java.sql.Timestamp;

public class Tache {
    private int id;
    private Parcelle parcelle;
    private String description;
    private String statut;
    private String priorite;
    private Timestamp dateCreation;
    private Timestamp dateEcheance;
    private Culture culture;
    private Utilisateur assignee;

    public static final String STATUT_PENDING = "PENDING";
    public static final String STATUT_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUT_DONE = "DONE";
    public static final String STATUT_CANCELLED = "CANCELLED";

    public static final String PRIORITE_LOW = "LOW";
    public static final String PRIORITE_MEDIUM = "MEDIUM";
    public static final String PRIORITE_HIGH = "HIGH";
    public static final String PRIORITE_URGENT = "URGENT";

    public Tache() {
    }

    public Tache(Parcelle parcelle, String description, String statut, String priorite,
                Timestamp dateEcheance, Culture culture, Utilisateur assignee) {
        this.parcelle = parcelle;
        this.description = description;
        this.statut = statut;
        this.priorite = priorite;
        this.dateEcheance = dateEcheance;
        this.culture = culture;
        this.assignee = assignee;
    }

    public Tache(int id, Parcelle parcelle, String description, String statut, String priorite,
                Timestamp dateCreation, Timestamp dateEcheance, Culture culture, Utilisateur assignee) {
        this(parcelle, description, statut, priorite, dateEcheance, culture, assignee);
        this.id = id;
        this.dateCreation = dateCreation;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Parcelle getParcelle() { return parcelle; }
    public void setParcelle(Parcelle parcelle) {
        if (parcelle == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        this.parcelle = parcelle;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description;
    }

    public String getStatut() { return statut; }
    public void setStatut(String statut) {
        if (statut != null && !statut.equals(STATUT_PENDING) && !statut.equals(STATUT_IN_PROGRESS) &&
            !statut.equals(STATUT_DONE) && !statut.equals(STATUT_CANCELLED)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.statut = statut;
    }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) {
        if (priorite != null && !priorite.equals(PRIORITE_LOW) && !priorite.equals(PRIORITE_MEDIUM) &&
            !priorite.equals(PRIORITE_HIGH) && !priorite.equals(PRIORITE_URGENT)) {
            throw new IllegalArgumentException("Invalid priority value");
        }
        this.priorite = priorite;
    }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public Timestamp getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(Timestamp dateEcheance) { this.dateEcheance = dateEcheance; }

    public Culture getCulture() { return culture; }
    public void setCulture(Culture culture) { this.culture = culture; }

    public Utilisateur getAssignee() { return assignee; }
    public void setAssignee(Utilisateur assignee) { this.assignee = assignee; }

    @Override
    public String toString() {
        return "Tache{" +
                "id=" + id +
                ", parcelle=" + parcelle +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", priorite='" + priorite + '\'' +
                ", dateCreation=" + dateCreation +
                ", dateEcheance=" + dateEcheance +
                ", culture=" + culture +
                ", assignee=" + assignee +
                '}';
    }
}