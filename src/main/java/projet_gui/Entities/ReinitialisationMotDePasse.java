package projet_gui.Entities;

import java.sql.Timestamp;

public class ReinitialisationMotDePasse {
    private int id;
    private String email;
    private String codeVerification;
    private Timestamp dateExpiration;
    private Boolean codeValide;

    public ReinitialisationMotDePasse() {
    }

    public ReinitialisationMotDePasse(String email, String codeVerification, Timestamp dateExpiration, Boolean codeValide) {
        this.email = email;
        this.codeVerification = codeVerification;
        this.dateExpiration = dateExpiration;
        this.codeValide = codeValide;
    }

    public ReinitialisationMotDePasse(int id, String email, String codeVerification, Timestamp dateExpiration, Boolean codeValide) {
        this(email, codeVerification, dateExpiration, codeValide);
        this.id = id;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getCodeVerification() { return codeVerification; }
    public void setCodeVerification(String codeVerification) {
        if (codeVerification == null || codeVerification.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification code cannot be empty");
        }
        this.codeVerification = codeVerification;
    }

    public Timestamp getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Timestamp dateExpiration) {
        if (dateExpiration == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        this.dateExpiration = dateExpiration;
    }

    public Boolean getCodeValide() { return codeValide; }
    public void setCodeValide(Boolean codeValide) {
        this.codeValide = codeValide != null ? codeValide : false;
    }

    @Override
    public String toString() {
        return "ReinitialisationMotDePasse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", codeVerification='" + codeVerification + '\'' +
                ", dateExpiration=" + dateExpiration +
                ", codeValide=" + codeValide +
                '}';
    }
}