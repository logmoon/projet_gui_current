package projet_gui.Entities;

import java.sql.Timestamp;

public class RapportCulture {
    private int id;
    private ParcelleCulture parcelleCulture;
    private String etat;
    private String notes;
    private Timestamp date;

    public RapportCulture() {
    }

    public RapportCulture(ParcelleCulture parcelleCulture, String etat, String notes, Timestamp date) {
        this.parcelleCulture = parcelleCulture;
        this.etat = etat;
        this.notes = notes;
        this.date = date;
    }

    public RapportCulture(int id, ParcelleCulture parcelleCulture, String etat, String notes, Timestamp date) {
        this(parcelleCulture, etat, notes, date);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ParcelleCulture getParcelleCulture() { return parcelleCulture; }
    public void setParcelleCulture(ParcelleCulture parcelleCulture) {
        if (parcelleCulture == null) {
            throw new IllegalArgumentException("Field-Crop relationship cannot be null");
        }
        this.parcelleCulture = parcelleCulture;
    }

    public String getEtat() { return etat; }
    public void setEtat(String etat) {
        if (etat == null || etat.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be empty");
        }
        this.etat = etat;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    @Override
    public String toString() {
        return "RapportCulture{" +
                "id=" + id +
                ", parcelleCulture=" + parcelleCulture +
                ", etat='" + etat + '\'' +
                ", notes='" + notes + '\'' +
                ", date=" + date +
                '}';
    }
}