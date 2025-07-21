package com.bibliserver.dao;

import com.bibliserver.model.MemoireDetails;
import com.bibliserver.util.DatabaseUtil;
import java.sql.*;

public class MemoireDetailsDAO {
    public MemoireDetails findByBookId(int bookId) throws SQLException {
        String sql = "SELECT * FROM memoire_details WHERE book_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMemoireDetails(rs);
            }
            return null;
        }
    }

    public void create(MemoireDetails memoire) throws SQLException {
        String sql = "INSERT INTO memoire_details (book_id, university, supervisor, year, subject) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, memoire.getBookId());
            pstmt.setString(2, memoire.getUniversity());
            pstmt.setString(3, memoire.getSupervisor());
            pstmt.setInt(4, memoire.getYear());
            pstmt.setString(5, memoire.getSubject());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                memoire.setId(rs.getInt(1));
            }
        }
    }

    public void update(MemoireDetails memoire) throws SQLException {
        String sql = "UPDATE memoire_details SET university=?, supervisor=?, year=?, subject=? WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memoire.getUniversity());
            pstmt.setString(2, memoire.getSupervisor());
            pstmt.setInt(3, memoire.getYear());
            pstmt.setString(4, memoire.getSubject());
            pstmt.setInt(5, memoire.getBookId());
            pstmt.executeUpdate();
        }
    }

    public void deleteByBookId(int bookId) throws SQLException {
        String sql = "DELETE FROM memoire_details WHERE book_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    private MemoireDetails mapResultSetToMemoireDetails(ResultSet rs) throws SQLException {
        MemoireDetails m = new MemoireDetails();
        m.setId(rs.getInt("id"));
        m.setBookId(rs.getInt("book_id"));
        m.setUniversity(rs.getString("university"));
        m.setSupervisor(rs.getString("supervisor"));
        m.setYear(rs.getInt("year"));
        m.setSubject(rs.getString("subject"));
        return m;
    }
} 