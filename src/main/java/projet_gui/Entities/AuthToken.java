package projet_gui.Entities;

import java.sql.Timestamp;

public class AuthToken {
    private int id;
    private Utilisateur user;
    private String token;
    private Timestamp dateCreation;
    private Timestamp dateExpiration;
    private boolean actif;

    public AuthToken() {
    }

    public AuthToken(Utilisateur user, String token, Timestamp dateCreation, Timestamp dateExpiration, boolean actif) {
        this.user = user;
        this.token = token;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.actif = actif;
    }

    public AuthToken(int id, Utilisateur user, String token, Timestamp dateCreation, Timestamp dateExpiration, boolean actif) {
        this(user, token, dateCreation, dateExpiration, actif);
        this.id = id;
    }

    // Getters and setters for the token
    public String getToken()             { return token; }
    public void   setToken(String token) { this.token = token; }

    public Utilisateur getUser() { return user; }
    public void setUser(Utilisateur user) { this.user = user; }

    @Override
    public String toString() {
        return "AuthToken{" +
                "id=" + id +
                ", user='\n\t" + user.toString() + '\'' +
                ", token='" + token + '\'' +
                ", dateCreation='" + dateCreation.toString() + '\'' +
                ", dateExpiration='" + dateExpiration.toString() + '\'' +
                ", actif=" + actif +
                '}';
    }
}
