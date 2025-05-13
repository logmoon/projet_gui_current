package projet_gui.Services;

import projet_gui.Entities.Meteo;
import projet_gui.Utils.DataSource;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeteoService {
    private Connection conn = DataSource.getInstance().getCon();

    public boolean addservice(Meteo m, Integer parcelleId) throws SQLException {
        String sql = "INSERT INTO meteo (date, temperature, humidite, conditions, parcelleId, alerteMeteo, messageAlerte) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        LocalDateTime now = LocalDateTime.now();

        pstmt.setTimestamp(1, Timestamp.valueOf(now));
        pstmt.setDouble(2, m.getTemperature());
        pstmt.setDouble(3, m.getHumidite());
        pstmt.setString(4, m.getConditions());

        // 👉 Vérifie si parcelleId est null
        if (parcelleId != null) {
            pstmt.setInt(5, parcelleId);
        } else {
            pstmt.setNull(5, java.sql.Types.INTEGER);
        }

        pstmt.setBoolean(6, m.isAlerteMeteo());
        pstmt.setString(7, m.getMessageAlerte());

        int row = pstmt.executeUpdate();
        pstmt.close();

        return row > 0;
    }


    public List<Meteo> getWeatherHistoryFromDb(int nbFois) {
        List<Meteo> historique = new ArrayList<>();

        // Utilisation de DataSource pour obtenir la connexion
        Connection connection = DataSource.getInstance().getCon();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Requête SQL pour récupérer les nbFois dernières entrées de la table meteo
            String sql = "SELECT * FROM meteo ORDER BY date DESC LIMIT ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, nbFois);

            resultSet = statement.executeQuery();

            // Parcours des résultats
            while (resultSet.next()) {
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                double temperature = resultSet.getDouble("temperature");
                double humidite = resultSet.getDouble("humidite");
                String conditions = resultSet.getString("conditions");

                // Création de l'objet meteo et ajout dans la liste
                Meteo m = new Meteo();
                m.setDate(date);
                m.setTemperature(temperature);
                m.setHumidite(humidite);
                m.setConditions(conditions);

                // Ajout dans l'historique
                historique.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return historique;
    }



    public Meteo recupererMeteo(String ville, String cleApi) {
        try {
            String villeEncodee = URLEncoder.encode(ville, StandardCharsets.UTF_8);
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    villeEncodee + "&appid=" + cleApi + "&units=metric&lang=fr";

            URL url = new URL(urlString);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
            StringBuilder contenu = new StringBuilder();
            String ligne;
            while ((ligne = in.readLine()) != null) {
                contenu.append(ligne);
            }
            in.close();

            JSONObject objetJson = new JSONObject(contenu.toString());

            Meteo donneesMeteo = new Meteo();
            donneesMeteo.setId(0);
            donneesMeteo.setDate(LocalDateTime.now());
            donneesMeteo.setTemperature(objetJson.getJSONObject("main").getDouble("temp"));
            donneesMeteo.setHumidite(objetJson.getJSONObject("main").getDouble("humidity"));
            donneesMeteo.setConditions(objetJson.getJSONArray("weather").getJSONObject(0).getString("description"));

            return donneesMeteo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
