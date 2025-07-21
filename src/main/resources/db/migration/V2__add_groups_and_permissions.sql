-- Création de la table des groupes
CREATE TABLE IF NOT EXISTS `groups` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table des permissions
CREATE TABLE IF NOT EXISTS `permissions` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` TEXT,
    `resource` VARCHAR(50) NOT NULL,
    `action` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `resource_action_unique` (`resource`, `action`)
);

-- Création de la table de liaison groupe-permission
CREATE TABLE IF NOT EXISTS `group_permissions` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `group_id` INT NOT NULL,
    `permission_id` INT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `permissions`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `group_permission_unique` (`group_id`, `permission_id`)
);

-- Ajout de la colonne group_id à la table users si elle n'existe pas déjà
ALTER TABLE `users` ADD COLUMN IF NOT EXISTS `group_id` INT;
ALTER TABLE `users` ADD FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`);

-- Insertion des groupes par défaut
INSERT INTO `groups` (`name`, `description`) VALUES
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
('GROUP_DELETE', 'Supprimer un groupe', 'GROUP', 'DELETE');

-- Attribution des permissions aux groupes
-- Administrateurs (tous les droits)
INSERT INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Administrateurs';

-- Bibliothécaires
INSERT INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Bibliothécaires'
AND p.name IN (
    'BOOK_CREATE', 'BOOK_READ', 'BOOK_UPDATE',
    'LOAN_CREATE', 'LOAN_READ', 'LOAN_UPDATE',
    'USER_READ'
);

-- Lecteurs
INSERT INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Lecteurs'
AND p.name IN ('BOOK_READ', 'LOAN_READ');

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

-- Permissions pour la gestion des médias
INSERT INTO `permissions` (`name`, `description`, `resource`, `action`) VALUES
('MEDIA_CREATE', 'Créer un média', 'MEDIA', 'CREATE'),
('MEDIA_READ', 'Consulter les médias', 'MEDIA', 'READ'),
('MEDIA_UPDATE', 'Modifier un média', 'MEDIA', 'UPDATE'),
('MEDIA_DELETE', 'Supprimer un média', 'MEDIA', 'DELETE')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Attribution des permissions médias aux groupes
INSERT INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Administrateurs'
AND p.name IN ('MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE');

INSERT INTO `group_permissions` (`group_id`, `permission_id`)
SELECT g.id, p.id
FROM `groups` g, `permissions` p
WHERE g.name = 'Bibliothécaires'
AND p.name IN ('MEDIA_CREATE', 'MEDIA_READ', 'MEDIA_UPDATE', 'MEDIA_DELETE'); 