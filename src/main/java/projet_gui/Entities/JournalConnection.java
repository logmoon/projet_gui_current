package projet_gui.Entities;

import java.sql.Timestamp;

public class JournalConnection {
    private int id;
    private Utilisateur user;
    private Timestamp dateConnexion;
    private String adresseIP;

    public JournalConnection() {
    }

    public JournalConnection(Utilisateur user, Timestamp dateConnexion, String adresseIP) {
        this.user = user;
        this.dateConnexion = dateConnexion;
        this.adresseIP = adresseIP;
    }

    public JournalConnection(int id, Utilisateur user, Timestamp dateConnexion, String adresseIP) {
        this(user, dateConnexion, adresseIP);
        this.id = id;
    }
}
