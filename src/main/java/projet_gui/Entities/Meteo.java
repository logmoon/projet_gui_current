package projet_gui.Entities;

import java.time.LocalDateTime;

public class Meteo {
    private int id;
    private LocalDateTime date;
    private Double temperature;
    private Double humidite;
    private String conditions;
    private Parcelle parcelle;
    private Boolean alerteMeteo;
    private String messageAlerte;

    public Meteo() {
    }

    public Meteo(LocalDateTime date, Double temperature, Double humidite, String conditions,
                Parcelle parcelle, Boolean alerteMeteo, String messageAlerte) {
        this.date = date;
        this.temperature = temperature;
        this.humidite = humidite;
        this.conditions = conditions;
        this.parcelle = parcelle;
        this.alerteMeteo = alerteMeteo;
        this.messageAlerte = messageAlerte;
    }

    public Meteo(int id, LocalDateTime date, Double temperature, Double humidite, String conditions,
                Parcelle parcelle, Boolean alerteMeteo, String messageAlerte) {
        this(date, temperature, humidite, conditions, parcelle, alerteMeteo, messageAlerte);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) {
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

    public Boolean isAlerteMeteo() { return alerteMeteo == null ? false : alerteMeteo; }
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
                ", alerteMeteo=" + alerteMeteo +
                ", messageAlerte='" + messageAlerte + '\'' +
                '}';
    }
}