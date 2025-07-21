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
import com.bibliserver.dao.GroupDAO;
import com.bibliserver.model.User;
import org.mindrot.jbcrypt.BCrypt;
import com.bibliserver.util.SecurityUtil;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label messageLabel;
    
    private UserDAO userDAO;
    private GroupDAO groupDAO;
    
    public void initialize() {
        userDAO = new UserDAO();
        groupDAO = new GroupDAO();
        SecurityUtil.initialize(groupDAO);
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
                if (user != null && userDAO.validateUser(username, password)) {
                    loadMainView(user);
                } else {
                    messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
                }
            } else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (Exception e) {
            try {
                DatabaseUtil.getConnection();
                if (userDAO.validateUser(username, password)) {
                    User user = userDAO.findByUsername(username);
                    if (user != null && userDAO.validateUser(username, password)) {
                        loadMainView(user);
                    } else {
                        messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
                    }
                } else {
                    messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect");
                }
            } catch (Exception ex) {
                messageLabel.setText("Erreur de connexion à la base de données");
                e.printStackTrace();
                ex.printStackTrace();
            }
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
            Scene mainScene = new Scene(root);
            mainScene.getStylesheets().add(getClass().getResource("/fxml/style-light.css").toExternalForm());
            stage.setScene(mainScene);
            stage.setTitle("Gestion de Bibliothèque");
            stage.setMaximized(true); // Maximiser uniquement après connexion
        } catch (Exception e) {
            messageLabel.setText("Erreur lors du chargement de l'application");
            e.printStackTrace();
        }
    }
} 