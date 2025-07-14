package com.bibliserver.controller;

import com.bibliserver.dao.GroupDAO;
import com.bibliserver.model.Group;
import com.bibliserver.model.Privilege;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import com.bibliserver.util.ToastUtil;

public class GroupsController implements Initializable {
    
    @FXML private ListView<Group> groupsListView;
    @FXML private TextField groupNameField;
    @FXML private TextArea groupDescriptionArea;
    @FXML private TableView<PrivilegeWrapper> privilegesTable;
    @FXML private TableColumn<PrivilegeWrapper, String> privilegeNameColumn;
    @FXML private TableColumn<PrivilegeWrapper, String> privilegeDescriptionColumn;
    @FXML private TableColumn<PrivilegeWrapper, Boolean> privilegeGrantedColumn;
    
    private GroupDAO groupDAO;
    private Group currentGroup;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        groupDAO = new GroupDAO();
        
        // Configuration des colonnes
        privilegeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        privilegeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        privilegeGrantedColumn.setCellValueFactory(cellData -> cellData.getValue().grantedProperty());
        privilegeGrantedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(privilegeGrantedColumn));
        
        // Chargement des groupes
        loadGroups();
        
        // Listener pour la sélection d'un groupe
        groupsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentGroup = newVal;
                showGroupDetails(newVal);
            }
        });
    }
    
    private void loadGroups() {
        try {
            groupsListView.setItems(FXCollections.observableArrayList(groupDAO.findAll()));
        } catch (SQLException e) {
            showError("Erreur lors du chargement des groupes", e);
        }
    }
    
    private void showGroupDetails(Group group) {
        groupNameField.setText(group.getName());
        groupDescriptionArea.setText(group.getDescription());
        loadPrivileges(group);
    }
    
    private void loadPrivileges(Group group) {
        try {
            List<Privilege> privileges = groupDAO.getPrivileges(group.getId());
            List<PrivilegeWrapper> wrappers = new ArrayList<>();
            
            for (Privilege p : privileges) {
                boolean hasPrivilege = groupDAO.hasPrivilege(group.getId(), p.getId());
                wrappers.add(new PrivilegeWrapper(p, hasPrivilege));
            }
            
            privilegesTable.setItems(FXCollections.observableArrayList(wrappers));
        } catch (SQLException e) {
            showError("Erreur lors du chargement des privilèges", e);
        }
    }
    
    @FXML
    private void showAddGroupDialog() {
        currentGroup = new Group();
        groupNameField.clear();
        groupDescriptionArea.clear();
        privilegesTable.getItems().clear();
    }
    
    @FXML
    private void handleSave() {
        if (currentGroup == null) return;
        
        try {
            currentGroup.setName(groupNameField.getText());
            currentGroup.setDescription(groupDescriptionArea.getText());
            
            if (currentGroup.getId() == 0) {
                groupDAO.create(currentGroup);
                ToastUtil.showToast(groupNameField.getScene(), "Groupe ajouté avec succès", true);
            } else {
                groupDAO.update(currentGroup);
                ToastUtil.showToast(groupNameField.getScene(), "Groupe modifié avec succès", true);
            }
            
            // Mise à jour des privilèges
            for (PrivilegeWrapper wrapper : privilegesTable.getItems()) {
                if (wrapper.isGranted()) {
                    groupDAO.grantPrivilege(currentGroup.getId(), wrapper.getPrivilege().getId());
                } else {
                    groupDAO.revokePrivilege(currentGroup.getId(), wrapper.getPrivilege().getId());
                }
            }
            
            loadGroups();
            showInfo("Succès", "Le groupe a été enregistré avec succès.");
            
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement", e);
            ToastUtil.showToast(groupNameField.getScene(), "Erreur lors de l'enregistrement du groupe", false);
        }
    }
    
    @FXML
    private void handleDelete() {
        if (currentGroup == null || currentGroup.getId() == 0) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le groupe");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce groupe ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    groupDAO.delete(currentGroup.getId());
                    loadGroups();
                    showAddGroupDialog(); // Reset form
                    ToastUtil.showToast(groupNameField.getScene(), "Groupe supprimé avec succès", true);
                } catch (SQLException e) {
                    showError("Erreur lors de la suppression", e);
                    ToastUtil.showToast(groupNameField.getScene(), "Erreur lors de la suppression du groupe", false);
                }
            }
        });
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    private void showInfo(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Classe wrapper pour les privilèges avec une propriété observable pour la case à cocher
    private static class PrivilegeWrapper {
        private final Privilege privilege;
        private final BooleanProperty granted;
        
        public PrivilegeWrapper(Privilege privilege, boolean granted) {
            this.privilege = privilege;
            this.granted = new SimpleBooleanProperty(granted);
        }
        
        public Privilege getPrivilege() {
            return privilege;
        }
        
        public String getName() {
            return privilege.getName();
        }
        
        public String getDescription() {
            return privilege.getDescription();
        }
        
        public boolean isGranted() {
            return granted.get();
        }
        
        public BooleanProperty grantedProperty() {
            return granted;
        }
    }
} 