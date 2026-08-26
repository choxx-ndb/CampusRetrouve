DROP DATABASE IF EXISTS campus_retrouve;
CREATE DATABASE campus_retrouve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_retrouve;

CREATE TABLE utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    motdepass VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE objets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description LONGTEXT,
    type ENUM('perdue', 'trouve') NOT NULL,
    localisation VARCHAR(255),
    image_path VARCHAR(500),
    status ENUM('disponible', 'reclame', 'restitue') DEFAULT 'disponible',
    proprietaire_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_objets_utilisateur FOREIGN KEY (proprietaire_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE TABLE reclamations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    objet_id INT NOT NULL,
    utilisateur_id INT NOT NULL,
    message TEXT NOT NULL,
    status ENUM('en_attente', 'approuve', 'rejete') DEFAULT 'en_attente',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reclamations_objet FOREIGN KEY (objet_id)
        REFERENCES objets(id) ON DELETE CASCADE,
    CONSTRAINT fk_reclamations_utilisateur FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE TABLE messages_reclamation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reclamation_id INT NOT NULL,
    expediteur_id INT NOT NULL,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_messages_reclamation FOREIGN KEY (reclamation_id)
        REFERENCES reclamations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_expediteur FOREIGN KEY (expediteur_id)
        REFERENCES utilisateurs(id) ON DELETE CASCADE
);
-- DONNEES DE DEMONSTRATION LOCALES UNIQUEMENT.
-- Ne jamais reutiliser ces identifiants en production.
INSERT INTO utilisateurs (nom, email, motdepass, role) VALUES
('Admin UMI', 'admin@umi.ac.ma', '$2a$12$B/JcqqbUaKOsx2bZ/xQRxOA5VlQ2N2XFcg5nvx.3w9W0oLSq3Hyvq', 'admin'),
('Super Admin', 'superadmin@umi.ac.ma', '$2a$12$YXd9OLxZS/MaLO2J3hEsUePHfyaJzJr.85OouGyJXKIAfvqGT9xjW', 'admin'),
('Ahmed', 'ahmed@umi.ac.ma', '$2a$12$MM/GlEBhjnr2PIxl9QVQ8.qBMD6YuETz8nVNEgpFoewuccwnvxh.G', 'user'),
('Youssef', 'youssef@umi.ac.ma', '$2a$12$rUzQHeC1iAsp3eqH.C3bQuevIKvvcTA0QHQSeEpTgvTzasXptV.SS', 'user'),
('Sara', 'sara@umi.ac.ma', '$2a$12$AoMDxR83GUXcdOwiqUqc3eg0tuZMjeygHF3muoniVQz76AE1cWPGq', 'user'),
('Meryem', 'meryem@umi.ac.ma', '$2a$12$VmavsSuAUOC8dUsZG9AEC.T0wy4WFJ4K/fSLRqyX/hEf.d.VMutJq', 'user'),
('Omar', 'omar@umi.ac.ma', '$2a$12$9/w.h/VBm/2ZspSFtweYi.YLTYNcMxF81TkPY.y4q4K7GGNMitqEK', 'user');

INSERT INTO objets (titre, description, type, localisation, image_path, status, proprietaire_id) VALUES
('iPhone 12 perdu', 'Coque noire, �cran l�g�rement ray�, perdu apr�s le cours de r�seau.', 'perdue', 'B�timent A', 'uploads/default-phone.jpg', 'reclame', 3),
('Carte �tudiante trouv�e', 'Carte au nom de Sara, trouv�e pr�s de la biblioth�que.', 'trouve', 'Biblioth�que centrale', 'uploads/default-card.jpg', 'disponible', 4),
('Cl�s avec porte-cl� orange', 'Trousseau de trois cl�s avec porte-cl� UMI orange.', 'trouve', 'Caf�t�ria', 'uploads/default-keys.jpg', 'disponible', 5),
('Sac � dos noir perdu', 'Sac Eastpak noir avec cahiers et chargeur.', 'perdue', 'Amphi 2', 'uploads/default-bag.jpg', 'disponible', 6),
('Casque Bluetooth trouv�', 'Casque noir trouv� dans la salle informatique.', 'trouve', 'Salle Info 3', 'uploads/default-headphones.jpg', 'restitue', 7);

INSERT INTO reclamations (objet_id, utilisateur_id, message, status) VALUES
(1, 4, 'Je pense que c''est mon t�l�phone. La coque est noire avec une marque pr�s de la cam�ra.', 'en_attente'),
(5, 3, 'Merci, ce casque correspond au mien. Je peux confirmer le mod�le.', 'approuve');

INSERT INTO messages_reclamation (reclamation_id, expediteur_id, contenu) VALUES
(1, 4, 'Bonjour, je peux donner plus de d�tails sur le t�l�phone.'),
(1, 3, 'Bonjour, peux-tu pr�ciser le fond d''�cran?'),
(1, 4, 'Fond d''�cran bleu avec le logo de mon club.'),
(2, 7, 'Je l''ai d�pos� au bureau des objets trouv�s.'),
(2, 3, 'Merci beaucoup, je passe le r�cup�rer cet apr�s-midi.');
