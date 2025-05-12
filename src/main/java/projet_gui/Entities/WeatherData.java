package projet_gui.Entities;

public class WeatherData {
    private String description;
    private double temperature;
    private double humidity;
    private double windSpeed;

    public WeatherData(String description, double temperature, double humidity, double windSpeed) {
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getDescription() { return description; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
}