package com.bibliserver.dao;

import com.bibliserver.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CleanDatabaseUtil {
    public static void clean() throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM loans");
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM books");
            stmt.execute("DELETE FROM `groups`");
        }
    }
} 