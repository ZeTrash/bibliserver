package com.bibliserver.dao;

import com.bibliserver.model.Media;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    public Media findById(int id) throws SQLException {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMedia(rs);
            }
            return null;
        }
    }

    public List<Media> findByBookId(int bookId) throws SQLException {
        String sql = "SELECT * FROM media WHERE book_id = ?";
        List<Media> medias = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                medias.add(mapResultSetToMedia(rs));
            }
        }
        return medias;
    }

    public List<Media> findIndependents() throws SQLException {
        String sql = "SELECT * FROM media WHERE is_independent = TRUE";
        List<Media> medias = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                medias.add(mapResultSetToMedia(rs));
            }
        }
        return medias;
    }

    public List<Media> findAllWithBook() {
        List<Media> medias = new ArrayList<>();
        String sql = "SELECT m.*, b.title AS book_title FROM media m LEFT JOIN books b ON m.book_id = b.id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Media media = mapResultSetToMedia(rs);
                media.setBookTitle(rs.getString("book_title"));
                medias.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medias;
    }

    public List<Media> findByAssociation(boolean associated) {
        List<Media> medias = new ArrayList<>();
        String sql = associated ?
            "SELECT m.*, b.title AS book_title FROM media m JOIN books b ON m.book_id = b.id" :
            "SELECT m.*, NULL AS book_title FROM media m WHERE m.book_id IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Media media = mapResultSetToMedia(rs);
                media.setBookTitle(rs.getString("book_title"));
                medias.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medias;
    }

    public void create(Media media) throws SQLException {
        String sql = "INSERT INTO media (type, title, description, book_id, is_independent, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, media.getType());
            pstmt.setString(2, media.getTitle());
            pstmt.setString(3, media.getDescription());
            if (media.getBookId() != null) {
                pstmt.setInt(4, media.getBookId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setBoolean(5, media.isIndependent());
            pstmt.setTimestamp(6, Timestamp.valueOf(media.getCreatedAt() != null ? media.getCreatedAt() : LocalDateTime.now()));
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                media.setId(rs.getInt(1));
            }
        }
    }

    public void update(Media media) throws SQLException {
        String sql = "UPDATE media SET type=?, title=?, description=?, book_id=?, is_independent=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, media.getType());
            pstmt.setString(2, media.getTitle());
            pstmt.setString(3, media.getDescription());
            if (media.getBookId() != null) {
                pstmt.setInt(4, media.getBookId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setBoolean(5, media.isIndependent());
            pstmt.setInt(6, media.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media m = new Media();
        m.setId(rs.getInt("id"));
        m.setType(rs.getString("type"));
        m.setTitle(rs.getString("title"));
        m.setDescription(rs.getString("description"));
        int bookId = rs.getInt("book_id");
        m.setBookId(rs.wasNull() ? null : bookId);
        m.setIndependent(rs.getBoolean("is_independent"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) m.setCreatedAt(ts.toLocalDateTime());
        return m;
    }
} 