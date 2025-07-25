package com.bibliserver.controller;

import com.bibliserver.dao.BookDAO;
import com.bibliserver.dao.LoanDAO;
import com.bibliserver.dao.UserDAO;
import com.bibliserver.model.Book;
import com.bibliserver.model.Loan;
import com.bibliserver.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import com.bibliserver.util.ToastUtil;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class LoansController {
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<Loan> loansTable;
    
    @FXML
    private TableColumn<Loan, String> bookTitleColumn;
    
    @FXML
    private TableColumn<Loan, String> userNameColumn;
    
    @FXML
    private TableColumn<Loan, LocalDate> loanDateColumn;
    
    @FXML
    private TableColumn<Loan, LocalDate> dueDateColumn;
    
    @FXML
    private TableColumn<Loan, String> statusColumn;
    
    @FXML
    private Dialog<Loan> loanDialog;
    
    @FXML
    private ComboBox<Book> bookComboBox;
    
    @FXML
    private ComboBox<User> userComboBox;
    
    @FXML
    private DatePicker loanDatePicker;
    
    @FXML
    private Spinner<Integer> durationSpinner;
    
    @FXML
    private Label dueDateLabel;
    
        @FXML
    private Dialog<ButtonType> returnDialog;

    @FXML
    private Label returnBookInfoLabel;
    
    @FXML
    private Label returnUserInfoLabel;
    
    @FXML
    private CheckBox damageCheckBox;
    
    @FXML
    private TextArea damageNotesArea;
    
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private DateTimeFormatter dateFormatter;
    
    private static String initialFilter = null;
    public static void setInitialFilter(String filter) { initialFilter = filter; }
    
    public void initialize() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        setupTableColumns();
        setupDialogs();
        setupFilterComboBox();
        if (initialFilter != null && filterComboBox != null) {
            filterComboBox.setValue(initialFilter);
            initialFilter = null;
        }
        loadLoans();
    }
    
    private void setupTableColumns() {
        bookTitleColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getBook().getTitle()
            )
        );
        
        userNameColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getUser().getUsername()
            )
        );
        
        loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configuration de la colonne d'actions
        TableColumn<Loan, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setMinWidth(200);
        // Largeur automatique, pas de setMinWidth/setPrefWidth/setMaxWidth/setResizable
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnButton = new Button();
            private final HBox buttons = new HBox(10, returnButton);
            {
                FontIcon returnIcon = new FontIcon("fas-undo");
                returnIcon.setIconColor(Color.WHITE);
                returnButton.setGraphic(returnIcon);
                returnButton.getStyleClass().addAll("action-btn", "edit-btn");
                returnButton.setTooltip(new Tooltip("Enregistrer le retour de ce livre"));
                returnButton.setOnAction(event -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    showReturnDialog(loan);
                });
                buttons.setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Loan loan = getIndex() >= 0 && getIndex() < getTableView().getItems().size() ? getTableView().getItems().get(getIndex()) : null;
                setGraphic((empty || loan == null || "RETURNED".equals(loan.getStatus())) ? null : buttons);
                setText(null);
            }
        });
        loansTable.getColumns().add(actionsColumn);

        // Rafraîchir la colonne quand la sélection change
        loansTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            loansTable.refresh();
        });

        // Configuration de la ligne de clic
        loansTable.setRowFactory(param -> {
            TableRow<Loan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Loan loan = row.getItem();
                if (event.getClickCount() == 2 && !row.isEmpty() && loan != null && !"RETURNED".equals(loan.getStatus())) {
                    showReturnDialog(loan);
                }
            });
            return row;
        });

        // Binding pour colonnes flexibles
        loansTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue();
            double flexWidth = tableWidth / 6;
            bookTitleColumn.setPrefWidth(flexWidth);
            userNameColumn.setPrefWidth(flexWidth);
            loanDateColumn.setPrefWidth(flexWidth);
            dueDateColumn.setPrefWidth(flexWidth);
            statusColumn.setPrefWidth(flexWidth);
            // Dernière colonne Actions
            loansTable.getColumns().get(loansTable.getColumns().size() - 1).setPrefWidth(flexWidth);
        });
    }
    
    private void setupDialogs() {
        // Configuration du dialogue de nouvel emprunt
        loanDialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Book selectedBook = bookComboBox.getValue();
                User selectedUser = userComboBox.getValue();
                LocalDate loanDate = loanDatePicker.getValue();
                int duration = durationSpinner.getValue();
                
                if (selectedBook != null && selectedUser != null && loanDate != null) {
                    return new Loan(selectedBook, selectedUser, loanDate, loanDate.plusDays(duration));
                }
            }
            return null;
        });
        
        // Mise à jour automatique de la date de retour prévue
        durationSpinner.valueProperty().addListener((obs, oldVal, newVal) -> 
            updateDueDateLabel()
        );
        
        loanDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> 
            updateDueDateLabel()
        );
        
        // Configuration du dialogue de retour
        damageCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> 
            damageNotesArea.setVisible(newVal)
        );
    }
    
    private void setupFilterComboBox() {
        filterComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> loadLoans()
        );
    }
    
    private void loadLoans() {
        try {
            List<Loan> allLoans = loanDAO.findActiveLoans();
            String filter = filterComboBox.getValue();
            
            ObservableList<Loan> filteredLoans = FXCollections.observableArrayList();
            for (Loan loan : allLoans) {
                if (filter == null || filter.equals("Tous") ||
                    (filter.equals("En cours") && loan.getStatus().equals("ACTIVE")) ||
                    (filter.equals("En retard") && loan.isOverdue()) ||
                    (filter.equals("Retournés") && loan.getStatus().equals("RETURNED"))) {
                    filteredLoans.add(loan);
                }
            }
            
            loansTable.setItems(filteredLoans);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des emprunts", e);
        }
    }
    
    @FXML
    private void showNewLoanDialog() {
        try {
            // Charger les livres disponibles
            ObservableList<Book> availableBooks = FXCollections.observableArrayList();
            for (Book book : bookDAO.findAll()) {
                if (book.getAvailableQuantity() > 0) {
                    availableBooks.add(book);
                }
            }
            bookComboBox.setItems(availableBooks);
            // Charger les utilisateurs
            userComboBox.setItems(FXCollections.observableArrayList(userDAO.findAll()));
            // Initialiser les valeurs par défaut
            loanDatePicker.setValue(LocalDate.now());
            durationSpinner.getValueFactory().setValue(14);
            updateDueDateLabel();
            Optional<Loan> result = loanDialog.showAndWait();
            result.ifPresent(loan -> {
                if (loan.getBook().getGenre() != null && loan.getBook().getGenre().equals("Mémoire")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Emprunt impossible");
                    alert.setHeaderText("Ce livre est un mémoire");
                    alert.setContentText("Les mémoires ne peuvent pas être empruntés.");
                    alert.showAndWait();
                } else {
                    saveLoan(loan);
                }
            });
        } catch (SQLException e) {
            showError("Erreur lors de la préparation du dialogue", e);
        }
    }
    
    private void showReturnDialog(Loan loan) {
        returnBookInfoLabel.setText("Livre : " + loan.getBook().getTitle());
        returnUserInfoLabel.setText("Emprunteur : " + loan.getUser().getUsername());
        damageCheckBox.setSelected(false);
        damageNotesArea.clear();
        
        Optional<ButtonType> result = returnDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                loanDAO.returnBook(loan.getId());
                loadLoans();
                ToastUtil.showToast(loansTable.getScene(), "Livre retourné avec succès", true);
            } catch (SQLException e) {
                showError("Erreur lors du retour du livre", e);
                ToastUtil.showToast(loansTable.getScene(), "Erreur lors du retour du livre", false);
            }
        }
    }
    
    private void saveLoan(Loan loan) {
        try {
            loanDAO.create(loan);
            loadLoans();
            ToastUtil.showToast(loansTable.getScene(), "Emprunt enregistré avec succès", true);
        } catch (SQLException e) {
            showError("Erreur lors de l'enregistrement de l'emprunt", e);
            ToastUtil.showToast(loansTable.getScene(), "Erreur lors de l'enregistrement de l'emprunt", false);
        }
    }
    
    private void updateDueDateLabel() {
        LocalDate loanDate = loanDatePicker.getValue();
        Integer duration = durationSpinner.getValue();
        
        if (loanDate != null && duration != null) {
            LocalDate dueDate = loanDate.plusDays(duration);
            dueDateLabel.setText(dueDate.format(dateFormatter));
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
