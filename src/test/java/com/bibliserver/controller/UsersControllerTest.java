package com.bibliserver.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.control.TableView;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.matcher.base.NodeMatchers;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import java.util.concurrent.TimeUnit;

public class UsersControllerTest extends ApplicationTest {
    private UsersController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/users.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void testAddButtonExists() {
        Button addButton = lookup("#addButton").query();
        assertNotNull(addButton);
        assertEquals("➕ Ajouter un utilisateur", addButton.getText());
    }

    @Test
    public void testAddUserUI() throws java.util.concurrent.TimeoutException {
        // S'assurer qu'il y a au moins un groupe en base pour le test UI
        try {
            com.bibliserver.dao.GroupDAO groupDAO = new com.bibliserver.dao.GroupDAO();
            if (groupDAO.findAll().isEmpty()) {
                groupDAO.create(new com.bibliserver.model.Group("TestGroup", "Groupe de test"));
            }
        } catch (java.sql.SQLException e) {
            throw new AssertionError("Erreur lors de la préparation du groupe de test", e);
        }
        // Simule un clic sur le bouton d'ajout
        clickOn("#addButton");
        // Attente explicite pour que le champ soit visible
        verifyThat("#usernameField", NodeMatchers.isVisible());
        // Remplit le champ username
        clickOn("#usernameField");
        write("nouvelutilisateur");
        // Remplit le champ mot de passe
        clickOn("#passwordField");
        write("motdepasse123");
        // Sélectionne le premier groupe dans la comboBox
        clickOn(".combo-box");
        type(javafx.scene.input.KeyCode.DOWN);
        type(javafx.scene.input.KeyCode.ENTER);
        // Clique sur le bouton 'Ajouter' du dialogue
        clickOn("Ajouter");
        // Attente explicite que l'utilisateur apparaisse dans la table
        waitFor(5, TimeUnit.SECONDS, () -> {
            TableView usersTable = lookup("#usersTable").query();
            return usersTable.getItems().stream()
                .anyMatch(u -> ((com.bibliserver.model.User)u).getUsername().equals("nouvelutilisateur"));
        });
        // Vérifie que l'utilisateur apparaît dans la table
        TableView usersTable = lookup("#usersTable").query();
        assertTrue(usersTable.getItems().stream().anyMatch(u -> ((com.bibliserver.model.User)u).getUsername().equals("nouvelutilisateur")));
    }
} 