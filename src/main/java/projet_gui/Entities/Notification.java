package projet_gui.Entities;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private Utilisateur utilisateur;
    private String message;
    private Timestamp dateCreation;
    private Boolean lue;
    private String type;
    private Integer lieeObjectId;
    private String lieeObjectType;

    public Notification() {
    }

    public Notification(Utilisateur utilisateur, String message, Timestamp dateCreation,
                       Boolean lue, String type, Integer lieeObjectId, String lieeObjectType) {
        this.utilisateur = utilisateur;
        this.message = message;
        this.dateCreation = dateCreation;
        this.lue = lue;
        this.type = type;
        this.lieeObjectId = lieeObjectId;
        this.lieeObjectType = lieeObjectType;
    }

    public Notification(int id, Utilisateur utilisateur, String message, Timestamp dateCreation,
                       Boolean lue, String type, Integer lieeObjectId, String lieeObjectType) {
        this(utilisateur, message, dateCreation, lue, type, lieeObjectId, lieeObjectType);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.utilisateur = utilisateur;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        this.message = message;
    }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) {
        if (dateCreation == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        this.dateCreation = dateCreation;
    }

    public Boolean getLue() { return lue; }
    public void setLue(Boolean lue) {
        this.lue = lue != null ? lue : false;
    }

    public String getType() { return type; }
    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification type cannot be empty");
        }
        this.type = type;
    }

    public Integer getLieeObjectId() { return lieeObjectId; }
    public void setLieeObjectId(Integer lieeObjectId) { this.lieeObjectId = lieeObjectId; }

    public String getLieeObjectType() { return lieeObjectType; }
    public void setLieeObjectType(String lieeObjectType) { this.lieeObjectType = lieeObjectType; }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", utilisateur=" + utilisateur +
                ", message='" + message + '\'' +
                ", dateCreation=" + dateCreation +
                ", lue=" + lue +
                ", type='" + type + '\'' +
                ", lieeObjectId=" + lieeObjectId +
                ", lieeObjectType='" + lieeObjectType + '\'' +
                '}';
    }
}