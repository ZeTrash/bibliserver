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
    private static final String URL_PARAMS = "?useUnicode=true&characterEncoding=UTF-8";
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
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        // Tente de se connecter à la base cible
                        connection = DriverManager.getConnection(URL + DATABASE + URL_PARAMS, USER, PASSWORD);
                    } catch (SQLException e) {
                        // Si la base n'existe pas, on la crée avec init.sql
                        if (e.getMessage() != null && (e.getMessage().contains("Unknown database") || e.getMessage().contains("inconnue"))) {
                            // Crée la base et l'initialise
                            connection = DriverManager.getConnection(URL, USER, PASSWORD);
                            executeSQLFile("src/main/resources/sql/init.sql");
                            // Re-tente la connexion à la base cible
                            connection = DriverManager.getConnection(URL + DATABASE + URL_PARAMS, USER, PASSWORD);
                        } else {
                            throw e;
                        }
                    }
                }
            } catch (SQLException e) {
                throw e;
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL non trouvé", e);
            } catch (Exception e) {
                throw new SQLException("Erreur lors de l'initialisation de la base de données", e);
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

    public static void executeSQLFile(String filePath) throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get(filePath)), java.nio.charset.StandardCharsets.UTF_8);
        try (Connection conn = connection != null ? connection : DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
} 