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
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role) REFERENCES Role(id)
);

-- Create a normal user (123456 is the password)
INSERT INTO Utilisateur (nom, prenom, email, motDePasse, role) VALUES 
('Amen', 'BA', 'tw@tw.tw', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'AGRICULTEUR');

-- Create an admin user (admin1 is the password)
INSERT INTO Utilisateur (nom, prenom, email, motDePasse, role) VALUES 
('Admin', 'User', 'admin@admin.com', '25f43b1486ad95a1398e3eeb3d83bc4010015fcc9bedb35b432e00298d5021f7', 'ADMIN');

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

-- Plot/Field table (added area and image fields)
CREATE TABLE Parcelle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    longuerMetre DOUBLE NOT NULL,
    largeurMetre DOUBLE NOT NULL,
    localisationCity VARCHAR(200) NOT NULL, -- La localisation de la parcelle
    proprietaireId INT NOT NULL,
    imagePath VARCHAR(255),  -- Added field for storing image path
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proprietaireId) REFERENCES Utilisateur(id)
);

-- Crop type table (added image field)
CREATE TABLE Culture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    besoinEau DOUBLE NOT NULL,
    besoinNutriments DOUBLE NOT NULL,
    statut ENUM('HEALTHY', 'SICK', 'HARVESTED', 'FAILED') DEFAULT 'HEALTHY',
    imagePath VARCHAR(255),  -- Added field for storing image path
    parcelleId INT NOT NULL,
    description TEXT,
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id) ON DELETE CASCADE
);

-- Disease table
CREATE TABLE Maladie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    traitement VARCHAR(255) NOT NULL,
    symptomes TEXT,
    cultureId INT,
    FOREIGN KEY (cultureId) REFERENCES Culture(id) ON DELETE CASCADE
);

-- Task management
CREATE TABLE Tache (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    statut ENUM('PENDING', 'IN_PROGRESS', 'DONE', 'CANCELLED') DEFAULT 'PENDING',
    priorite ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateEcheance TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cultureId INT,
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id),
    FOREIGN KEY (cultureId) REFERENCES Culture(id),
);

-- Weather data table with field-specific tracking
CREATE TABLE Meteo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    temperature DOUBLE NOT NULL,
    humidite DOUBLE NOT NULL,
    conditions VARCHAR(100) NOT NULL,
    parcelleId INT,
    alerteMeteo BOOLEAN DEFAULT FALSE,  -- Added field for weather alerts
    messageAlerte VARCHAR(255),  -- Added field for alert messages
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);


-- Notifications table for system alerts
CREATE TABLE Notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT NOT NULL,
    message TEXT NOT NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lue BOOLEAN DEFAULT FALSE,
    type VARCHAR(50) NOT NULL,
    liee_objectId INT,  -- Added field to link to relevant object (parcelle, task, etc.)
    liee_objectType VARCHAR(50),  -- Type of the linked object
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id)
);

-- User Authentication Indices
CREATE INDEX idx_utilisateur_email ON Utilisateur(email);
CREATE INDEX idx_utilisateur_role ON Utilisateur(role);
CREATE INDEX idx_utilisateur_actif ON Utilisateur(actif);

-- Auth Token Indices (for authentication/session management)
CREATE INDEX idx_authtoken_token ON AuthToken(token);
CREATE INDEX idx_authtoken_userid_expiration ON AuthToken(utilisateurId, dateExpiration, estActif);

-- Password Reset Indices
CREATE INDEX idx_reinitialisation_email ON ReinitialisationMotDePasse(email, codeValide);
CREATE INDEX idx_reinitialisation_expiration ON ReinitialisationMotDePasse(dateExpiration);

-- Connection Journal Indices (for security analytics)
CREATE INDEX idx_journalconnexion_userid ON JournalConnexion(utilisateurId);
CREATE INDEX idx_journalconnexion_date ON JournalConnexion(dateConnexion);
CREATE INDEX idx_journalconnexion_ip ON JournalConnexion(adresseIP);

-- Parcelle (Plot/Field) Indices 
CREATE INDEX idx_parcelle_proprietaire ON Parcelle(proprietaireId);
CREATE INDEX idx_parcelle_localisation ON Parcelle(localisationCity);

-- Culture (Crop) Indices
CREATE INDEX idx_culture_parcelle ON Culture(parcelleId);
CREATE INDEX idx_culture_statut ON Culture(statut);
CREATE INDEX idx_culture_nom ON Culture(nom);

-- Maladie (Disease) Indices
CREATE INDEX idx_maladie_culture ON Maladie(cultureId);
CREATE INDEX idx_maladie_nom ON Maladie(nom);

-- Tache (Task) Indices
CREATE INDEX idx_tache_parcelle ON Tache(parcelleId);
CREATE INDEX idx_tache_culture ON Tache(cultureId);
CREATE INDEX idx_tache_assignee ON Tache(assigneeId);
CREATE INDEX idx_tache_statut ON Tache(statut);
CREATE INDEX idx_tache_date_echeance ON Tache(dateEcheance);
CREATE INDEX idx_tache_priorite_statut ON Tache(priorite, statut);

-- Meteo (Weather) Indices
CREATE INDEX idx_meteo_parcelle ON Meteo(parcelleId);
CREATE INDEX idx_meteo_date ON Meteo(date);
CREATE INDEX idx_meteo_alerte ON Meteo(alerteMeteo);

-- Notification Indices
CREATE INDEX idx_notification_utilisateur ON Notification(utilisateurId);
CREATE INDEX idx_notification_lue ON Notification(lue);
CREATE INDEX idx_notification_date ON Notification(dateCreation);
CREATE INDEX idx_notification_type ON Notification(type);
CREATE INDEX idx_notification_liee_object ON Notification(liee_objectType, liee_objectId);