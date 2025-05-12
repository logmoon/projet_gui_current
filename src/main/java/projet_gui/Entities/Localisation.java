package projet_gui.Entities;

public class Localisation {
    private int id;
    private String nomCity;
    private Double latitude;
    private Double longitude;

    public Localisation() {
    }

    public Localisation(String nomCity) {
        this.nomCity = nomCity;
    }

    public Localisation(String nomCity, Double latitude, Double longitude) {
        this.nomCity = nomCity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localisation(int id, String nomCity, Double latitude, Double longitude) {
        this(nomCity, latitude, longitude);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomCity() { return nomCity; }
    public void setNomCity(String nomCity) { 
        if (nomCity == null || nomCity.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        this.nomCity = nomCity; 
    }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { 
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        this.latitude = latitude; 
    }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { 
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        this.longitude = longitude; 
    }

    @Override
    public String toString() {
        return "Localisation{" +
                "id=" + id +
                ", nomCity='" + nomCity + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}