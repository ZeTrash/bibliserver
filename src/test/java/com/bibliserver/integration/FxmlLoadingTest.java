package com.bibliserver.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.Test;

import java.net.URL;
import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class FxmlLoadingTest {

    private static final AtomicBoolean javafxInitialized = new AtomicBoolean(false);

    @Test
    void testMainFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/main.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier main.fxml ne se charge pas correctement !");
    }

    @Test
    void testBooksFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/books.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier books.fxml ne se charge pas correctement !");
    }

    @Test
    void testDashboardFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/dashboard.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier dashboard.fxml ne se charge pas correctement !");
    }

    @Test
    void testGroupsFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/groups.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier groups.fxml ne se charge pas correctement !");
    }

    @Test
    void testLoansFxmlLoads() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> exceptionRef = new AtomicReference<>();

        Runnable loadTask = () -> {
            try {
                URL fxml = getClass().getResource("/fxml/loans.fxml");
                FXMLLoader loader = new FXMLLoader(fxml);
                Parent root = loader.load();
            } catch (Throwable t) {
                exceptionRef.set(t);
            } finally {
                latch.countDown();
            }
        };

        if (javafxInitialized.compareAndSet(false, true)) {
            Platform.startup(loadTask);
        } else {
            Platform.runLater(loadTask);
        }

        latch.await();
        if (exceptionRef.get() != null) {
            throw new AssertionError("Le fichier loans.fxml ne se charge pas correctement !", exceptionRef.get());
        }
    }

    @Test
    void testLoginFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/login.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier login.fxml ne se charge pas correctement !");
    }

    @Test
    void testUsersFxmlLoads() {
        assertDoesNotThrow(() -> {
            URL fxml = getClass().getResource("/fxml/users.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
        }, "Le fichier users.fxml ne se charge pas correctement !");
    }
} 