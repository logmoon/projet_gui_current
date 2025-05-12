-- Role enumeration table
CREATE TABLE Role (
    id VARCHAR(20) PRIMARY KEY,
    description VARCHAR(100)
);

-- Initialize roles
INSERT INTO Role (id, description) VALUES 
('ADMIN', 'Administrator with full access'),
('AGRICULTEUR', 'Farmer user');

-- User table
CREATE TABLE Utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    motDePasse VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (role) REFERENCES Role(id)
);

CREATE TABLE AuthToken (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateExpiration TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estActif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    UNIQUE (token)
);

-- Password reset request table
CREATE TABLE ReinitialisationMotDePasse (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    codeVerification VARCHAR(100) NOT NULL,
    dateExpiration TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    codeValide BOOLEAN DEFAULT TRUE
);

-- Connection journal (login history)
CREATE TABLE JournalConnexion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT,
    dateConnexion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    adresseIP VARCHAR(45) NOT NULL,
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id) ON DELETE SET NULL
);

-- Location table
CREATE TABLE Localisation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nomCity VARCHAR(150) NOT NULL
);

-- Plot/Field table
CREATE TABLE Parcelle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    superficie DOUBLE NOT NULL,
    cultureActuelle VARCHAR(100),
    localisationId INT NOT NULL,
    proprietaireId INT NOT NULL,
    FOREIGN KEY (localisationId) REFERENCES Localisation(id),
    FOREIGN KEY (proprietaireId) REFERENCES Utilisateur(id)
);

-- Crop type table
CREATE TABLE Culture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    besoinEau DOUBLE NOT NULL,
    besoinNutriments DOUBLE NOT NULL
);

-- Crop report table
CREATE TABLE RapportCulture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cultureId INT NOT NULL,
    parcelleId INT NOT NULL,
    etat VARCHAR(50) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (cultureId) REFERENCES Culture(id),
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);

-- Disease table
CREATE TABLE Maladie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    traitement VARCHAR(255) NOT NULL
);

-- Task management
CREATE TABLE Tache (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    statut ENUM('PENDING', 'DONE') DEFAULT 'PENDING',
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateEcheance TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);

-- Reminders
CREATE TABLE Rappel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    dateRappel TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);

-- Weather data table (optional, but simple)
CREATE TABLE Meteo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    temperature DOUBLE NOT NULL,
    humidite DOUBLE NOT NULL,
    conditions VARCHAR(100) NOT NULL,
    localisationId INT NOT NULL,
    FOREIGN KEY (localisationId) REFERENCES Localisation(id)
);

-- Crop History table
CREATE TABLE HistoriqueCulture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    cultureId INT NOT NULL,
    dateDebut TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    dateFin TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rendement DOUBLE,
    UNIQUE KEY unique_history (parcelleId, cultureId, dateDebut),
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id),
    FOREIGN KEY (cultureId) REFERENCES Culture(id)
);

-- Notifications table for system alerts
CREATE TABLE Notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT NOT NULL,
    message TEXT NOT NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lue BOOLEAN DEFAULT FALSE,
    type VARCHAR(50) NOT NULL,
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id)
);

-- Security related logs
CREATE TABLE JournalSecurite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT,
    action VARCHAR(100) NOT NULL,
    dateAction TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description TEXT,
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id)
);

-- Indices for better performance
CREATE INDEX idx_parcelle_proprietaire ON Parcelle(proprietaireId);
CREATE INDEX idx_rapportculture_parcelle ON RapportCulture(parcelleId);
CREATE INDEX idx_meteo_localisation ON Meteo(localisationId);
CREATE INDEX idx_notification_utilisateur ON Notification(utilisateurId);
CREATE INDEX idx_usertoken_token ON AuthToken(token);