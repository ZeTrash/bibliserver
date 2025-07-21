package com.bibliserver.controller;

import com.bibliserver.dao.MediaDAO;
import com.bibliserver.model.Media;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;

public class MediaController {
    @FXML private TextField searchField;
    @FXML private TableView<Media> mediaTable;
    @FXML private TableColumn<Media, String> typeColumn;
    @FXML private TableColumn<Media, String> titleColumn;
    @FXML private TableColumn<Media, String> descriptionColumn;
    @FXML private TableColumn<Media, String> createdAtColumn;
    @FXML private TableColumn<Media, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableColumn<Media, String> bookTitleColumn;

    private MediaDAO mediaDAO;
    private ObservableList<Media> mediaList = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        mediaDAO = new MediaDAO();
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        createdAtColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null)
                return new ReadOnlyStringWrapper(cellData.getValue().getCreatedAt().format(dateFormatter));
            else
                return new ReadOnlyStringWrapper("");
        });
        // Largeur automatique pour actionsColumn
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button();
            private final HBox buttons = new HBox(10, deleteBtn);
            {
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.WHITE);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.getStyleClass().addAll("action-btn", "delete-btn");
                deleteBtn.setTooltip(new Tooltip("Supprimer ce média"));
                deleteBtn.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    handleRemoveMedia(media);
                });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        actionsColumn.setMinWidth(200);
        mediaTable.setItems(mediaList);
        addButton.setOnAction(e -> handleAddMedia());
        removeButton.setOnAction(e -> {
            Media selected = mediaTable.getSelectionModel().getSelectedItem();
            if (selected != null) handleRemoveMedia(selected);
        });
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadMedia());
        loadMedia();
    }

    private void loadMedia() {
        try {
            mediaList.clear();
            String filter = filterComboBox.getValue();
            if (filter == null || filter.equals("Tous")) {
                mediaList.addAll(mediaDAO.findAllWithBook());
            } else if (filter.equals("Associés à un livre")) {
                mediaList.addAll(mediaDAO.findByAssociation(true));
            } else if (filter.equals("Indépendants")) {
                mediaList.addAll(mediaDAO.findByAssociation(false));
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement des médias", e);
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        try {
            mediaList.clear();
            for (Media m : mediaDAO.findIndependents()) {
                if (m.getTitle() != null && m.getTitle().toLowerCase().contains(query))
                    mediaList.add(m);
                else if (m.getType() != null && m.getType().toLowerCase().contains(query))
                    mediaList.add(m);
                else if (m.getDescription() != null && m.getDescription().toLowerCase().contains(query))
                    mediaList.add(m);
            }
        } catch (Exception e) {
            showError("Erreur lors de la recherche", e);
        }
    }

    private void handleAddMedia() {
        Dialog<Media> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un média indépendant");
        dialog.setHeaderText("Informations du média");
        ButtonType addBtn = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField typeField = new TextField();
        typeField.setPromptText("Type (CD, DVD, etc.)");
        TextField titleField = new TextField();
        titleField.setPromptText("Titre du média");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        grid.add(new Label("Type :"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Titre :"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description :"), 0, 2);
        grid.add(descField, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                Media m = new Media();
                m.setType(typeField.getText().trim());
                m.setTitle(titleField.getText().trim());
                m.setDescription(descField.getText().trim());
                m.setBookId(null);
                m.setIndependent(true);
                return m;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(media -> {
            try {
                mediaDAO.create(media);
                mediaList.add(media);
            } catch (Exception e) {
                showError("Erreur lors de l'ajout du média", e);
            }
        });
    }

    private void handleRemoveMedia(Media media) {
        try {
            mediaDAO.delete(media.getId());
            mediaList.remove(media);
        } catch (Exception e) {
            showError("Erreur lors de la suppression du média", e);
        }
    }

    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 