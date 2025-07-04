# Système de Gestion de Bibliothèque

Une application client-serveur pour la gestion de bibliothèque développée avec JavaFX.

## Prérequis

- Java JDK 17 ou supérieur
- Maven
- MySQL Server
- IDE (recommandé : IntelliJ IDEA ou Eclipse)

## Installation

1. Clonez le dépôt :
```bash
git clone [url-du-repo]
cd bibliserver
```

2. Configurez la base de données :
- Assurez-vous que MySQL Server est en cours d'exécution
- Créez une base de données nommée 'bibliserver'
- Exécutez le script SQL d'initialisation situé dans `src/main/resources/sql/init.sql`

3. Configurez les paramètres de connexion à la base de données :
- Ouvrez le fichier `src/main/java/com/bibliserver/util/DatabaseUtil.java`
- Modifiez les constantes URL, USER et PASSWORD selon votre configuration MySQL

4. Compilez et exécutez le projet :
```bash
mvn clean install
mvn javafx:run
```

## Utilisation

1. Connexion :
- Utilisateur par défaut : admin
- Mot de passe par défaut : admin123

2. Fonctionnalités :
- Gestion des utilisateurs (création, modification, suppression)
- Gestion des livres (ajout, modification, suppression)
- Gestion des emprunts (emprunt, retour)
- Recherche de livres
- Génération de rapports

## Structure du Projet

```
bibliserver/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bibliserver/
│   │   │           ├── controller/
│   │   │           ├── dao/
│   │   │           ├── model/
│   │   │           ├── util/
│   │   │           └── view/
│   │   └── resources/
│   │       ├── fxml/
│   │       └── sql/
│   └── test/
└── pom.xml
```

## Sécurité

- Les mots de passe sont hashés avec BCrypt
- Gestion des rôles utilisateurs
- Protection contre les injections SQL avec PreparedStatement

## Support

Pour toute question ou problème, veuillez créer une issue dans le dépôt GitHub. #   b i b l i s e r v e r  
 