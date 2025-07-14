package com.bibliserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE = "bibliserver";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection;
    private static boolean useH2 = false;
    
    public static void useTestDatabase() {
        useH2 = true;
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if (useH2) {
                    connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(URL + DATABASE, USER, PASSWORD);
                }
            } catch (SQLException e) {
                // Si la base de données n'existe pas, on se connecte sans la base et on la crée
                if (e.getMessage().contains("inconnue")) {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                } else {
                    throw e;
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL non trouvé", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeSQLFile(String filePath) throws SQLException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] statements = content.split(";");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }
} 