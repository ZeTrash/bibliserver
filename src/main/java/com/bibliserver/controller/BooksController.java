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
    private TableColumn<Book, Void> actionsColumn;
    
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
    
    private BookDAO bookDAO;
    private Book selectedBook;
    
    public void initialize() {
        bookDAO = new BookDAO();
        
        setupTableColumns();
        loadBooks();
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
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showEditBookDialog(book);
                });
                
                deleteButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    handleDeleteBook(book);
                });
                
                deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
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
        
        Optional<Book> result = bookDialog.showAndWait();
        result.ifPresent(this::saveBook);
    }
    
    private void showEditBookDialog(Book book) {
        selectedBook = book;
        populateDialogFields(book);
        bookDialog.setHeaderText("Modifier le livre");
        
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
                ToastUtil.showToast(booksTable.getScene(), "Livre ajouté avec succès", true);
            } else {
                book.setId(selectedBook.getId());
                bookDAO.update(book);
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
    }
    
    private void populateDialogFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        publisherField.setText(book.getPublisher());
        yearField.setText(String.valueOf(book.getPublicationYear()));
        quantitySpinner.getValueFactory().setValue(book.getQuantity());
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 