// src/main/java/projet_gui/Services/WeatherService.java
package projet_gui.Services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import projet_gui.Entities.WeatherData;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private static final String API_KEY = "054ad20b6775045304d020b3c0da6c48";
    private static final String BASE_URL = "http://api.weatherstack.com/current?access_key=" + API_KEY + "&query=";

    public static WeatherData getWeatherData(String city) {
        try {
            URL url = new URL(BASE_URL + city);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            JsonObject current = jsonObject.getAsJsonObject("current");
            String description = current.getAsJsonArray("weather_descriptions").get(0).getAsString();
            double temperature = current.get("temperature").getAsDouble();
            double humidity = current.get("humidity").getAsDouble();
            double windSpeed = current.get("wind_speed").getAsDouble();

            return new WeatherData(description, temperature, humidity, windSpeed);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}