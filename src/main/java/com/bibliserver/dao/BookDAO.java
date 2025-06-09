package com.bibliserver.dao;

import com.bibliserver.model.Book;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    public void create(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, publication_year, publisher, quantity, available_quantity) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setString(5, book.getPublisher());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setInt(7, book.getAvailableQuantity());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                book.setId(rs.getInt(1));
            }
        }
    }
    
    public Book findById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            
            return null;
        }
    }
    
    public List<Book> findAll() throws SQLException {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        
        return books;
    }
    
    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publication_year = ?, " +
                    "publisher = ?, quantity = ?, available_quantity = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setString(5, book.getPublisher());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setInt(7, book.getAvailableQuantity());
            pstmt.setInt(8, book.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public List<Book> searchBooks(String query) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        
        return books;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setPublisher(rs.getString("publisher"));
        book.setQuantity(rs.getInt("quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        book.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return book;
    }
} 