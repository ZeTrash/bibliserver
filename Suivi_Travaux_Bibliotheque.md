# Suivi des Travaux – Application de gestion de bibliothèque

## ✅ Terminé
- Modélisation conceptuelle (MERISE)
- Modélisation logique de la base de données
- Création du schéma de la base de données
- Rédaction des scripts SQL de création
- Ajout des données initiales (import ou saisie)
- Création des requêtes SQL les plus utilisées (recherche, emprunt, retour, etc.)
- Création des groupes d’utilisateurs (admin, bibliothécaire, lecteur, etc.)
- Définition des rôles et privilèges pour chaque groupe
- Attribution des privilèges dans la base de données
- Mise en place du projet serveur (structure, dépendances)
- Connexion à la base de données
- Développement des DAO (Data Access Object)
- Développement des modèles (Book, User, Loan, etc.)
- Développement des contrôleurs (gestion des livres, utilisateurs, prêts, etc.)
- Gestion de l’authentification et des sessions
- Conception des interfaces graphiques (FXML)
- Développement des vues (livres, utilisateurs, prêts, groupes, etc.)
- Intégration des contrôleurs côté client
- Gestion des interactions utilisateur
- Mise en place du chiffrement des mots de passe (BCrypt)
- Gestion des droits d’accès selon les privilèges
- Protection contre les injections SQL et autres failles (PreparedStatement)
- Rédaction de la documentation technique (README.md)

## ⬜ Non terminé
- Analyse des besoins
- Rédaction du cahier des charges
- Préparation de l’environnement serveur (local/réseau)
- Déploiement de l’application serveur
- Déploiement de l’application client
- Recette fonctionnelle avec l’utilisateur final
- Rédaction du guide utilisateur
- Formation des utilisateurs

## ➡️ En cours / À compléter (détails)
- Ajout d'utilisateur via l'interface graphique (méthode `handleAddUser` dans `UsersController` non implémentée)
- Modification d'utilisateur via l'interface graphique (méthode `handleEditUser` dans `UsersController` non implémentée)
- Affichage des livres populaires sur le tableau de bord (`loadPopularBooks` dans `DashboardController` non implémenté)
- Gestion avancée des erreurs et alertes utilisateur (plusieurs TODO dans les contrôleurs)
- Rédaction et exécution des tests unitaires (partiellement présent, à compléter)
- Tests d’intégration (client-serveur, partiellement présents)

### 14/07/2025
- Correction du test UI d’ajout d’utilisateur (attente explicite, robustesse ComboBox groupe)
- Ajout d’un groupe de test automatique si besoin pour fiabiliser les tests UI
- Tous les tests passent (DAO, UI)
- JaCoCo fonctionne avec Java 17, rapport généré
- Code poussé sur GitHub

---

### Légende
- ✅ : Terminé
- ⬜ : Non terminé
- ➡️ : En cours / À compléter 