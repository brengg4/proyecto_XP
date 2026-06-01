package main.java.xp.persistence;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/xp_project";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: No se pudo establecer conexión a la base de datos.");
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnection() {
    }
}
