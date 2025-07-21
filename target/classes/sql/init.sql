-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bibliserver
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE bibliserver;

-- Table des groupes
CREATE TABLE IF NOT EXISTS `groups` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des permissions
CREATE TABLE IF NOT EXISTS `permissions` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` TEXT,
    `resource` VARCHAR(50) NOT NULL,
    `action` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `resource_action_unique` (`resource`, `action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table de liaison groupe-permission
CREATE TABLE IF NOT EXISTS `group_permissions` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `group_permission_unique` (`group_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    group_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES `groups`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des livres
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) UNIQUE,
    publication_year INT,
    publisher VARCHAR(100),
    quantity INT DEFAULT 1,
    available_quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    genre VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des emprunts
CREATE TABLE IF NOT EXISTS loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    user_id INT,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des informations spécifiques aux mémoires
CREATE TABLE IF NOT EXISTS memoire_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL UNIQUE,
    university VARCHAR(255),
    supervisor VARCHAR(255),
    year INT,
    subject VARCHAR(255),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Permissions pour la gestion des médias
INSERT INTO `permissions` (`name`, `description`, `resource`, `action`) VALUES
('MEDIA_CREATE', 'Créer un média', 'MEDIA', 'CREATE'),
('MEDIA_READ', 'Consulter les médias', 'MEDIA', 'READ'),
('MEDIA_UPDATE', 'Modifier un média', 'MEDIA', 'UPDATE'),
('MEDIA_DELETE', 'Supprimer un média', 'MEDIA', 'DELETE')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insertion des groupes par défaut
INSERT IGNORE INTO `groups` (`name`, `description`) VALUES
('Administrateurs', 'Accès complet à toutes les fonctionnalités'),
('Bibliothécaires', 'Gestion des livres et des emprunts'),
('Lecteurs', 'Consultation et emprunt de livres');

-- Insertion des permissions de base
INSERT INTO `permissions` (`name`, `description`, `resource`, `action`) VALUES
-- Permissions sur les livres
('BOOK_CREATE', 'Créer un nouveau livre', 'BOOK', 'CREATE'),
('BOOK_READ', 'Consulter les livres', 'BOOK', 'READ'),
('BOOK_UPDATE', 'Modifier un livre', 'BOOK', 'UPDATE'),
('BOOK_DELETE', 'Supprimer un livre', 'BOOK', 'DELETE'),
-- Permissions sur les emprunts
('LOAN_CREATE', 'Créer un emprunt', 'LOAN', 'CREATE'),
('LOAN_READ', 'Consulter les emprunts', 'LOAN', 'READ'),
('LOAN_UPDATE', 'Modifier un emprunt', 'LOAN', 'UPDATE'),
('LOAN_DELETE', 'Supprimer un emprunt', 'LOAN', 'DELETE'),
-- Permissions sur les utilisateurs
('USER_CREATE', 'Créer un utilisateur', 'USER', 'CREATE'),
('USER_READ', 'Consulter les utilisateurs', 'USER', 'READ'),
('USER_UPDATE', 'Modifier un utilisateur', 'USER', 'UPDATE'),
('USER_DELETE', 'Supprimer un utilisateur', 'USER', 'DELETE'),
-- Permissions sur les groupes
('GROUP_CREATE', 'Créer un groupe', 'GROUP', 'CREATE'),
('GROUP_READ', 'Consulter les groupes', 'GROUP', 'READ'),
('GROUP_UPDATE', 'Modifier un groupe', 'GROUP', 'UPDATE'),
('GROUP_DELETE', 'Supprimer un groupe', 'GROUP', 'DELETE')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insertion des permissions supplémentaires pour l'administration système
INSERT INTO `permissions` (`name`, `description`, `resource`, `action`) VALUES
-- Permissions de configuration système
('SYSTEM_CONFIG', 'Configurer les paramètres système', 'SYSTEM', 'CONFIG'),
('SYSTEM_BACKUP', 'Gérer les sauvegardes', 'SYSTEM', 'BACKUP'),
('SYSTEM_RESTORE', 'Restaurer les sauvegardes', 'SYSTEM', 'RESTORE'),
-- Permissions de statistiques
('STATS_VIEW', 'Voir les statistiques', 'STATS', 'READ'),
('STATS_EXPORT', 'Exporter les statistiques', 'STATS', 'EXPORT'),
-- Permissions de rapports
('REPORT_CREATE', 'Créer des rapports', 'REPORT', 'CREATE'),
('REPORT_READ', 'Consulter les rapports', 'REPORT', 'READ'),
('REPORT_EXPORT', 'Exporter les rapports', 'REPORT', 'EXPORT')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Attribution des permissions aux groupes
-- Administrateurs (tous les droits)
INSERT IGNORE INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Administrateurs';

-- Bibliothécaires (permissions étendues)
INSERT IGNORE INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Bibliothécaires'
AND p.name IN (
    'BOOK_CREATE', 'BOOK_READ', 'BOOK_UPDATE', 'BOOK_DELETE',
    'LOAN_CREATE', 'LOAN_READ', 'LOAN_UPDATE', 'LOAN_DELETE',
    'USER_READ', 'USER_CREATE',
    'STATS_VIEW', 'REPORT_READ'
);

-- Lecteurs (permissions de base)
INSERT IGNORE INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Lecteurs'
AND p.name IN (
    'BOOK_READ',
    'LOAN_READ', 'LOAN_CREATE'
);

-- Attribution des permissions médias aux groupes
INSERT IGNORE INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Administrateurs'
AND p.name IN ('MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE');

INSERT IGNORE INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Bibliothécaires'
AND p.name IN ('MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE');

-- Insertion d'un administrateur par défaut (mot de passe: admin123)
INSERT IGNORE INTO users (username, password_hash, group_id) 
SELECT 'admin', '$2a$10$TeW2oGu/AvWxBSn7bUHnpOKkzQz4Xrf9TU72lKMKSSQbIby1C4Pwi', g.id
FROM `groups` g WHERE g.name = 'Administrateurs'; 

-- Création de la table des genres (MySQL)
CREATE TABLE IF NOT EXISTS genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion de genres par défaut (UTF-8)
-- Insertion des genres avec encodage UTF-8
INSERT INTO genres (name) VALUES
  ('Roman'),
  ('Essai'),
  ('Science-Fiction'),
  ('Biographie'),
  ('Poésie'),
  ('Théâtre');