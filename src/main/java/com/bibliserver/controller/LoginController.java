package com.bibliserver.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.bibliserver.util.DatabaseUtil;
import com.bibliserver.dao.UserDAO;
import com.bibliserver.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label messageLabel;
    
    private UserDAO userDAO;
    
    public void initialize() {
        userDAO = new UserDAO();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        try {
            if (userDAO.validateUser(username, password)) {
                User user = userDAO.findByUsername(username);
                loadMainView(user);
            } else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }
    
    private void loadMainView(User user) {
        try {
            // Charger la vue principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // Obtenir le contrôleur et définir l'utilisateur courant
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);
            
            // Obtenir la scène actuelle
            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            
            // Remplacer le contenu par la vue principale
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion de Bibliothèque");
            
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement de l'application");
            e.printStackTrace();
        }
    }
} 