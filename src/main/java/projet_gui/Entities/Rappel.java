package projet_gui.Entities;

import java.sql.Timestamp;

public class Rappel {
    private int id;
    private Parcelle parcelle;
    private String message;
    private Timestamp dateRappel;
    private Boolean estComplete;

    public Rappel() {
    }

    public Rappel(Parcelle parcelle, String message, Timestamp dateRappel, Boolean estComplete) {
        this.parcelle = parcelle;
        this.message = message;
        this.dateRappel = dateRappel;
        this.estComplete = estComplete;
    }

    public Rappel(int id, Parcelle parcelle, String message, Timestamp dateRappel, Boolean estComplete) {
        this(parcelle, message, dateRappel, estComplete);
        this.id = id;
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

    public String getMessage() { return message; }
    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        this.message = message;
    }

    public Timestamp getDateRappel() { return dateRappel; }
    public void setDateRappel(Timestamp dateRappel) {
        if (dateRappel == null) {
            throw new IllegalArgumentException("Reminder date cannot be null");
        }
        this.dateRappel = dateRappel;
    }

    public Boolean getEstComplete() { return estComplete; }
    public void setEstComplete(Boolean estComplete) {
        this.estComplete = estComplete != null ? estComplete : false;
    }

    @Override
    public String toString() {
        return "Rappel{" +
                "id=" + id +
                ", parcelle=" + parcelle +
                ", message='" + message + '\'' +
                ", dateRappel=" + dateRappel +
                ", estComplete=" + estComplete +
                '}';
    }
}