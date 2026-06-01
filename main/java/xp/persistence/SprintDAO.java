package main.java.xp.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SprintDAO {

    public int crearSprint(String nombre, String fechaInicio) {
        String sql = "INSERT INTO sprints (nombre, fecha_inicio) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, fechaInicio);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear sprint: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<String[]> listarSprints() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_inicio, fecha_fin FROM sprints ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sprints: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public String[] buscarPorId(int id) {
        String sql = "SELECT id, nombre, fecha_inicio, fecha_fin FROM sprints WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar sprint por id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
