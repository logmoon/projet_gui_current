package projet_gui.Entities;

import java.sql.Timestamp;

public class Meteo {
    private int id;
    private Timestamp date;
    private Double temperature;
    private Double humidite;
    private String conditions;
    private Parcelle parcelle;
    private Localisation localisation;
    private Boolean alerteMeteo;
    private String messageAlerte;

    public Meteo() {
    }

    public Meteo(Timestamp date, Double temperature, Double humidite, String conditions,
                Parcelle parcelle, Localisation localisation, Boolean alerteMeteo, String messageAlerte) {
        this.date = date;
        this.temperature = temperature;
        this.humidite = humidite;
        this.conditions = conditions;
        this.parcelle = parcelle;
        this.localisation = localisation;
        this.alerteMeteo = alerteMeteo;
        this.messageAlerte = messageAlerte;
    }

    public Meteo(int id, Timestamp date, Double temperature, Double humidite, String conditions,
                Parcelle parcelle, Localisation localisation, Boolean alerteMeteo, String messageAlerte) {
        this(date, temperature, humidite, conditions, parcelle, localisation, alerteMeteo, messageAlerte);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) {
        if (temperature == null) {
            throw new IllegalArgumentException("Temperature cannot be null");
        }
        this.temperature = temperature;
    }

    public Double getHumidite() { return humidite; }
    public void setHumidite(Double humidite) {
        if (humidite == null || humidite < 0 || humidite > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100");
        }
        this.humidite = humidite;
    }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) {
        if (conditions == null || conditions.trim().isEmpty()) {
            throw new IllegalArgumentException("Weather conditions cannot be empty");
        }
        this.conditions = conditions;
    }

    public Parcelle getParcelle() { return parcelle; }
    public void setParcelle(Parcelle parcelle) {
        if (parcelle == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        this.parcelle = parcelle;
    }

    public Localisation getLocalisation() { return localisation; }
    public void setLocalisation(Localisation localisation) {
        if (localisation == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.localisation = localisation;
    }

    public Boolean getAlerteMeteo() { return alerteMeteo; }
    public void setAlerteMeteo(Boolean alerteMeteo) {
        this.alerteMeteo = alerteMeteo != null ? alerteMeteo : false;
    }

    public String getMessageAlerte() { return messageAlerte; }
    public void setMessageAlerte(String messageAlerte) { this.messageAlerte = messageAlerte; }

    @Override
    public String toString() {
        return "Meteo{" +
                "id=" + id +
                ", date=" + date +
                ", temperature=" + temperature +
                ", humidite=" + humidite +
                ", conditions='" + conditions + '\'' +
                ", parcelle=" + parcelle +
                ", localisation=" + localisation +
                ", alerteMeteo=" + alerteMeteo +
                ", messageAlerte='" + messageAlerte + '\'' +
                '}';
    }
}