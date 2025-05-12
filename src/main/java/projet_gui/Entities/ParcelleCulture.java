package projet_gui.Entities;

import java.sql.Timestamp;

public class ParcelleCulture {
    private int id;
    private Parcelle parcelle;
    private Culture culture;
    private String statut;
    private Maladie maladie;
    private Timestamp dateAjout;

    public static final String STATUT_HEALTHY = "HEALTHY";
    public static final String STATUT_SICK = "SICK";
    public static final String STATUT_HARVESTED = "HARVESTED";
    public static final String STATUT_FAILED = "FAILED";

    public ParcelleCulture() {
    }

    public ParcelleCulture(Parcelle parcelle, Culture culture, String statut, Maladie maladie, Timestamp dateAjout) {
        this.parcelle = parcelle;
        this.culture = culture;
        this.statut = statut;
        this.maladie = maladie;
        this.dateAjout = dateAjout;
    }

    public ParcelleCulture(int id, Parcelle parcelle, Culture culture, String statut, Maladie maladie, Timestamp dateAjout) {
        this(parcelle, culture, statut, maladie, dateAjout);
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

    public String getStatut() { return statut; }
    public void setStatut(String statut) {
        if (statut != null && !statut.equals(STATUT_HEALTHY) && !statut.equals(STATUT_SICK) &&
            !statut.equals(STATUT_HARVESTED) && !statut.equals(STATUT_FAILED)) {
            throw new IllegalArgumentException("Invalid status value");
        }
        this.statut = statut;
    }

    public Maladie getMaladie() { return maladie; }
    public void setMaladie(Maladie maladie) { this.maladie = maladie; }

    public Timestamp getDateAjout() { return dateAjout; }
    public void setDateAjout(Timestamp dateAjout) { this.dateAjout = dateAjout; }

    @Override
    public String toString() {
        return "ParcelleCulture{" +
                "id=" + id +
                ", parcelle=" + parcelle +
                ", culture=" + culture +
                ", statut='" + statut + '\'' +
                ", maladie=" + maladie +
                ", dateAjout=" + dateAjout +
                '}';
    }


}