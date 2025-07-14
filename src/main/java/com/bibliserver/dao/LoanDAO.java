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
        String sql = "SELECT * FROM loans WHERE id = ?";
        
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
        String sql = "SELECT * FROM loans WHERE user_id = ?";
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
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE'";
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
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            Loan loan = findById(loanId);
            if (loan != null) {
                pstmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
                pstmt.setInt(2, loanId);
                pstmt.executeUpdate();
                
                // Mettre à jour la quantité disponible du livre
                updateBookAvailability(loan.getBook().getId(), 1);
            }
        }
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

        Book book = bookDAO.findById(bookId);
        User user = userDAO.findById(userId);

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