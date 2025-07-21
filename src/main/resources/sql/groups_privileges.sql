-- Tables pour la gestion des groupes et privilèges
CREATE TABLE IF NOT EXISTS user_groups (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS privileges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_privileges (
    group_id INT,
    privilege_id INT,
    FOREIGN KEY (group_id) REFERENCES user_groups(id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, privilege_id)
);

-- Ajout de la colonne group_id à la table users si elle n'existe pas déjà
ALTER TABLE users ADD COLUMN IF NOT EXISTS group_id INT,
ADD FOREIGN KEY IF NOT EXISTS (group_id) REFERENCES user_groups(id);

-- Ajout de la colonne genre à la table books si elle n'existe pas déjà
ALTER TABLE books ADD COLUMN IF NOT EXISTS genre VARCHAR(100);

-- Insertion des privilèges prédéfinis
INSERT INTO privileges (name, description) VALUES
    ('MANAGE_BOOKS', 'Permet de gérer les livres (ajout, modification, suppression)'),
    ('VIEW_BOOKS', 'Permet de consulter la liste des livres'),
    ('MANAGE_LOANS', 'Permet de gérer les emprunts (création, retour)'),
    ('VIEW_LOANS', 'Permet de consulter la liste des emprunts'),
    ('MANAGE_USERS', 'Permet de gérer les utilisateurs (ajout, modification, suppression)'),
    ('VIEW_USERS', 'Permet de consulter la liste des utilisateurs'),
    ('MANAGE_GROUPS', 'Permet de gérer les groupes et leurs privilèges'),
    ('VIEW_GROUPS', 'Permet de consulter la liste des groupes'),
    ('VIEW_STATISTICS', 'Permet de consulter les statistiques du tableau de bord')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Création des groupes par défaut
INSERT INTO user_groups (name, description) VALUES
    ('Administrateurs', 'Accès complet à toutes les fonctionnalités'),
    ('Bibliothécaires', 'Gestion des livres et des emprunts'),
    ('Lecteurs', 'Consultation des livres uniquement')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Attribution des privilèges aux groupes par défaut
-- Administrateurs (tous les privilèges)
INSERT INTO group_privileges (group_id, privilege_id)
SELECT 
    (SELECT id FROM user_groups WHERE name = 'Administrateurs'),
    id
FROM privileges
ON DUPLICATE KEY UPDATE group_id = group_id;

-- Bibliothécaires
INSERT INTO group_privileges (group_id, privilege_id)
SELECT 
    (SELECT id FROM user_groups WHERE name = 'Bibliothécaires'),
    id
FROM privileges 
WHERE name IN ('MANAGE_BOOKS', 'VIEW_BOOKS', 'MANAGE_LOANS', 'VIEW_LOANS', 'VIEW_STATISTICS')
ON DUPLICATE KEY UPDATE group_id = group_id;

-- Lecteurs
INSERT INTO group_privileges (group_id, privilege_id)
SELECT 
    (SELECT id FROM user_groups WHERE name = 'Lecteurs'),
    id
FROM privileges 
WHERE name IN ('VIEW_BOOKS')
ON DUPLICATE KEY UPDATE group_id = group_id;

-- Attribution du groupe Administrateurs à l'utilisateur 'admin' s'il existe
UPDATE users 
SET group_id = (SELECT id FROM user_groups WHERE name = 'Administrateurs')
WHERE username = 'admin'; 

-- Table des informations spécifiques aux mémoires
CREATE TABLE IF NOT EXISTS memoire_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL UNIQUE,
    university VARCHAR(255),
    supervisor VARCHAR(255),
    year INT,
    subject VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Table des médias (CD, DVD, etc.)
CREATE TABLE IF NOT EXISTS media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL, -- 'CD', 'DVD', etc.
    title VARCHAR(255),
    description TEXT,
    book_id INT, -- NULL si le média est indépendant
    is_independent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
);

-- Privilèges pour la gestion des médias
INSERT INTO privileges (name, description) VALUES
    ('MANAGE_MEDIA', 'Permet de gérer les médias (ajout, modification, suppression)'),
    ('VIEW_MEDIA', 'Permet de consulter la liste des médias')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Attribution des privilèges médias aux groupes
INSERT INTO group_privileges (group_id, privilege_id)
SELECT (SELECT id FROM user_groups WHERE name = 'Administrateurs'), id
FROM privileges WHERE name IN ('MANAGE_MEDIA', 'VIEW_MEDIA')
ON DUPLICATE KEY UPDATE group_id = group_id;

INSERT INTO group_privileges (group_id, privilege_id)
SELECT (SELECT id FROM user_groups WHERE name = 'Bibliothécaires'), id
FROM privileges WHERE name IN ('MANAGE_MEDIA', 'VIEW_MEDIA')
ON DUPLICATE KEY UPDATE group_id = group_id; 