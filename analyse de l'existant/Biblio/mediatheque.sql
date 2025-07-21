-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 12 mai 2025 à 07:32
-- Version du serveur : 8.3.0
-- Version de PHP : 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `mediatheque`
--

-- --------------------------------------------------------

--
-- Structure de la table `auteur`
--

DROP TABLE IF EXISTS `auteur`;
CREATE TABLE IF NOT EXISTS `auteur` (
  `id_auteur` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_auteur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `cle_temporaire`
--

DROP TABLE IF EXISTS `cle_temporaire`;
CREATE TABLE IF NOT EXISTS `cle_temporaire` (
  `id_cle_temporaire` int NOT NULL AUTO_INCREMENT,
  `cle` varchar(255) DEFAULT NULL,
  `employé_id_employé` int NOT NULL,
  `status` enum('utilisee','non_utilisee') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'non_utilisee',
  PRIMARY KEY (`id_cle_temporaire`),
  KEY `fk_cle_temporaire_employé1_idx` (`employé_id_employé`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `cle_temporaire`
--

INSERT INTO `cle_temporaire` (`id_cle_temporaire`, `cle`, `employé_id_employé`, `status`) VALUES
(37, 'wRe0AtNQRQoyDBRIyQeh', 1, 'non_utilisee'),
(42, 'tHB5SvhCTmBUb8VjtGbH', 1, 'non_utilisee'),
(43, 'qqeyMVB6TX1EZqjjFMcR', 1, 'non_utilisee'),
(44, 'cbHcNRuFhRO99gEo6JtL', 1, 'non_utilisee'),
(45, 'WIVpqfC3SIV3A5OwXPNw', 1, 'non_utilisee'),
(55, 'NxiMYCoonD1HiC9WWGsJ', 1, 'utilisee');

-- --------------------------------------------------------

--
-- Structure de la table `edition`
--

DROP TABLE IF EXISTS `edition`;
CREATE TABLE IF NOT EXISTS `edition` (
  `id_edition` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_edition`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `edition`
--

INSERT INTO `edition` (`id_edition`, `nom`) VALUES
(1, 'Club France Loisirs'),
(2, 'Stock pour la traduction en Français'),
(3, 'Ramsay'),
(4, 'Dunod'),
(5, '2eme edition'),
(6, 'Grand Livre'),
(7, 'Hatier'),
(8, 'Dunod'),
(9, 'MENTOR'),
(10, 'Eyrolles'),
(11, 'Springer'),
(12, 'Pearson'),
(13, 'Runus'),
(16, 'Soline');

-- --------------------------------------------------------

--
-- Structure de la table `employé`
--

DROP TABLE IF EXISTS `employé`;
CREATE TABLE IF NOT EXISTS `employé` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) DEFAULT NULL,
  `prenom` varchar(100) DEFAULT NULL,
  `email1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `mot_de_passe` varchar(155) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `photo` text NOT NULL,
  `contact` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email1`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `employé`
--

INSERT INTO `employé` (`id`, `nom`, `prenom`, `email1`, `mot_de_passe`, `photo`, `contact`) VALUES
(1, 'JAMES', 'Marie Erylise', 'marieerylise@gmail.com', '$2y$10$rQ0YojuEijLeNPOmYMNnPuHbzlnIYI5D.GPlsEBtUu48hy4iUf4VO', 'uploads/1730741334531.jpg', '0327254916');

-- --------------------------------------------------------

--
-- Structure de la table `employé_has_livre`
--

DROP TABLE IF EXISTS `employé_has_livre`;
CREATE TABLE IF NOT EXISTS `employé_has_livre` (
  `employé_id_employé` int NOT NULL,
  `livre_id_livre` int NOT NULL,
  KEY `fk_employé_has_livre_livre1_idx` (`livre_id_livre`),
  KEY `fk_employé_has_livre_employé1_idx` (`employé_id_employé`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `etudiant`
--

DROP TABLE IF EXISTS `etudiant`;
CREATE TABLE IF NOT EXISTS `etudiant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom_etudiant` varchar(200) DEFAULT NULL,
  `prenom` varchar(100) DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  `lieu_naissance` varchar(200) DEFAULT NULL,
  `sexe` varchar(10) DEFAULT NULL,
  `num_cin` varchar(50) DEFAULT NULL,
  `lieu_cin` varchar(200) DEFAULT NULL,
  `duplicata_cin` varchar(50) DEFAULT NULL,
  `adresse_etu` text,
  `telephone1` varchar(10) DEFAULT NULL,
  `telephone2` varchar(10) DEFAULT NULL,
  `email1` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `delegue` varchar(5) DEFAULT 'NON',
  `nationalite` varchar(100) DEFAULT NULL,
  `serie_bacc` varchar(100) DEFAULT NULL,
  `annee_bacc` varchar(10) DEFAULT NULL,
  `nom_mere` varchar(200) DEFAULT NULL,
  `nom_pere` varchar(200) DEFAULT NULL,
  `contact_parent` varchar(10) DEFAULT NULL,
  `adresse_parent` text,
  `photo` text,
  `boursier` varchar(5) DEFAULT 'OUI',
  `taux_bourse` varchar(5) DEFAULT '4/4',
  `parcours_abrevie` varchar(50) DEFAULT NULL,
  `bacc` varchar(25) DEFAULT NULL,
  `mention_bacc` varchar(50) DEFAULT NULL,
  `lieu_bacc` varchar(100) DEFAULT NULL,
  `profession_pere` varchar(100) DEFAULT NULL,
  `profession_mere` varchar(100) DEFAULT NULL,
  `ville_parent` varchar(100) DEFAULT NULL,
  `date_cin` date DEFAULT NULL,
  `1er_tranche_num_recu` varchar(50) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `dernier_diplome` varchar(100) DEFAULT NULL,
  `serie_dernier_diplome` varchar(100) DEFAULT NULL,
  `mention_dernier_diplome` varchar(100) DEFAULT NULL,
  `annee_dernier_diplome` varchar(4) DEFAULT NULL,
  `lieu_dernier_diplome` varchar(100) DEFAULT NULL,
  `annee_univ` varchar(100) DEFAULT NULL,
  `semestre` varchar(20) DEFAULT NULL,
  `provisoire_1` varchar(10) DEFAULT NULL,
  `id_cle_temporaire` varchar(100) DEFAULT NULL,
  `mot_de_passe` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `premiere_connexion` varchar(1) NOT NULL DEFAULT 'o',
  `numero_carte_etudiant` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `1er_tranche_num_recu` (`1er_tranche_num_recu`),
  KEY `parcours_abrevie` (`parcours_abrevie`),
  KEY `parcours_abrevie_2` (`parcours_abrevie`)
) ENGINE=MyISAM AUTO_INCREMENT=2823 DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Structure de la table `livre`
--

DROP TABLE IF EXISTS `livre`;
CREATE TABLE IF NOT EXISTS `livre` (
  `id_livre` int NOT NULL,
  `titre` varchar(45) DEFAULT NULL,
  `auteur` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `categorie` varchar(45) DEFAULT NULL,
  `langue` varchar(5) DEFAULT NULL,
  `niveau` varchar(15) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `quantite_exemplaire` varchar(5) DEFAULT NULL,
  `media_id_media` int DEFAULT NULL,
  `edition_id_edition` int NOT NULL,
  `status` varchar(100) DEFAULT 'disponible',
  `isbn` varchar(100) DEFAULT NULL,
  `photos` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `annee` year NOT NULL,
  `date_ajout` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_livre`),
  KEY `fk_livre_edition1_idx` (`edition_id_edition`),
  KEY `idx_livre_media` (`id_livre`,`media_id_media`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `livre`
--

INSERT INTO `livre` (`id_livre`, `titre`, `auteur`, `description`, `categorie`, `langue`, `niveau`, `type`, `quantite_exemplaire`, `media_id_media`, `edition_id_edition`, `status`, `isbn`, `photos`, `annee`, `date_ajout`) VALUES
(4, 'Le grand livre  de l\'électricité', 'J.Ney', '\"Le Grand Livre de l\'Électricité\" est un ouvrage complet qui traite des fondamentaux et des applications pratiques de l\'électricité. Ce livre est conçu pour les étudiants, les professionnels et les passionnés d\'électricité, abordant une large gamme de suj', 'Electricite', 'FR', 'Lycéen ', NULL, '0', NULL, 8, 'disponible', '978-2100579781', 'Le grand livre  d\'Electricité.jpg', '1964', '2024-10-20 20:09:39'),
(6, 'Electromecanique', 'Damien Grenier', 'Il vise à compléter l\'enseignement de base et à offrir un ensemble cohérent sur les aspects méthodologiques et les applications de l\'ensemble du domaine électromécanique, principalement centré sur les puissances faibles à moyennes (quelques kW).', 'electronique', 'FR', '2nd cycle', NULL, '0', NULL, 4, 'disponible', '210-0057-3256', '1730741334619.jpg', '2001', '2024-10-15 09:39:31'),
(25, 'Introduction a l\'electrotechnique', 'Jacques Laroche', 'Introduction à l\'électrotechnique\" est un ouvrage de base en génie électrique qui couvre les principes fondamentaux de l\'électrotechnique. Il présente les concepts essentiels, comme les circuits électriques, les champs magnétiques, et l’électronique de pu', 'Electronique', 'Franç', '1er Cycle', NULL, '0', NULL, 10, 'disponible', '210-0057-146', '1730741334624.jpg', '2002', '2024-11-09 08:22:23'),
(32, 'MECANIQUE STATIQUE', 'J.P.LARRALDE', '\"Mécanique Statique\" est un ouvrage fondamental qui traite des principes de la mécanique appliqués aux systèmes en équilibre. Ce livre est essentiel pour les étudiants en ingénierie, en physique, et en architecture. Les sujets couverts incluent: Principes', 'Mécanique', 'FR', 'DTSS/Master', NULL, '2', NULL, 11, 'disponible', '2-225-748222-5', '1730741334612.jpg', '1992', '2024-10-20 20:19:39'),
(111, 'WCDMA UMTS', 'Harri Holma', 'Comprendre et exploiter la norme de l\'Internet mobile de demain\r\nMalgré l\'abondante polémique autour de l\'UMTS (Universal Mobile Telecommunication System) et de l\'attribution des licences européennes, le grand public commencera, en 2003, à découvrir les p', 'INFORMATIQUE', 'EN', 'Facultative', 'physique', '2', NULL, 1, 'disponible', '1.548-535-448-314X', '1730741334636.jpg', '1999', '2024-10-15 09:39:31'),
(114, 'Le basic facile par une methode progressive', 'Seymour C. Hirsch', '\"Le Basic Facile\" est un manuel pédagogique conçu pour l\'apprentissage du français, principalement destiné aux débutants. Il propose une approche progressive qui permet aux apprenants d\'acquérir les bases essentielles de la langue française. Le livre se c', 'Informatique', 'FR', 'Facultative', NULL, '0', NULL, 7, 'disponible', '978-2746237672', '1730741334631.jpg', '1982', '2024-10-20 19:19:00'),
(118, 'Word 6 pour Windows', 'Peter Ebel', '**\"Word 6 pour Windows\"** est un guide pratique pour apprendre à utiliser Microsoft Word, version 6, sur Windows. Il explique les principales fonctionnalités du logiciel, comme la création de documents, la mise en page, l\'insertion d\'images et de tableaux', 'Informatique', 'FR', 'Facultative', NULL, '2', NULL, 12, 'disponible', '2-7242-0469-7', '1730741334642.jpg', '1993', '2024-10-20 19:08:48'),
(232, 'La bible de l\'humour juif-2-', 'Dory Rotnemer', 'un recueil d’histoires drôles et de réflexions humoristiques sur la culture juive, écrit par Marc-Alain Ouaknin et Dory Rotnemer. Ce livre continue la tradition du premier tome en explorant avec humour et tendresse les aspects de la vie juive, en mettant ', 'Roman', 'FR', 'Facultative', NULL, '0', NULL, 3, 'disponible', '2.290-04976-X', '1730741334499.jpg', '1997', '2024-10-20 18:50:12'),
(563, 'Le souffle du destin', 'Elisabeth Adler', 'Dans la Spéculation transcendante sur l’intentionnalité apparente dans le destin de l’individu, essai des Parerga, Schopenhauer risque l’hypothèse d’un sens métaphysique de la vie de l’individu dont le destin singulier serait conduit par la Volonté ', 'ROMAN', 'FR', 'Facultative', NULL, '0', NULL, 16, 'disponible', '2.87677.066.0', '1730741334506.jpg', '1977', '2024-10-15 09:39:31'),
(801, 'Les oiseaux se cachent pour mourir', 'Colleen Mclullough ', 'Écrasé par le soleil brûlant d\'Australie, le domaine de Drogheda déploie ses milliers d\'hectares à perte de vue. Sur ces terres, les Cleary vont pouvoir entamer une nouvelle vie, loin de la misère qu\'ils ont connue dans leur Nouvelle-Zélande natale. ', 'ROMAN', 'FR', 'Facultative', NULL, '02', NULL, 1, 'disponible', '2-7242-0469-7', '1730741334689.jpg', '1977', '2024-10-15 09:39:31');

-- --------------------------------------------------------

--
-- Structure de la table `livre_has_auteur`
--

DROP TABLE IF EXISTS `livre_has_auteur`;
CREATE TABLE IF NOT EXISTS `livre_has_auteur` (
  `livre_id_livre` int NOT NULL,
  `livre_media_id_media` int NOT NULL,
  `auteur_id_auteur` int NOT NULL,
  PRIMARY KEY (`livre_id_livre`),
  KEY `fk_livre_has_auteur_auteur1_idx` (`auteur_id_auteur`),
  KEY `fk_livre_has_auteur_livre1_idx` (`livre_id_livre`,`livre_media_id_media`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `media`
--

DROP TABLE IF EXISTS `media`;
CREATE TABLE IF NOT EXISTS `media` (
  `id_media` int NOT NULL AUTO_INCREMENT,
  `titre` varchar(255) DEFAULT NULL,
  `type` enum('audio','vidéo','image') DEFAULT NULL,
  `dimension` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `duree` time DEFAULT NULL,
  `categorie` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `createur` varchar(45) DEFAULT NULL,
  `chemin` varchar(255) NOT NULL,
  PRIMARY KEY (`id_media`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `media`
--

INSERT INTO `media` (`id_media`, `titre`, `type`, `dimension`, `duree`, `categorie`, `createur`, `chemin`) VALUES
(1, 'Full Impact', 'audio', NULL, '00:00:18', 'Anglais', 'J. Martinez', ''),
(2, 'Action Goals', 'audio', '', '00:00:20', 'Anglais', 'P. Billaud', ''),
(3, 'Projects', 'audio', NULL, '00:00:00', 'Anglais', 'Jeremy Reyburn', ''),
(13, 'qqqq', NULL, '5184 X 2730', NULL, 'aaa', 'Andrea', 'uploads/photos/AAAAAA.png'),
(15, 'CIMPA', NULL, '180 x 14', NULL, 'qqq', 'Andrea', 'uploads/photos/20210220Cuisine_mar289.jpg'),
(16, 'viviane', NULL, '5184 X 2730', NULL, 'ROMAN', 'Andrea', 'uploads/photos/18AOUT.png'),
(22, 'CIMPA', 'image', '5184 X 2730', NULL, 'image évènement', 'RAZANATSARA Andréa', 'uploads/photos/IMG_7984.JPG'),
(23, 'CIMPA', 'image', '5184 X 2730', NULL, 'image évènement', 'RAZANATSARA Andréa', 'uploads/photos/IMG_8162.JPG'),
(24, 'CIMPA', 'image', '5184 X 2730', NULL, 'image évenèment', 'RAZANATSARA André', 'uploads/photos/IMG_8153.JPG');

-- --------------------------------------------------------

--
-- Structure de la table `membre`
--

DROP TABLE IF EXISTS `membre`;
CREATE TABLE IF NOT EXISTS `membre` (
  `id_membre` int NOT NULL AUTO_INCREMENT,
  `etudiant_id_etudiant` int DEFAULT NULL,
  `personnel_id_personnel` int DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `employe_id_employe` int DEFAULT NULL,
  PRIMARY KEY (`id_membre`),
  UNIQUE KEY `fk_membre_employe` (`employe_id_employe`),
  UNIQUE KEY `fk_membre_etudiant` (`etudiant_id_etudiant`),
  UNIQUE KEY `fk_membre_personnel` (`personnel_id_personnel`),
  KEY `fk_membre_etudiant1_idx` (`etudiant_id_etudiant`),
  KEY `fk_membre_personnel1_idx` (`personnel_id_personnel`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `memoire`
--

DROP TABLE IF EXISTS `memoire`;
CREATE TABLE IF NOT EXISTS `memoire` (
  `id_memoire` varchar(20) NOT NULL,
  `collection` varchar(45) DEFAULT NULL,
  `categorie` varchar(45) DEFAULT NULL,
  `mention` varchar(45) DEFAULT NULL,
  `niveau` varchar(10) DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  `auteur` varchar(100) DEFAULT NULL,
  `annee` year DEFAULT NULL,
  PRIMARY KEY (`id_memoire`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `memoire`
--

INSERT INTO `memoire` (`id_memoire`, `collection`, `categorie`, `mention`, `niveau`, `titre`, `auteur`, `annee`) VALUES
('', 'Génie Industriel ', 'TIM', 'Telecomunication', 'DTS', 'Creation de l\'audio FEOM-BIAVY', 'JAMES Marie Erylise', '2023'),
('001-MPAN', 'Genie Civil et Genie Naval', 'Travaux Pratique (TP)', 'Génie Civil', 'DTS', 'Etude d\'aménagement et assainissement du Boulevard Andevoranto(gare routiere) jusqu au boulevard Ralaimongo tranon-jiro,Toamasina ', 'Faly Noel Ephrem', '2023'),
('001-REHA', 'Génie Management,Commerce et Service (EGMCS)', 'Tourisme et Gestion Hoteliere (TGH)', 'Commerce et Services (MCS)', 'DTS', 'Elaboration d\'un plan de soutien des entreprises touristiques a Nosy-be impactes par la crise sanitaire: Cas de l\'EDBM', 'TOMBOMIASA Franselin Gino', '2020'),
('001-TSARA', 'Génie Industriel', 'Maintance des Systemes Automatisés (MSA)', 'Maintenance et Energie (MME)', 'DTS', 'Etude du système de detection automatique des defauts sur les lignes de distribution', 'FALIMANANA Davida', '2022'),
('002-MPAN', 'Genie Civil et Genie Naval', 'Travaux Pratique ( TP ) ', 'Génie Civil', 'DTS', 'Projet d\'aménagemant urbaine dans la ville d\'Ambilobe sur le Fokotany Matiakoho', 'RAKOTOMALALA Ambinintsoa Gabriel', '2023'),
('002-REHA', 'Génie Management,Commerce et Service (EGMCS)', 'Comptabilité,Contrôle et Audit (CCA)', 'Finance,Banque et Assurance (MFBA)', 'DTSS', 'Analyse de la performance budgetaire de la collectivité territoriale décentralisée: \"cas de la Commune Urbaine d\'Ambanja (CUA)\"', 'VELOMAMY Rasoanirina Angela', '2020'),
('002-TSARA', 'Génie Industriel', 'Maintance des Systemes Automatisés (MSA)', 'Maintenance et Energie (MME)', 'DTS', 'Etude des systèmes de suspension automatique de Renault DXI 440', 'ANAS ', '2022'),
('003-MPAN', 'Genie Civil et Genie Naval', 'Travaux Pratique  (TP)', 'Génie Civil', 'DTS', 'Projet de construction de l\'avenue de l\'emergence de Diego Suarez sise au Fokontany Tanambao V Antsiranana', 'ABDALLAH Moubarrak', '2023'),
('003-REHA', 'Génie Management,Commerce et Service (EGMCS)', 'Comptabilité,Contrôle et Audit (CCA)', 'Finance,Banque et Assurance (MFBA)', 'DTSS', 'Analyse du recouvrement de la taxe de sejour communale \"cas de la CUDS\"', 'VELONARIVO Lovatiana Joella', '2020'),
('003-TSARA', 'Génie Industriel', 'Système a Energie Renouvelable et Alternative', 'Maintenance et Energie (MME)', 'DTSS', 'Etude d\'un système d\'Autoconsomation par photovoltaique', 'HAZZAM Chamsouddine', '2022'),
('004-MPAN', 'Genie Civil et Genie Naval', 'Construction Civil et Infrastructure (CCI)', 'Génie Civil', 'DTSS', 'Projet de construction d\'un batiment R+3 a usage Mixte (Banque et Appartement d\'Habitation)', 'TOMBO Jean Daya Houssen', '2023'),
('004-REHA', 'Génie Management,Commerce et Service (EGMCS)', 'Management des Entreprises et Organisations (', 'Management', 'Ingenieur', 'La redifinition de la politique de distribution au sein de la societe JB SA Antsiranana', 'RAFANOMEZANA Odissin', '2020'),
('004-TSARA', 'Génie Industriel', 'Techniques Avancées de Maintenance (TAM)', 'Maintenance et Energie (MME)', 'Ingénieur', 'Etude,Analyse,Diagnostic et Proposition de Maintenance ameliorative des barrieres de Parking et des tapis a bagages', 'VONJISOA Marc Johannes', '2022'),
('005-MPAN', 'Genie Civil et Genie Naval', 'Construction Civil et Infrastructure (CCI)', 'Génie Civil', 'DTSS', 'Projet de construction de route armé reliant RIP 3D - route et RNP 06 dans la cuds', 'BELAHY Rogin Rodrick', '2023'),
('005-TSARA', 'Génie Industriel', 'Techniques Avancées de Maintenance (TAM)', 'Maintenance et Energie (MME)', 'Ingenieur', 'Etude de remplacement d\'un synchroniseur et regulateur de vitesse de la centrale hydroelectrique de Tsiazompaniry par un autre marque standard', 'RANDRIAMALALA Rolland Pierrot', '2022'),
('006-MPAN', 'Genie Civil et Genie Naval', 'Construction Civil et Infrastructure (CCI)', 'Génie Civil', 'DTSS', 'Projet de construction d\'une maison d\'habitation R+3 type appartement', 'ANGELICO', '2023'),
('006-TSARA', 'Génie Industriel', 'Techniques Avancées de Maintenance (TAM)', 'Maintenance et Energie (MME)', 'Ingénieur', 'Modernisation de la gestion de maintenance de la chaine de production du chantier cour a canne,usine SUCOMA', 'ANJARA Jodie Carmel', '2022'),
('007-MPAN', 'Genie Civil et Genie Naval', 'Technologie Naval (TechNa)', 'Génie Naval', 'DTS', 'Amélioration du bras mécanique a commande pneumatique d\'un soudage cabochon', 'LANDRO RAHERIARISON Bryan Tommy', '2023'),
('007-TSARA', 'Génie Management,Commerce et Service (EGMCS)', 'Tourisme et Gestion Hoteliere (TGH)', 'Commerce et Services (MCS)', 'DTS', 'Les réseaux sociaux comme moyen de promotion de l\'hotel belle vue sur le marché', 'BEZOKY Nayka Nadege Michaella', '2022'),
('008-MPAN', 'Genie Civil et Genie Naval', 'Technologie Naval (TechNa)', 'Génie Naval', 'DTS', 'Etude de procédure de traitement de la coque du patrouilleur cotier MALAKY', 'LOVASOA Nomenjanahary Cedrique Justin', '2023'),
('008-TSARA', 'Génie Management,Commerce et Service (EGMCS)', 'Tourisme et Gestion Hoteliere (TGH)', 'Commerce et Services (MCS)', 'DTS', 'Constribution a l\'amelioration des offres de services face aux entreprises concurentes: cas de l\'Hotel CONCORDE', 'BEZARA Nathalie Noricia', '2022'),
('009-MPAN', 'Genie Civil et Genie Naval', 'Technologie Naval (TechNa)', 'Génie Naval', 'DTS', 'Maintanance du système de propulsion et des hélices du remorqueur ', 'SOUSSA Fernando Thierry', '2023'),
('009-TSARA', 'Génie Management,Commerce et Service (EGMCS)', 'Comptabilité,Contrôle et Audit (CCA)', 'Finance,Banque et Assurance (MFBA)', 'DTSS', 'Analyse de la procédure comptable de l\'agence auximad S.A, Antsiranana', 'JAOSANTA Marino Floris', '2022'),
('010-TSARA', 'Génie Management,Commerce et Service (EGMCS)', 'Management des Entreprises et Organisations (', 'Management', 'Ingenieur', 'Dynamique de la strategie de recouvrement cas de la JIRAMA Antsiranana', 'MBOTITOMBO Sania', '2022'),
('011-MPAN', 'Génie Industriel', 'Système a Energie Renouvelable et Alternative', 'Maintenance et Energie (MME)', 'DTS', 'Etude et amélioration de la centrale Hydroelectrique de NAMORONA', 'RABELAZANDRAINY Fanambinantsoa Bienvenu', '2023'),
('011-TSARA', 'Génie Management,Commerce et Service (EGMCS)', 'Management des Entreprises et Organisations (', 'Management', 'Ingenieur', 'L\'organisation de la gestion du portefeuille client de l\'assurance ARO TOAMASINA', 'RAMARIANTSOA Lanja Beatrice', '2022'),
('012-MPAN', 'Génie industriel', 'Système a Energie Renouvelable et Alternative', 'Maintenance et Energie (MME)', 'DTSS', 'Etude et Réalisation d\'un nano-reseau dans la zone BESOKATRA', 'BEVANONANA Osvaldo Nawfel', '2023'),
('2023', 'genie industriel', 'Informatique', 'tc', 'adr', 'bibliotheque', 'marie', '2024');

-- --------------------------------------------------------

--
-- Structure de la table `operation`
--

DROP TABLE IF EXISTS `operation`;
CREATE TABLE IF NOT EXISTS `operation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_action` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `date_debut_emprunt` date DEFAULT NULL,
  `date_fin_emprunt` date DEFAULT NULL,
  `statut_emprunt` enum('en cours','proche de la limite','retard','rendus') DEFAULT NULL,
  `date_retour` date DEFAULT NULL,
  `statut_retour` enum('rendus','non rendus') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'non rendus',
  `date_reservation` date DEFAULT NULL,
  `statut_reservation` enum('en attente','approuvée') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `renouvellement` varchar(45) DEFAULT NULL,
  `livre_id_livre` int NOT NULL,
  `membre_id_membre` int DEFAULT NULL,
  `nombre_renouvellement_restant` tinyint NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`),
  KEY `fk_operation_livre1_idx` (`livre_id_livre`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `operation`
--

INSERT INTO `operation` (`id`, `type_action`, `date_debut_emprunt`, `date_fin_emprunt`, `statut_emprunt`, `date_retour`, `statut_retour`, `date_reservation`, `statut_reservation`, `renouvellement`, `livre_id_livre`, `membre_id_membre`, `nombre_renouvellement_restant`) VALUES
(150, 'reservation', NULL, '2025-05-12', '', '2025-05-12', 'rendus', '2024-11-21', '', NULL, 114, 7895, 3),
(151, 'reservation', NULL, '2025-05-12', '', '2025-05-12', 'rendus', '2024-11-21', '', NULL, 563, 7895, 3),
(152, 'reservation', NULL, '2025-05-12', '', '2025-05-12', 'rendus', '2024-11-21', '', NULL, 4, 7895, 3);

-- --------------------------------------------------------

--
-- Structure de la table `personnel`
--

DROP TABLE IF EXISTS `personnel`;
CREATE TABLE IF NOT EXISTS `personnel` (
  `ID_personnel` int NOT NULL AUTO_INCREMENT,
  `Ecole` varchar(6) DEFAULT NULL,
  `Nom` varchar(24) DEFAULT NULL,
  `Prenom` varchar(39) DEFAULT NULL,
  `email1` varchar(37) DEFAULT NULL,
  `email2` varchar(34) DEFAULT NULL,
  `Date_de_naissance` date DEFAULT NULL,
  `Lieu_de_naissance` varchar(40) DEFAULT NULL,
  `IM_NContrat` varchar(7) DEFAULT NULL,
  `Mention` varchar(100) DEFAULT NULL,
  `Statut` varchar(40) NOT NULL,
  `Diplome` varchar(106) DEFAULT NULL,
  `Titre` varchar(20) DEFAULT NULL,
  `Grade` varchar(21) DEFAULT NULL,
  `Categorie` varchar(4) DEFAULT NULL,
  `Date_contrat` date DEFAULT NULL,
  `Fonction` varchar(100) DEFAULT NULL,
  `Telephone1` varchar(13) DEFAULT NULL,
  `Telephone2` varchar(13) DEFAULT NULL,
  `Matiere` varchar(92) DEFAULT NULL,
  `Parcours` varchar(17) DEFAULT NULL,
  `Adresse` varchar(76) DEFAULT NULL,
  `CIN` varchar(15) DEFAULT NULL,
  `date_cin` date NOT NULL,
  `lieu_CIN` varchar(27) DEFAULT NULL,
  `piece_jointe` varchar(40) DEFAULT NULL,
  `Budget_payeur` varchar(8) DEFAULT NULL,
  `Specialit` varchar(74) DEFAULT NULL,
  `domaine_de_recherche` varchar(100) DEFAULT NULL,
  `niveau_enseigner` varchar(100) DEFAULT NULL,
  `Sexe` varchar(9) DEFAULT NULL,
  `Taux` int DEFAULT NULL,
  `CodeBanque` varchar(5) DEFAULT NULL,
  `CodeGuichet` varchar(5) DEFAULT NULL,
  `NumCompteBanque` varchar(11) DEFAULT NULL,
  `RIB` int DEFAULT NULL,
  `DomicileBanque` varchar(40) DEFAULT NULL,
  `Maintien_activit` varchar(11) DEFAULT NULL,
  `Date_prise_service` date DEFAULT NULL,
  `obligation` double DEFAULT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `id_cle_temporaire` varchar(50) DEFAULT NULL,
  `mot_de_passe` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `premiere_connexion` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'o',
  PRIMARY KEY (`ID_personnel`),
  UNIQUE KEY `ID_personnel_UNIQUE` (`ID_personnel`)
) ENGINE=InnoDB AUTO_INCREMENT=7898 DEFAULT CHARSET=utf8mb3;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `cle_temporaire`
--
ALTER TABLE `cle_temporaire`
  ADD CONSTRAINT `fk_cle_temporaire_employé1` FOREIGN KEY (`employé_id_employé`) REFERENCES `employé` (`id`);

--
-- Contraintes pour la table `employé_has_livre`
--
ALTER TABLE `employé_has_livre`
  ADD CONSTRAINT `fk_employé_has_livre_employé1` FOREIGN KEY (`employé_id_employé`) REFERENCES `employé` (`id`),
  ADD CONSTRAINT `fk_employé_has_livre_livre1` FOREIGN KEY (`livre_id_livre`) REFERENCES `livre` (`id_livre`);

--
-- Contraintes pour la table `livre`
--
ALTER TABLE `livre`
  ADD CONSTRAINT `fk_livre_edition1` FOREIGN KEY (`edition_id_edition`) REFERENCES `edition` (`id_edition`);

--
-- Contraintes pour la table `livre_has_auteur`
--
ALTER TABLE `livre_has_auteur`
  ADD CONSTRAINT `fk_livre_has_auteur_livre1` FOREIGN KEY (`livre_id_livre`,`livre_media_id_media`) REFERENCES `livre` (`id_livre`, `media_id_media`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
