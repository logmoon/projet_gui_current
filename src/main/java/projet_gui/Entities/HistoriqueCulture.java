package projet_gui.Entities;

import java.sql.Timestamp;

public class HistoriqueCulture {
    private int id;
    private Parcelle parcelle;
    private Culture culture;
    private Timestamp dateDebut;
    private Timestamp dateFin;
    private Double rendement;
    private String notes;

    public HistoriqueCulture() {
    }

    public HistoriqueCulture(Parcelle parcelle, Culture culture, Timestamp dateDebut, 
                           Timestamp dateFin, Double rendement, String notes) {
        this.parcelle = parcelle;
        this.culture = culture;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.rendement = rendement;
        this.notes = notes;
    }

    public HistoriqueCulture(int id, Parcelle parcelle, Culture culture, Timestamp dateDebut,
                           Timestamp dateFin, Double rendement, String notes) {
        this(parcelle, culture, dateDebut, dateFin, rendement, notes);
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

    public Culture getCulture() { return culture; }
    public void setCulture(Culture culture) {
        if (culture == null) {
            throw new IllegalArgumentException("Crop cannot be null");
        }
        this.culture = culture;
    }

    public Timestamp getDateDebut() { return dateDebut; }
    public void setDateDebut(Timestamp dateDebut) {
        if (dateDebut == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        this.dateDebut = dateDebut;
    }

    public Timestamp getDateFin() { return dateFin; }
    public void setDateFin(Timestamp dateFin) { this.dateFin = dateFin; }

    public Double getRendement() { return rendement; }
    public void setRendement(Double rendement) {
        if (rendement != null && rendement < 0) {
            throw new IllegalArgumentException("Yield cannot be negative");
        }
        this.rendement = rendement;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "HistoriqueCulture{" +
                "id=" + id +
                ", parcelle=" + parcelle +
                ", culture=" + culture +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", rendement=" + rendement +
                ", notes='" + notes + '\'' +
                '}';
    }
}