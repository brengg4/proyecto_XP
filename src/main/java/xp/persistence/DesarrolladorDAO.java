package main.java.xp.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DesarrolladorDAO {

    public void registrar(String nombre) {
        String sql = "INSERT INTO desarrolladores (nombre, fecha_registro) VALUES (?, CURDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar desarrollador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String[]> listarTodos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_registro FROM desarrolladores ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("fecha_registro")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar desarrolladores: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean existeNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM desarrolladores WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de desarrollador: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String[] buscarPorId(int id) {
        String sql = "SELECT id, nombre, fecha_registro FROM desarrolladores WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_registro")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar desarrollador por id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id FROM desarrolladores WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener id por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}
