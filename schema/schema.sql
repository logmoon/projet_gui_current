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
    imagePath VARCHAR(255),  -- Added field for storing image path
    description TEXT
);

-- Disease table
CREATE TABLE Maladie (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    traitement VARCHAR(255) NOT NULL,
    symptomes TEXT  -- Added symptoms field
);

CREATE TABLE ParcelleCulture (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- Added PK for easier referencing
    parcelleId INT NOT NULL,
    cultureId INT NOT NULL,
    maladieId INT,
    statut ENUM('HEALTHY', 'SICK', 'HARVESTED', 'FAILED') DEFAULT 'HEALTHY',  -- Added status field
    dateAjout TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE KEY unique_parcelle_culture (parcelleId, cultureId),  -- Changed to UNIQUE constraint
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id) ON DELETE CASCADE,
    FOREIGN KEY (cultureId) REFERENCES Culture(id) ON DELETE CASCADE,
    FOREIGN KEY (maladieId) REFERENCES Maladie(id) ON DELETE SET NULL
);

-- Crop report table
CREATE TABLE RapportCulture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleCultureId INT NOT NULL,  -- Changed to reference the junction table
    etat VARCHAR(50) NOT NULL,
    notes TEXT,  -- Added field for additional notes
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (parcelleCultureId) REFERENCES ParcelleCulture(id) ON DELETE CASCADE
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
    assigneeId INT,  -- Added field to track who's assigned to the task
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id),
    FOREIGN KEY (cultureId) REFERENCES Culture(id),
    FOREIGN KEY (assigneeId) REFERENCES Utilisateur(id) ON DELETE SET NULL
);

-- Reminders
CREATE TABLE Rappel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    dateRappel TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estComplete BOOLEAN DEFAULT FALSE,  -- Added field to mark reminder as complete
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);

-- Weather data table with field-specific tracking
CREATE TABLE Meteo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    temperature DOUBLE NOT NULL,
    humidite DOUBLE NOT NULL,
    conditions VARCHAR(100) NOT NULL,
    parcelleId INT NOT NULL,
    alerteMeteo BOOLEAN DEFAULT FALSE,  -- Added field for weather alerts
    messageAlerte VARCHAR(255),  -- Added field for alert messages
    FOREIGN KEY (parcelleId) REFERENCES Parcelle(id)
);

-- Crop History table
CREATE TABLE HistoriqueCulture (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parcelleId INT NOT NULL,
    cultureId INT NOT NULL,
    dateDebut TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    dateFin TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rendement DOUBLE,
    notes TEXT,  -- Added field for notes on crop cycle
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
    liee_objectId INT,  -- Added field to link to relevant object (parcelle, task, etc.)
    liee_objectType VARCHAR(50),  -- Type of the linked object
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id)
);

-- Security related logs
CREATE TABLE JournalSecurite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    utilisateurId INT,
    action VARCHAR(100) NOT NULL,
    dateAction TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description TEXT,
    adresseIP VARCHAR(45),  -- Added IP address for better tracking
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id)
);

-- New table for application settings
CREATE TABLE ParametresApplication (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cle VARCHAR(100) UNIQUE NOT NULL,
    valeur TEXT NOT NULL,
    description VARCHAR(255),
    modifiable BOOLEAN DEFAULT TRUE
);

-- Initialize some basic settings
INSERT INTO ParametresApplication (cle, valeur, description) VALUES
('APP_NAME', 'AgroAssist', 'Name of the application'),
('DEFAULT_LANGUAGE', 'fr', 'Default application language'),
('PASSWORD_RESET_EXPIRY', '24', 'Hours until password reset links expire');

-- Indices for better performance
CREATE INDEX idx_parcelle_proprietaire ON Parcelle(proprietaireId);
CREATE INDEX idx_parcelle_culture_parcelle ON ParcelleCulture(parcelleId);
CREATE INDEX idx_parcelle_culture_culture ON ParcelleCulture(cultureId);
CREATE INDEX idx_rapportculture_parcelle_culture ON RapportCulture(parcelleCultureId);
CREATE INDEX idx_meteo_parcelle ON Meteo(parcelleId);
CREATE INDEX idx_notification_utilisateur ON Notification(utilisateurId);
CREATE INDEX idx_usertoken_token ON AuthToken(token);
CREATE INDEX idx_tache_parcelle ON Tache(parcelleId);
CREATE INDEX idx_tache_assignee ON Tache(assigneeId);