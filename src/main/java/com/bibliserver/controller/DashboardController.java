package com.bibliserver.controller;

import com.bibliserver.dao.BookDAO;
import com.bibliserver.dao.LoanDAO;
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
    
    private BookDAO bookDAO;
    private LoanDAO loanDAO;
    private DateTimeFormatter dateFormatter;
    
    public void initialize() {
        bookDAO = new BookDAO();
        loanDAO = new LoanDAO();
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        setupTableColumns();
        loadStatistics();
        loadRecentLoans();
        loadPopularBooks();
    }
    
    private void setupTableColumns() {
        // Configuration des colonnes pour les emprunts récents
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
        
        loanDateColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getLoanDate().format(dateFormatter)
            )
        );
        
        dueDateColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getDueDate().format(dateFormatter)
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
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Afficher une alerte d'erreur
        }
    }
    
    private void loadRecentLoans() {
        try {
            ObservableList<Loan> recentLoans = FXCollections.observableArrayList(
                loanDAO.findActiveLoans()
            );
            recentLoansTable.setItems(recentLoans);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Afficher une alerte d'erreur
        }
    }
    
    private void loadPopularBooks() {
        // TODO: Implémenter le chargement des livres populaires
        // Cette fonctionnalité nécessite une requête SQL plus complexe
        // qui compte le nombre d'emprunts par livre
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
} 