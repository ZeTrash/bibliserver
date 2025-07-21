package com.bibliserver.dao;

import com.bibliserver.model.Loan;
import com.bibliserver.model.Book;
import com.bibliserver.model.User;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private BookDAO bookDAO;
    private UserDAO userDAO;
    
    public LoanDAO() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    public void create(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_id, user_id, loan_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, loan.getBook().getId());
            pstmt.setInt(2, loan.getUser().getId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
            pstmt.setString(5, loan.getStatus());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                loan.setId(rs.getInt(1));
            }
            
            // Mettre à jour la quantité disponible du livre
            updateBookAvailability(loan.getBook().getId(), -1);
        }
    }
    
    public Loan findById(int id) throws SQLException {
        String sql = "SELECT l.*, b.id as b_id, b.title as b_title, b.author as b_author, b.isbn as b_isbn, b.publication_year as b_publication_year, b.publisher as b_publisher, b.quantity as b_quantity, b.available_quantity as b_available_quantity, b.created_at as b_created_at, " +
                "u.id as u_id, u.username as u_username, u.password_hash as u_password_hash, u.group_id as u_group_id, u.created_at as u_created_at, u2.name as u_group_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN users u ON l.user_id = u.id " +
                "LEFT JOIN `groups` u2 ON u.group_id = u2.id " +
                "WHERE l.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
            return null;
        }
    }

    public List<Loan> findByUser(int userId) throws SQLException {
        String sql = "SELECT l.*, b.id as b_id, b.title as b_title, b.author as b_author, b.isbn as b_isbn, b.publication_year as b_publication_year, b.publisher as b_publisher, b.quantity as b_quantity, b.available_quantity as b_available_quantity, b.created_at as b_created_at, " +
                "u.id as u_id, u.username as u_username, u.password_hash as u_password_hash, u.group_id as u_group_id, u.created_at as u_created_at, u2.name as u_group_name " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN users u ON l.user_id = u.id " +
                "LEFT JOIN `groups` u2 ON u.group_id = u2.id " +
                "WHERE l.user_id = ?";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }

    public List<Loan> findActiveLoans() throws SQLException {
        String sql = "SELECT l.*, b.id as b_id, b.title as b_title, b.author as b_author, b.isbn as b_isbn, b.publication_year as b_publication_year, b.publisher as b_publisher, b.quantity as b_quantity, b.available_quantity as b_available_quantity, b.created_at as b_created_at, " +
                "u.id as u_id, u.username as u_username, u.password_hash as u_password_hash, u.group_id as u_group_id, u.created_at as u_created_at, u2.name as u_group_name " +
                "FROM loans l " +
                "LEFT JOIN books b ON l.book_id = b.id " +
                "LEFT JOIN users u ON l.user_id = u.id " +
                "LEFT JOIN `groups` u2 ON u.group_id = u2.id " +
                "WHERE l.status = 'ACTIVE'";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }
    
    public void returnBook(int loanId) throws SQLException {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = ? WHERE id = ?";
        Loan loan = findById(loanId); // Récupérer le prêt AVANT d'ouvrir le PreparedStatement
        if (loan != null) {
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
                pstmt.setInt(2, loanId);
                pstmt.executeUpdate();
                // Mettre à jour la quantité disponible du livre
                updateBookAvailability(loan.getBook().getId(), 1);
            }
        }
    }
    
    public List<Loan> findRecentLoans(int n) throws SQLException {
        String sql = "SELECT l.*, b.id as b_id, b.title as b_title, b.author as b_author, b.isbn as b_isbn, b.publication_year as b_publication_year, b.publisher as b_publisher, b.quantity as b_quantity, b.available_quantity as b_available_quantity, b.created_at as b_created_at, " +
                "u.id as u_id, u.username as u_username, u.password_hash as u_password_hash, u.group_id as u_group_id, u.created_at as u_created_at, u2.name as u_group_name " +
                "FROM loans l " +
                "LEFT JOIN books b ON l.book_id = b.id " +
                "LEFT JOIN users u ON l.user_id = u.id " +
                "LEFT JOIN `groups` u2 ON u.group_id = u2.id " +
                "ORDER BY COALESCE(l.return_date, l.loan_date) DESC LIMIT ?";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }
    
    public List<Loan> findAll() throws SQLException {
        String sql = "SELECT l.*, b.id as b_id, b.title as b_title, b.author as b_author, b.isbn as b_isbn, b.publication_year as b_publication_year, b.publisher as b_publisher, b.quantity as b_quantity, b.available_quantity as b_available_quantity, b.created_at as b_created_at, " +
                "u.id as u_id, u.username as u_username, u.password_hash as u_password_hash, u.group_id as u_group_id, u.created_at as u_created_at, u2.name as u_group_name " +
                "FROM loans l " +
                "LEFT JOIN books b ON l.book_id = b.id " +
                "LEFT JOIN users u ON l.user_id = u.id " +
                "LEFT JOIN `groups` u2 ON u.group_id = u2.id ";
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        }
        return loans;
    }
    
    private void updateBookAvailability(int bookId, int delta) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity + ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, delta);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }
    
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int bookId = rs.getInt("book_id");
        int userId = rs.getInt("user_id");
        LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
        Date returnDateSql = rs.getDate("return_date");
        String status = rs.getString("status");

        // Mapping Book
        Book book = new Book();
        if (rs.getObject("b_id") != null) {
            book.setId(rs.getInt("b_id"));
            book.setTitle(rs.getString("b_title"));
            book.setAuthor(rs.getString("b_author"));
            book.setIsbn(rs.getString("b_isbn"));
            book.setPublicationYear(rs.getInt("b_publication_year"));
            book.setPublisher(rs.getString("b_publisher"));
            book.setQuantity(rs.getInt("b_quantity"));
            book.setAvailableQuantity(rs.getInt("b_available_quantity"));
            book.setCreatedAt(rs.getTimestamp("b_created_at").toLocalDateTime());
        } else {
            book.setTitle("[Livre supprimé]");
        }

        // Mapping User
        User user = new User();
        if (rs.getObject("u_id") != null) {
            user.setId(rs.getInt("u_id"));
            user.setUsername(rs.getString("u_username"));
            user.setPasswordHash(rs.getString("u_password_hash"));
            user.setGroupId(rs.getInt("u_group_id"));
            user.setCreatedAt(rs.getTimestamp("u_created_at").toLocalDateTime());
            user.setGroupName(rs.getString("u_group_name"));
        } else {
            user.setUsername("[Utilisateur supprimé]");
        }

        Loan loan = new Loan();
        loan.setId(id);
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(loanDate);
        loan.setDueDate(dueDate);
        if (returnDateSql != null) {
            loan.setReturnDate(returnDateSql.toLocalDate());
        }
        loan.setStatus(status);
        return loan;
    }
} 