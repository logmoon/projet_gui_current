package projet_gui.Entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String pwdHash;
    private Role role;
    private boolean actif;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email, String pwd, Role role, boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        setEmail(email);
        setPwd(pwd);
        this.role = role;
        this.actif = actif;
    }

    public Utilisateur(int id, String nom, String prenom, String email, String pwd, Role role, boolean actif) {
        this(nom, prenom, email, pwd, role, actif);
        this.id = id;
    }

    // Getters and setters
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public void setNom(String nom) { this.nom = nom; }
    public String getNom() { return nom; }

    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getPrenom() { return prenom; }

    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
    public String getEmail() { return email; }

    public void setHashedPwd(String pwdHash) {
        if (pwdHash == null || pwdHash.length() < 64) {
            throw new IllegalArgumentException("Invalid hashed password format");
        }
        this.pwdHash = pwdHash;
    }
    public void setPwd(String pwd) {
        if (pwd == null || pwd.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.pwdHash = hashPassword(pwd);
    }
    public String getPwdHash() {
        return pwdHash;
    }

    public void setRole(Role role) { this.role = role; }
    public Role getRole() { return role; }
    public boolean isAdmin() {return role == Role.ADMIN; }

    public void setActif(Boolean actif) { this.actif = actif; }
    public Boolean getActif() { return actif; }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }

    // Password hashing
    private String hashPassword(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pwd.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found");
        }
    }
}

