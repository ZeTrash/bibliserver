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