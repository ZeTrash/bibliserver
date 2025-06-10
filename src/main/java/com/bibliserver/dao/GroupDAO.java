package com.bibliserver.dao;

import com.bibliserver.model.Group;
import com.bibliserver.model.Privilege;
import com.bibliserver.model.Permission;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    public List<Group> findAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM `groups`";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Group group = mapResultSetToGroup(rs);
                groups.add(group);
            }
            
            // Charger les permissions pour chaque groupe après avoir fermé le ResultSet principal
            for (Group group : groups) {
                group.setPermissions(findPermissionsForGroup(group.getId()));
        }
        
        return groups;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { }
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }
    
    public Group findById(int id) throws SQLException {
        String sql = "SELECT * FROM `groups` WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Group group = mapResultSetToGroup(rs);
                    group.setPermissions(findPermissionsForGroup(id));
                    return group;
                }
            }
        }
        
        return null;
    }

    public void create(Group group) throws SQLException {
        String sql = "INSERT INTO `groups` (name, description) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setId(generatedKeys.getInt(1));
            }
            }
            
            // Mise à jour des permissions
            updateGroupPermissions(group);
        }
    }
    
    public void update(Group group) throws SQLException {
        String sql = "UPDATE `groups` SET name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            stmt.setInt(3, group.getId());
            
            stmt.executeUpdate();
            
            // Mise à jour des permissions
            updateGroupPermissions(group);
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `groups` WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private List<Permission> findPermissionsForGroup(int groupId) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = """
            SELECT p.* FROM permissions p
            JOIN group_permissions gp ON p.id = gp.permission_id
            WHERE gp.group_id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapResultSetToPermission(rs));
                }
            }
        }
        
        return permissions;
    }

    private void updateGroupPermissions(Group group) throws SQLException {
        // Supprimer toutes les permissions existantes
        String deleteSql = "DELETE FROM group_permissions WHERE group_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, group.getId());
            stmt.executeUpdate();
        }

        // Ajouter les nouvelles permissions
        String insertSql = "INSERT INTO group_permissions (group_id, permission_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (Permission permission : group.getPermissions()) {
                stmt.setInt(1, group.getId());
                stmt.setInt(2, permission.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private Group mapResultSetToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return group;
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
    
    public List<Privilege> getPrivileges(int groupId) throws SQLException {
        String sql = """
            SELECT p.* FROM permissions p
INNER JOIN group_permissions gp ON p.id = gp.permission_id
            WHERE gp.group_id = ?
        """;
        
        List<Privilege> privileges = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, groupId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                privileges.add(mapResultSetToPrivilege(rs));
            }
        
        return privileges;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { }
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }
    
    public boolean hasPrivilege(int groupId, int privilegeId) {
        String sql = "SELECT 1 FROM group_permissions WHERE group_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, privilegeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
            }
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean hasPrivilege(int groupId, String privilegeName) throws SQLException {
        String sql = "SELECT 1 FROM group_permissions gp " +
"JOIN permissions p ON p.id = gp.permission_id " +
                    "WHERE gp.group_id = ? AND p.name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId);
            pstmt.setString(2, privilegeName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
            }
        }
    }
    
    public void grantPrivilege(int groupId, int privilegeId) throws SQLException {
        String sql = "INSERT INTO group_permissions (group_id, permission_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, privilegeId);
            
            pstmt.executeUpdate();
        }
    }
    
    public void revokePrivilege(int groupId, int privilegeId) throws SQLException {
        String sql = "DELETE FROM group_permissions WHERE group_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, privilegeId);
            
            pstmt.executeUpdate();
        }
    }
    
    private Privilege mapResultSetToPrivilege(ResultSet rs) throws SQLException {
        return new Privilege(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );
    }
} 