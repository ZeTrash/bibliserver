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
import com.bibliserver.dao.GroupDAO;
import com.bibliserver.model.Group;
import org.mindrot.jbcrypt.BCrypt;
import javafx.scene.layout.GridPane;
import com.bibliserver.util.ToastUtil;

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
    private GroupDAO groupDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        groupDAO = new GroupDAO();
        
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
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un utilisateur");
        dialog.setHeaderText("Saisissez les informations du nouvel utilisateur");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setId("usernameField");
        usernameField.setPromptText("Nom d'utilisateur");
        PasswordField passwordField = new PasswordField();
        passwordField.setId("passwordField");
        passwordField.setPromptText("Mot de passe");
        ComboBox<Group> groupComboBox = new ComboBox<>();
        try {
            groupComboBox.getItems().addAll(groupDAO.findAll());
        } catch (SQLException e) {
            showError("Erreur lors du chargement des groupes", e);
        }
        groupComboBox.setPromptText("Groupe");

        grid.add(new Label("Nom d'utilisateur :"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Mot de passe :"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Groupe :"), 0, 2);
        grid.add(groupComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                Group group = groupComboBox.getValue();
                if (username.isEmpty() || password.isEmpty() || group == null) {
                    showError("Champs manquants", new Exception("Veuillez remplir tous les champs."));
                    return null;
                }
                User user = new User();
                user.setUsername(username);
                user.setPasswordHash(password);
                user.setGroupId(group.getId());
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            try {
                userDAO.createUser(user.getUsername(), user.getPasswordHash(), user.getGroupId());
                loadUsers();
                showInfo("Succès", "Utilisateur ajouté avec succès.");
                ToastUtil.showToast(addButton.getScene(), "Utilisateur ajouté avec succès", true);
            } catch (com.bibliserver.dao.DuplicateUserException e) {
                showError("Nom d'utilisateur déjà utilisé", e);
                ToastUtil.showToast(addButton.getScene(), "Nom d'utilisateur déjà utilisé", false);
            } catch (SQLException e) {
                showError("Erreur lors de l'ajout de l'utilisateur", e);
                ToastUtil.showToast(addButton.getScene(), "Erreur lors de l'ajout de l'utilisateur", false);
            }
        });
    }
    
    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Modifier l'utilisateur");
            dialog.setHeaderText("Modifiez les informations de l'utilisateur");

            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField usernameField = new TextField(selectedUser.getUsername());
            usernameField.setPromptText("Nom d'utilisateur");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Nouveau mot de passe (laisser vide pour ne pas changer)");
            ComboBox<Group> groupComboBox = new ComboBox<>();
            try {
                groupComboBox.getItems().addAll(groupDAO.findAll());
                for (Group g : groupComboBox.getItems()) {
                    if (g.getId() == selectedUser.getGroupId()) {
                        groupComboBox.setValue(g);
                        break;
                    }
                }
            } catch (SQLException e) {
                showError("Erreur lors du chargement des groupes", e);
            }
            groupComboBox.setPromptText("Groupe");

            grid.add(new Label("Nom d'utilisateur :"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Nouveau mot de passe :"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Groupe :"), 0, 2);
            grid.add(groupComboBox, 1, 2);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String username = usernameField.getText().trim();
                    String password = passwordField.getText();
                    Group group = groupComboBox.getValue();
                    if (username.isEmpty() || group == null) {
                        showError("Champs manquants", new Exception("Veuillez remplir tous les champs obligatoires."));
                        return null;
                    }
                    User user = new User();
                    user.setId(selectedUser.getId());
                    user.setUsername(username);
                    user.setGroupId(group.getId());
                    user.setCreatedAt(selectedUser.getCreatedAt());
                    if (!password.isEmpty()) {
                        user.setPasswordHash(password);
                    } else {
                        user.setPasswordHash(null);
                    }
                    return user;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(user -> {
                try {
                    userDAO.updateUser(user);
                    if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                        userDAO.updatePassword(user.getId(), user.getPasswordHash());
                    }
                    loadUsers();
                    showInfo("Succès", "Utilisateur modifié avec succès.");
                    ToastUtil.showToast(editButton.getScene(), "Utilisateur modifié avec succès", true);
                } catch (SQLException e) {
                    showError("Erreur lors de la modification de l'utilisateur", e);
                    ToastUtil.showToast(editButton.getScene(), "Erreur lors de la modification de l'utilisateur", false);
                }
            });
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
                        ToastUtil.showToast(deleteButton.getScene(), "Utilisateur supprimé avec succès", true);
                    } catch (SQLException e) {
                        showError("Erreur lors de la suppression", e);
                        ToastUtil.showToast(deleteButton.getScene(), "Erreur lors de la suppression", false);
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