package com.bibliserver.dao;

import com.bibliserver.model.User;
import com.bibliserver.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                System.out.println("password_entrée: " + password + " ,pass_in_bdd: " + storedHash + " ,password_hashed: " + BCrypt.hashpw(password, BCrypt.gensalt()));
                return BCrypt.checkpw(password, storedHash);
            }
            
            return false;
        }
    }
    
    public User findById(int id) throws SQLException {
        String sql = "SELECT u.*, g.name as group_name FROM users u LEFT JOIN `groups` g ON u.group_id = g.id WHERE u.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
            return null;
        }
    }
    
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, g.name as group_name FROM users u LEFT JOIN `groups` g ON u.group_id = g.id WHERE u.username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
            return null;
        }
    }
    
    public void createUser(String username, String password, int groupId) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, group_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setInt(3, groupId);
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                // Vous pouvez utiliser cet ID si nécessaire
            }
        }
    }
    
    public List<User> findAll() throws SQLException {
        String sql = "SELECT u.*, g.name as group_name FROM users u LEFT JOIN `groups` g ON u.group_id = g.id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, group_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setInt(2, user.getGroupId());
            pstmt.setInt(3, user.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
        }
    }
    
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setGroupName(rs.getString("group_name"));
        user.setGroupId(rs.getInt("group_id"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
} 