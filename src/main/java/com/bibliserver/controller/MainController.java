package com.bibliserver.controller;

import com.bibliserver.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import com.bibliserver.util.SecurityUtil;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.Alert;

public class MainController {
    private static MainController instance;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Button usersButton;
    
    @FXML
    private Button groupsButton;
    
    @FXML
    private Label currentUserLabel;
    
    @FXML
    private Button booksButton;
    
    @FXML
    private Button loansButton;
    
    @FXML
    private Button themeToggleButton;
    @FXML
    private FontIcon themeIcon;
    
    @FXML
    private Button mediaButton;
    
    private User currentUser;
    
    private boolean darkMode = false;
    
    @FXML
    public void initialize() {
        instance = this;
        // Gestion du bouton de thème
        if (themeToggleButton != null) {
            themeToggleButton.setOnAction(e -> toggleTheme());
        }
        updateThemeIcon();
        // Cette méthode est appelée automatiquement après le chargement du FXML
        showDashboard();
    }
    
    public static MainController getInstance() {
        return instance;
    }
    
    public Button getBooksButton() {
        return booksButton;
    }
    
    public Button getLoansButton() {
        return loansButton;
    }
    
    public Button getUsersButton() {
        return usersButton;
    }
    
    public Button getGroupsButton() {
        return groupsButton;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        currentUserLabel.setText("Connecté en tant que: " + user.getUsername());
        
        // Mise à jour de la visibilité des boutons selon les privilèges
        booksButton.setVisible(SecurityUtil.hasPermission(user, "BOOK", "READ"));
        loansButton.setVisible(SecurityUtil.hasPermission(user, "LOAN", "READ"));
        usersButton.setVisible(SecurityUtil.hasPermission(user, "USER", "READ"));
        groupsButton.setVisible(SecurityUtil.hasPermission(user, "GROUP", "READ"));
    }
    
    @FXML
    private void showDashboard() {
        loadView("/fxml/dashboard.fxml");
    }
    
    public void showBooks() {
        loadView("/fxml/books.fxml");
    }
    
    public void showLoans() {
        loadView("/fxml/loans.fxml");
    }
    
    public void showUsers() {
        if ("Administrateurs".equals(currentUser.getGroupName())) {
            loadView("/fxml/users.fxml");
        }
    }
    
    @FXML
    private void showGroups() {
        loadView("/fxml/groups.fxml");
    }
    
    @FXML
    private void showMedia() {
        if (currentUser != null && com.bibliserver.util.SecurityUtil.hasPermission(currentUser, "MEDIA", "READ")) {
            loadView("/fxml/media.fxml");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Accès refusé");
            alert.setHeaderText("Droits insuffisants");
            alert.setContentText("Vous n'avez pas la permission d'accéder à la gestion des médias.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            // Charger la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Obtenir la scène actuelle
            Scene scene = contentArea.getScene();
            Stage stage = (Stage) scene.getWindow();
            
            // Remplacer le contenu par la vue de connexion
            Scene loginScene = new Scene(root);
            loginScene.getStylesheets().add(getClass().getResource("/fxml/style-light.css").toExternalForm());
            stage.setScene(loginScene);
            stage.setTitle("Connexion - Gestion de Bibliothèque");
            stage.setMaximized(false); // Revenir à la taille normale sur l'écran de login
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Afficher une alerte d'erreur
        }
    }
    
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Vider et ajouter la nouvelle vue
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Afficher une alerte d'erreur
        }
    }

    private void toggleTheme() {
        Scene scene = themeToggleButton.getScene();
        if (scene == null) return;
        String light = getClass().getResource("/fxml/style-light.css").toExternalForm();
        String dark = getClass().getResource("/fxml/style-dark.css").toExternalForm();
        if (!darkMode) {
            scene.getStylesheets().remove(light);
            if (!scene.getStylesheets().contains(dark)) {
                scene.getStylesheets().add(dark);
            }
            darkMode = true;
        } else {
            scene.getStylesheets().remove(dark);
            if (!scene.getStylesheets().contains(light)) {
                scene.getStylesheets().add(light);
            }
            darkMode = false;
        }
        updateThemeIcon();
    }

    private void updateThemeIcon() {
        if (themeIcon != null) {
            if (darkMode) {
                themeIcon.setIconLiteral("fas-sun");
            } else {
                themeIcon.setIconLiteral("fas-moon");
            }
        }
    }

    public void showLoansWithFilter(String filter) {
        LoansController.setInitialFilter(filter);
        loadView("/fxml/loans.fxml");
    }

    public void showBooksWithFilter(String filter) {
        BooksController.setInitialFilter(filter);
        loadView("/fxml/books.fxml");
    }
    public void showUsersWithFilter(String filter) {
        UsersController.setInitialFilter(filter);
        loadView("/fxml/users.fxml");
    }
} 