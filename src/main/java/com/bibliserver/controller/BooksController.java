package com.bibliserver.controller;

import com.bibliserver.dao.BookDAO;
import com.bibliserver.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert;
import com.bibliserver.util.ToastUtil;
import com.bibliserver.dao.MemoireDetailsDAO;
import com.bibliserver.model.MemoireDetails;
import javafx.beans.value.ChangeListener;
import com.bibliserver.dao.MediaDAO;
import com.bibliserver.model.Media;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar;
import com.bibliserver.dao.GenreDAO;
import com.bibliserver.model.Genre;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class BooksController {
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Book> booksTable;
    
    @FXML
    private TableColumn<Book, String> titleColumn;
    
    @FXML
    private TableColumn<Book, String> authorColumn;
    
    @FXML
    private TableColumn<Book, String> isbnColumn;
    
    @FXML
    private TableColumn<Book, String> publisherColumn;
    
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    
    @FXML
    private TableColumn<Book, Integer> quantityColumn;
    
    @FXML
    private TableColumn<Book, Integer> availableColumn;
    
    @FXML
    private Dialog<Book> bookDialog;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField authorField;
    
    @FXML
    private TextField isbnField;
    
    @FXML
    private TextField publisherField;
    
    @FXML
    private TextField yearField;
    
    @FXML
    private Spinner<Integer> quantitySpinner;
    
    @FXML
    private ComboBox<Genre> genreComboBox;
    @FXML
    private Label memoireUniversityLabel;
    @FXML
    private TextField memoireUniversityField;
    @FXML
    private Label memoireSupervisorLabel;
    @FXML
    private TextField memoireSupervisorField;
    @FXML
    private Label memoireYearLabel;
    @FXML
    private TextField memoireYearField;
    @FXML
    private Label memoireSubjectLabel;
    @FXML
    private TextField memoireSubjectField;
    @FXML private TableView<Media> mediaTableView;
    @FXML private TableColumn<Media, String> mediaTypeColumn;
    @FXML private TableColumn<Media, String> mediaTitleColumn;
    @FXML private TableColumn<Media, String> mediaDescriptionColumn;
    @FXML private TableColumn<Media, Void> mediaActionsColumn;
    @FXML private Button addMediaButton;
    @FXML private Button removeMediaButton;
    @FXML
    private Button manageGenresButton;

    private BookDAO bookDAO;
    private Book selectedBook;
    private MemoireDetailsDAO memoireDetailsDAO;
    private MediaDAO mediaDAO;
    private ObservableList<Media> mediaList = FXCollections.observableArrayList();
    private GenreDAO genreDAO;
    
    private static String initialFilter = null;
    public static void setInitialFilter(String filter) { initialFilter = filter; }
    
    public void initialize() {
        bookDAO = new BookDAO();
        memoireDetailsDAO = new MemoireDetailsDAO();
        mediaDAO = new MediaDAO();
        genreDAO = new GenreDAO();
        // Chargement dynamique des genres
        List<Genre> genres = genreDAO.getAllGenres();
        genreComboBox.setItems(FXCollections.observableArrayList(genres));
        // Affichage dynamique des champs mémoire
        genreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isMemoire = newVal != null && "Mémoire".equals(newVal.getName());
            memoireUniversityLabel.setVisible(isMemoire);
            memoireUniversityField.setVisible(isMemoire);
            memoireSupervisorLabel.setVisible(isMemoire);
            memoireSupervisorField.setVisible(isMemoire);
            memoireYearLabel.setVisible(isMemoire);
            memoireYearField.setVisible(isMemoire);
            memoireSubjectLabel.setVisible(isMemoire);
            memoireSubjectField.setVisible(isMemoire);
        });
        // TableView médias
        if (mediaTableView != null) {
            mediaTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            mediaTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            mediaDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            // Actions (supprimer)
            mediaActionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteBtn = new Button("Supprimer");
                {
                    deleteBtn.setOnAction(event -> {
                        Media media = getTableView().getItems().get(getIndex());
                        handleRemoveMedia(media);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            });
            mediaTableView.setItems(mediaList);
        }
        if (addMediaButton != null) {
            addMediaButton.setOnAction(e -> handleAddMedia());
        }
        if (removeMediaButton != null) {
            removeMediaButton.setOnAction(e -> {
                Media selected = mediaTableView.getSelectionModel().getSelectedItem();
                if (selected != null) handleRemoveMedia(selected);
            });
        }
        if (manageGenresButton != null) {
            manageGenresButton.setOnAction(e -> openManageGenresDialog());
        }
        
        // Ajout du resultConverter pour le Dialog<Book>
        bookDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String title = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    String isbn = isbnField.getText().trim();
                    String publisher = publisherField.getText().trim();
                    int year = Integer.parseInt(yearField.getText().trim());
                    int quantity = quantitySpinner.getValue();
                    Genre genre = genreComboBox.getValue();
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setIsbn(isbn);
                    book.setPublisher(publisher);
                    book.setPublicationYear(year);
                    book.setQuantity(quantity);
                    book.setAvailableQuantity(quantity);
                    book.setGenre(genre != null ? genre.getName() : null);
                    return book;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });
        
        setupTableColumns();
        loadBooks();
        if (initialFilter != null && searchField != null) {
            searchField.setText(initialFilter);
            initialFilter = null;
        }
    }
    
    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

        // Configuration de la colonne d'actions
        TableColumn<Book, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setMinWidth(200);
        // Largeur automatique, pas de setMinWidth/setPrefWidth/setMaxWidth/setResizable
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox buttons = new HBox(10, editButton, deleteButton);
            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconColor(Color.WHITE);
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().addAll("action-btn", "edit-btn");
                editButton.setTooltip(new Tooltip("Modifier ce livre"));
                editButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showEditBookDialog(book);
                });
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.WHITE);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().addAll("action-btn", "delete-btn");
                deleteButton.setTooltip(new Tooltip("Supprimer ce livre"));
                deleteButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleDeleteBook(book);
                });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
                setText(null);
            }
        });
        booksTable.getColumns().add(actionsColumn);

        // Rafraîchir la colonne quand la sélection change
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            booksTable.refresh();
        });

        // Configuration de la ligne de clic
        booksTable.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                final int index = row.getIndex();
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Book book = tv.getItems().get(index);
                    showEditBookDialog(book);
                }
            });
            return row;
        });

        // Binding pour colonnes flexibles
        booksTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue();
            double flexWidth = tableWidth / 8;
            titleColumn.setPrefWidth(flexWidth);
            authorColumn.setPrefWidth(flexWidth);
            isbnColumn.setPrefWidth(flexWidth);
            publisherColumn.setPrefWidth(flexWidth);
            yearColumn.setPrefWidth(flexWidth);
            quantityColumn.setPrefWidth(flexWidth);
            availableColumn.setPrefWidth(flexWidth);
            // Dernière colonne Actions
            booksTable.getColumns().get(booksTable.getColumns().size() - 1).setPrefWidth(flexWidth);
        });
    }
    
    private void loadBooks() {
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(bookDAO.findAll());
            booksTable.setItems(books);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des livres", e);
        }
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(
                bookDAO.searchBooks(query)
            );
            booksTable.setItems(books);
        } catch (SQLException e) {
            showError("Erreur lors de la recherche", e);
        }
    }
    
    @FXML
    private void showAddBookDialog() {
        selectedBook = null;
        clearDialogFields();
        bookDialog.setHeaderText("Ajouter un nouveau livre");
        mediaList.clear();
        Optional<Book> result = bookDialog.showAndWait();
        result.ifPresent(this::saveBook);
    }
    
    private void showEditBookDialog(Book book) {
        selectedBook = book;
        populateDialogFields(book);
        bookDialog.setHeaderText("Modifier le livre");
        // Charger les médias liés
        loadMediaForBook(book);
        Optional<Book> result = bookDialog.showAndWait();
        result.ifPresent(this::saveBook);
    }
    
    private void handleDeleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le livre");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce livre ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookDAO.delete(book.getId());
                loadBooks();
                ToastUtil.showToast(booksTable.getScene(), "Livre supprimé avec succès", true);
            } catch (SQLException e) {
                showError("Erreur lors de la suppression", e);
                ToastUtil.showToast(booksTable.getScene(), "Erreur lors de la suppression du livre", false);
            }
        }
    }
    
    private void saveBook(Book book) {
        try {
            if (selectedBook == null) {
                bookDAO.create(book);
                if ("Mémoire".equals(book.getGenre())) {
                    MemoireDetails memoire = new MemoireDetails();
                    memoire.setBookId(book.getId());
                    memoire.setUniversity(memoireUniversityField.getText().trim());
                    memoire.setSupervisor(memoireSupervisorField.getText().trim());
                    memoire.setYear(Integer.parseInt(memoireYearField.getText().trim()));
                    memoire.setSubject(memoireSubjectField.getText().trim());
                    memoireDetailsDAO.create(memoire);
                }
                ToastUtil.showToast(booksTable.getScene(), "Livre ajouté avec succès", true);
            } else {
                book.setId(selectedBook.getId());
                bookDAO.update(book);
                if ("Mémoire".equals(book.getGenre())) {
                    MemoireDetails memoire = memoireDetailsDAO.findByBookId(book.getId());
                    if (memoire == null) memoire = new MemoireDetails();
                    memoire.setBookId(book.getId());
                    memoire.setUniversity(memoireUniversityField.getText().trim());
                    memoire.setSupervisor(memoireSupervisorField.getText().trim());
                    memoire.setYear(Integer.parseInt(memoireYearField.getText().trim()));
                    memoire.setSubject(memoireSubjectField.getText().trim());
                    if (memoire.getId() == 0) memoireDetailsDAO.create(memoire);
                    else memoireDetailsDAO.update(memoire);
                } else {
                    memoireDetailsDAO.deleteByBookId(book.getId());
                }
                ToastUtil.showToast(booksTable.getScene(), "Livre modifié avec succès", true);
            }
            loadBooks();
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement", e);
            ToastUtil.showToast(booksTable.getScene(), "Erreur lors de l'enregistrement du livre", false);
        }
    }
    
    private void clearDialogFields() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        publisherField.clear();
        yearField.clear();
        quantitySpinner.getValueFactory().setValue(1);
        genreComboBox.getSelectionModel().clearSelection();
        memoireUniversityField.clear();
        memoireSupervisorField.clear();
        memoireYearField.clear();
        memoireSubjectField.clear();
    }
    
    private void populateDialogFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        publisherField.setText(book.getPublisher());
        yearField.setText(String.valueOf(book.getPublicationYear()));
        quantitySpinner.getValueFactory().setValue(book.getQuantity());
        // Sélectionner le genre correspondant
        if (book.getGenre() != null) {
            for (Genre g : genreComboBox.getItems()) {
                if (book.getGenre().equals(g.getName())) {
                    genreComboBox.setValue(g);
                    break;
                }
            }
        } else {
            genreComboBox.getSelectionModel().clearSelection();
        }
        if ("Mémoire".equals(book.getGenre())) {
            try {
                MemoireDetails memoire = memoireDetailsDAO.findByBookId(book.getId());
                if (memoire != null) {
                    memoireUniversityField.setText(memoire.getUniversity());
                    memoireSupervisorField.setText(memoire.getSupervisor());
                    memoireYearField.setText(String.valueOf(memoire.getYear()));
                    memoireSubjectField.setText(memoire.getSubject());
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void loadMediaForBook(Book book) {
        try {
            mediaList.clear();
            if (book != null && book.getId() > 0) {
                mediaList.addAll(mediaDAO.findByBookId(book.getId()));
            }
        } catch (Exception e) {
            // ignore
        }
    }
    private void handleAddMedia() {
        if (selectedBook == null || selectedBook.getId() == 0) {
            showError("Ajout impossible", new Exception("Enregistrez d'abord le livre avant d'ajouter un média."));
            return;
        }
        Dialog<Media> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un média");
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
                m.setBookId(selectedBook.getId());
                m.setIndependent(false);
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

    private void openManageGenresDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_genres.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des genres");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Rafraîchir la liste des genres après fermeture
            List<Genre> genres = genreDAO.getAllGenres();
            genreComboBox.setItems(FXCollections.observableArrayList(genres));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
} 