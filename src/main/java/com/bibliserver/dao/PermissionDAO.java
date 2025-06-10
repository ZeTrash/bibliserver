package com.bibliserver.dao;

import com.bibliserver.model.Permission;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {

    public List<Permission> findAll() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                permissions.add(mapResultSetToPermission(rs));
            }
        }
        
        return permissions;
    }

    public Permission findById(int id) throws SQLException {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPermission(rs);
                }
            }
        }
        
        return null;
    }

    public List<Permission> findByResource(String resource) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions WHERE resource = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resource);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapResultSetToPermission(rs));
                }
            }
        }
        
        return permissions;
    }

    public void create(Permission permission) throws SQLException {
        String sql = "INSERT INTO permissions (name, description, resource, action) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getResource());
            stmt.setString(4, permission.getAction());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    permission.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Permission permission) throws SQLException {
        String sql = "UPDATE permissions SET name = ?, description = ?, resource = ?, action = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getResource());
            stmt.setString(4, permission.getAction());
            stmt.setInt(5, permission.getId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setId(rs.getInt("id"));
        permission.setName(rs.getString("name"));
        permission.setDescription(rs.getString("description"));
        permission.setResource(rs.getString("resource"));
        permission.setAction(rs.getString("action"));
        return permission;
    }
} 