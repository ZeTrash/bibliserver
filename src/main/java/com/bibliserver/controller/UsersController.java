package com.bibliserver.controller;

import com.bibliserver.dao.UserDAO;
import com.bibliserver.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> groupNameColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        createdAtColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String date = cellData.getValue().getCreatedAt().format(formatter);
            return javafx.beans.binding.Bindings.createStringBinding(() -> date);
        });
        
        // Chargement des données
        loadUsers();
        
        // Activation/désactivation des boutons selon la sélection
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }
    
    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(userDAO.findAll()));
        } catch (SQLException e) {
            showError("Erreur lors du chargement des utilisateurs", e);
        }
    }
    
    @FXML
    private void handleAddUser() {
        // TODO: Implémenter l'ajout d'utilisateur
        showInfo("Non implémenté", "La fonction d'ajout d'utilisateur sera bientôt disponible.");
    }
    
    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // TODO: Implémenter la modification d'utilisateur
            showInfo("Non implémenté", "La fonction de modification d'utilisateur sera bientôt disponible.");
        }
    }
    
    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'utilisateur");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + selectedUser.getUsername() + " ?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        userDAO.deleteUser(selectedUser.getId());
                        loadUsers();
                    } catch (SQLException e) {
                        showError("Erreur lors de la suppression", e);
                    }
                }
            });
        }
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 