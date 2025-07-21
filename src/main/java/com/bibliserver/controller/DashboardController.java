package com.bibliserver.controller;

import com.bibliserver.dao.BookDAO;
import com.bibliserver.dao.LoanDAO;
import com.bibliserver.dao.UserDAO;
import com.bibliserver.model.Book;
import com.bibliserver.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.beans.property.SimpleStringProperty;
import com.bibliserver.model.User;
import javafx.scene.control.Tooltip;

public class DashboardController {
    @FXML
    private Label totalBooksLabel;
    
    @FXML
    private Label availableBooksLabel;
    
    @FXML
    private Label activeLoansLabel;
    
    @FXML
    private Label overdueLoansLabel;
    
    @FXML
    private TableView<Loan> recentLoansTable;
    
    @FXML
    private TableColumn<Loan, String> bookTitleColumn;
    
    @FXML
    private TableColumn<Loan, String> userNameColumn;
    
    @FXML
    private TableColumn<Loan, String> loanDateColumn;
    
    @FXML
    private TableColumn<Loan, String> dueDateColumn;
    
    @FXML
    private TableView<PopularBook> popularBooksTable;
    
    @FXML
    private TableColumn<PopularBook, String> popularBookTitleColumn;
    
    @FXML
    private TableColumn<PopularBook, String> authorColumn;
    
    @FXML
    private TableColumn<PopularBook, Integer> loanCountColumn;
    
    @FXML
    private PieChart statsPieChart;

    @FXML
    private ListView<String> activityListView;
    
    @FXML
    private Label totalUsersLabel;
    
    private BookDAO bookDAO;
    private LoanDAO loanDAO;
    private UserDAO userDAO;
    private DateTimeFormatter dateFormatter;
    
    public void initialize() {
        bookDAO = new BookDAO();
        loanDAO = new LoanDAO();
        userDAO = new UserDAO();
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        setupTableColumns();
        System.out.println("Colonne titre = " + bookTitleColumn);
        System.out.println("TableView = " + recentLoansTable);
        loadStatistics();
        loadRecentLoans();
        loadPopularBooks();
        loadPieChart();
        loadActivityList();
    }
    
    private void setupTableColumns() {
        bookTitleColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getBook() != null ? cellData.getValue().getBook().getTitle() : ""
            )
        );
        userNameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getUser() != null ? cellData.getValue().getUser().getUsername() : ""
            )
        );
        loanDateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getLoanDate() != null ? cellData.getValue().getLoanDate().toString() : ""
            )
        );
        dueDateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getDueDate() != null ? cellData.getValue().getDueDate().toString() : ""
            )
        );
        // Configuration des colonnes pour les livres populaires
        popularBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        loanCountColumn.setCellValueFactory(new PropertyValueFactory<>("loanCount"));
    }
    
    private void loadStatistics() {
        try {
            // Charger les statistiques des livres
            int totalBooks = 0;
            int availableBooks = 0;
            for (Book book : bookDAO.findAll()) {
                totalBooks += book.getQuantity();
                availableBooks += book.getAvailableQuantity();
            }
            totalBooksLabel.setText(String.valueOf(totalBooks));
            availableBooksLabel.setText(String.valueOf(availableBooks));
            
            // Charger les statistiques des emprunts
            int activeLoans = 0;
            int overdueLoans = 0;
            for (Loan loan : loanDAO.findActiveLoans()) {
                activeLoans++;
                if (loan.isOverdue()) {
                    overdueLoans++;
                }
            }
            activeLoansLabel.setText(String.valueOf(activeLoans));
            overdueLoansLabel.setText(String.valueOf(overdueLoans));

            // Charger le nombre total d'utilisateurs
            int totalUsers = userDAO.countAll();
            if (totalUsersLabel != null) {
                totalUsersLabel.setText(String.valueOf(totalUsers));
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des statistiques", e);
        }
    }
    
    private void loadRecentLoans() {
        try {
            ObservableList<Loan> recentLoans = FXCollections.observableArrayList(
                loanDAO.findRecentLoans(10) // tous statuts
            );
            recentLoansTable.setItems(recentLoans);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des emprunts récents", e);
        }
    }
    
    private void loadPopularBooks() {
        try {
            var books = bookDAO.findAll();
            var loans = loanDAO.findAll(); // tous statuts
            java.util.Map<Integer, Integer> loanCountMap = new java.util.HashMap<>();
            for (var loan : loans) {
                int bookId = loan.getBook().getId();
                loanCountMap.put(bookId, loanCountMap.getOrDefault(bookId, 0) + 1);
            }
            java.util.List<PopularBook> popularBooks = new java.util.ArrayList<>();
            for (var book : books) {
                int count = loanCountMap.getOrDefault(book.getId(), 0);
                if (count > 0) {
                    popularBooks.add(new PopularBook(book.getTitle(), book.getAuthor(), count));
                }
            }
            popularBooks.sort((a, b) -> Integer.compare(b.getLoanCount(), a.getLoanCount()));
            popularBooksTable.setItems(FXCollections.observableArrayList(popularBooks));
        } catch (Exception e) {
            showError("Erreur lors du chargement des livres populaires", e);
        }
    }
    
    private void loadPieChart() {
        try {
            var books = bookDAO.findAll();
            java.util.Map<String, Integer> genreCount = new java.util.HashMap<>();
            for (Book book : books) {
                String genre = book.getGenre() != null ? book.getGenre() : "Inconnu";
                genreCount.put(genre, genreCount.getOrDefault(genre, 0) + book.getQuantity());
            }
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (var entry : genreCount.entrySet()) {
                pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            statsPieChart.setData(pieData);
            statsPieChart.setTitle("Livres par genre");
            statsPieChart.setLegendVisible(true);
            statsPieChart.setLabelsVisible(true);
            statsPieChart.setPrefHeight(320); // Agrandir le graphique
            statsPieChart.setPrefWidth(320);
            // Tooltips et labels personnalisés
            for (PieChart.Data data : pieData) {
                String label = data.getName() + " (" + (int)data.getPieValue() + ")";
                Tooltip tooltip = new Tooltip(label);
                Tooltip.install(data.getNode(), tooltip);
                // Affichage personnalisé du label sur la part
                data.nameProperty().set(label);
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement du graphique", e);
        }
    }

    private void loadActivityList() {
        try {
            var loans = loanDAO.findRecentLoans(10); // Méthode à ajouter dans LoanDAO si besoin
            ObservableList<String> activities = FXCollections.observableArrayList();
            for (Loan loan : loans) {
                String action = loan.isReturned() ? "Retour" : "Emprunt";
                String date = loan.getLoanDate().format(dateFormatter);
                String user = loan.getUser().getUsername();
                String book = loan.getBook().getTitle();
                if (loan.isReturned()) {
                    date = loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : date;
                }
                activities.add(String.format("[%s] %s de '%s' par %s", date, action, book, user));
            }
            activityListView.setItems(activities);
        } catch (Exception e) {
            showError("Erreur lors du chargement de l'activité récente", e);
        }
    }
    
    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    
    // Classe interne pour représenter un livre populaire avec son nombre d'emprunts
    public static class PopularBook {
        private String title;
        private String author;
        private int loanCount;
        
        public PopularBook(String title, String author, int loanCount) {
            this.title = title;
            this.author = author;
            this.loanCount = loanCount;
        }
        
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getLoanCount() { return loanCount; }
    }

    @FXML
    private void handleBooksCardClick() {
        MainController.getInstance().showBooksWithFilter("");
    }
    @FXML
    private void handleUsersCardClick() {
        MainController.getInstance().showUsersWithFilter("");
    }
    @FXML
    private void handleLoansCardClick() {
        MainController.getInstance().showLoansWithFilter("En cours");
    }
    @FXML
    private void handleOverdueCardClick() {
        MainController.getInstance().showLoansWithFilter("En retard");
    }
    @FXML
    private void handleAvailableBooksCardClick() {
        MainController.getInstance().showBooksWithFilter("Disponible");
    }
} 