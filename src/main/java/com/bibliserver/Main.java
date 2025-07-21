package com.bibliserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/fxml/style-light.css").toExternalForm());
        primaryStage.setTitle("Gestion de Bibliothèque");
        primaryStage.setScene(scene);
        // primaryStage.setMaximized(true); // Suppression du plein écran au démarrage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 