package com.bibliserver.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;

public class ToastUtil {
    public static void showToast(Scene scene, String message, boolean success) {
        Platform.runLater(() -> {
            Popup popup = new Popup();
            Label label = new Label(message);
            label.getStyleClass().add("toast");
            if (success) {
                label.setStyle("-fx-background-color: #21B573; -fx-text-fill: white; -fx-padding: 12 24 12 24; -fx-background-radius: 8; -fx-font-size: 15px;");
            } else {
                label.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 12 24 12 24; -fx-background-radius: 8; -fx-font-size: 15px;");
            }
            popup.getContent().add(label);
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            // Positionner le toast en bas au centre
            double x = scene.getWindow().getX() + (scene.getWidth() - label.getWidth()) / 2;
            double y = scene.getWindow().getY() + scene.getHeight() - 80;
            popup.show(scene.getWindow(), x, y);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), label);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            // Disparition automatique
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), label);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(2.5));
            fadeOut.setOnFinished(e -> popup.hide());
            fadeOut.play();
        });
    }
} 