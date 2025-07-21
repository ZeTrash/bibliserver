package com.bibliserver.controller;

import com.bibliserver.dao.GenreDAO;
import com.bibliserver.model.Genre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ManageGenresController {
    @FXML private TableView<Genre> genresTable;
    @FXML private TableColumn<Genre, String> nameColumn;
    @FXML private TextField newGenreField;
    @FXML private Button addGenreButton;
    @FXML private Button deleteGenreButton;
    @FXML private Button closeButton;

    private GenreDAO genreDAO;
    private ObservableList<Genre> genresList;

    @FXML
    public void initialize() {
        genreDAO = new GenreDAO();
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        refreshGenres();
        addGenreButton.setOnAction(this::handleAddGenre);
        deleteGenreButton.setOnAction(this::handleDeleteGenre);
        closeButton.setOnAction(e -> closeWindow());
    }

    private void refreshGenres() {
        genresList = FXCollections.observableArrayList(genreDAO.getAllGenres());
        genresTable.setItems(genresList);
    }

    private void handleAddGenre(ActionEvent event) {
        String name = newGenreField.getText().trim();
        if (!name.isEmpty()) {
            genreDAO.addGenre(name);
            newGenreField.clear();
            refreshGenres();
        }
    }

    private void handleDeleteGenre(ActionEvent event) {
        Genre selected = genresTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            genreDAO.deleteGenre(selected.getId());
            refreshGenres();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
} 