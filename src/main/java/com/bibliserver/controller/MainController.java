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
    
    private User currentUser;
    
    @FXML
    public void initialize() {
        instance = this;
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
    
    @FXML
    private void showBooks() {
        loadView("/fxml/books.fxml");
    }
    
    @FXML
    private void showLoans() {
        loadView("/fxml/loans.fxml");
    }
    
    @FXML
    private void showUsers() {
        if ("Administrateurs".equals(currentUser.getGroupName())) {
            loadView("/fxml/users.fxml");
        }
    }
    
    @FXML
    private void showGroups() {
        loadView("/fxml/groups.fxml");
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
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Gestion de Bibliothèque");
            
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
} 