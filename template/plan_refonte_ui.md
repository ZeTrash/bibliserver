# Plan de refonte de l’interface utilisateur

## Objectif
Créer une interface moderne, professionnelle et agréable pour l’application de gestion de bibliothèque, en s’inspirant du **template 1** (clair) et du **template 3** (sombre).

---

## 1. Structure générale
- **Menu latéral (sidebar)** à gauche, avec icônes et texte, fond bleu nuit (clair) ou gris anthracite (sombre).
- **Barre supérieure** (optionnelle) pour le profil utilisateur, notifications, recherche globale.
- **Zone centrale** : dashboard, tableaux, statistiques, cartes, etc.
- **Utilisation de cartes** pour les blocs d’information (objectifs, activité, livres récents, etc.).

---

## 2. Palette de couleurs
### Thème clair (template 1)
- Fond principal : #F7F9FB
- Sidebar : #223A5E
- Accent : #21B573 (vert émeraude), #FFD700 (jaune doré)
- Texte : #222, #555
- Cartes : blanc, ombre douce

### Thème sombre (template 3)
- Fond principal : #181A1B ou #23272E
- Sidebar : #23272E
- Accent : #21B573, #FFD700
- Texte : #EEE, #BBB
- Cartes : #23272E, ombre plus marquée

---

## 3. Typographie
- Police principale : Inter, Roboto, Segoe UI, Open Sans
- Titres : gras, taille importante
- Texte courant : régulier, bonne lisibilité

---

## 4. Composants à moderniser
- **Sidebar** :
  - Icônes (SVG ou Unicode)
  - Boutons arrondis, effet hover
  - Indicateur de sélection
- **Boutons** :
  - Arrondis, ombre, couleurs d’accent
  - Effet hover et pressed
- **Tableaux** :
  - En-tête coloré, lignes alternées, effet hover
  - Coins arrondis
- **Cartes/statistiques** :
  - Blocs avec ombre, coins arrondis, icônes
- **Champs de saisie** :
  - Bordure colorée au focus, fond blanc ou sombre selon le thème
- **Notifications/toasts** :
  - Apparition/disparition animée, couleurs d’accent
- **Mode sombre** :
  - Basculer tout le style via une classe ou un fichier CSS supplémentaire

---

## 5. Fonctionnalités UI à ajouter ou améliorer
- Barre de recherche globale (en haut)
- Affichage du profil utilisateur (avatar, nom)
- Statistiques visuelles (graphes, jauges)
- Livres récents/activités récentes sous forme de cartes
- Responsive (adaptation à la taille de la fenêtre)
- Animation douce lors des transitions

---

## 6. Organisation technique
- Un fichier CSS principal pour le thème clair
- Un fichier CSS additionnel pour le thème sombre (activé par un bouton ou automatiquement)
- Utilisation systématique des classes CSS dans les FXML
- Prévoir des icônes (SVG ou police d’icônes)

---

## 7. Étapes de réalisation
1. Définir la palette de couleurs et la typographie dans le CSS
2. Refaire la sidebar/menu avec icônes et effet moderne
3. Moderniser les boutons et les tableaux
4. Créer des styles de cartes/statistiques
5. Ajouter la barre supérieure (recherche, profil)
6. Intégrer le thème sombre (CSS dédié + bouton de bascule)
7. Tester et ajuster chaque écran

---

## 8. Inspirations visuelles
- Template 1 (clair) : dashboard moderne, cartes, sidebar épurée
- Template 3 (sombre) : dashboard sombre, graphiques colorés, ambiance tech

---

**Ce plan servira de feuille de route pour la refonte complète de l’interface.** 