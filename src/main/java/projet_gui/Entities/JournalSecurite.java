package projet_gui.Entities;

import java.sql.Timestamp;

public class JournalSecurite {
    private int id;
    private Utilisateur utilisateur;
    private String action;
    private Timestamp dateAction;
    private String description;
    private String adresseIP;

    public JournalSecurite() {
    }

    public JournalSecurite(Utilisateur utilisateur, String action, Timestamp dateAction,
                          String description, String adresseIP) {
        this.utilisateur = utilisateur;
        this.action = action;
        this.dateAction = dateAction;
        this.description = description;
        this.adresseIP = adresseIP;
    }

    public JournalSecurite(int id, Utilisateur utilisateur, String action, Timestamp dateAction,
                          String description, String adresseIP) {
        this(utilisateur, action, dateAction, description, adresseIP);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public String getAction() { return action; }
    public void setAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }
        this.action = action;
    }

    public Timestamp getDateAction() { return dateAction; }
    public void setDateAction(Timestamp dateAction) {
        if (dateAction == null) {
            throw new IllegalArgumentException("Action date cannot be null");
        }
        this.dateAction = dateAction;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdresseIP() { return adresseIP; }
    public void setAdresseIP(String adresseIP) { this.adresseIP = adresseIP; }

    @Override
    public String toString() {
        return "JournalSecurite{" +
                "id=" + id +
                ", utilisateur=" + utilisateur +
                ", action='" + action + '\'' +
                ", dateAction=" + dateAction +
                ", description='" + description + '\'' +
                ", adresseIP='" + adresseIP + '\'' +
                '}';
    }
}