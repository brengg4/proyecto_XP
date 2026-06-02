package main.java.xp.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SesionPairingDAO {

    public int crearSesion(int pilotoId, int copilotoId, String fecha) {
        String sql = "INSERT INTO sesiones_pairing (piloto_id, copiloto_id, fecha, estado) VALUES (?, ?, ?, 'ACTIVA')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pilotoId);
            ps.setInt(2, copilotoId);
            ps.setString(3, fecha);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear sesión de pairing: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void registrarAporte(int sesionId, String componente, String codigo, String autor) {
        String sql = "INSERT INTO aportes (sesion_id, componente, codigo, autor, timestamp) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sesionId);
            ps.setString(2, componente);
            ps.setString(3, codigo);
            ps.setString(4, autor);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar aporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void registrarIntercambioRoles(int sesionId, String pilotoAnt, String copilotoAnt, String pilotoNuevo, String copilotoNuevo) {
        String sql = "INSERT INTO historial_roles (sesion_id, piloto_anterior, copiloto_anterior, piloto_nuevo, copiloto_nuevo, timestamp) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sesionId);
            ps.setString(2, pilotoAnt);
            ps.setString(3, copilotoAnt);
            ps.setString(4, pilotoNuevo);
            ps.setString(5, copilotoNuevo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar intercambio de roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String[]> listarSesiones() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT s.id, s.fecha, p.nombre AS piloto_nombre, c.nombre AS copiloto_nombre, s.estado " +
                     "FROM sesiones_pairing s " +
                     "JOIN desarrolladores p ON s.piloto_id = p.id " +
                     "JOIN desarrolladores c ON s.copiloto_id = c.id " +
                     "ORDER BY s.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("fecha"),
                    rs.getString("piloto_nombre"),
                    rs.getString("copiloto_nombre"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sesiones: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<String[]> listarAportes(int sesionId) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, componente, codigo, autor, timestamp FROM aportes WHERE sesion_id = ? ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sesionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("componente"),
                        rs.getString("codigo"),
                        rs.getString("autor"),
                        rs.getString("timestamp")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar aportes: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public void finalizarSesion(int sesionId) {
        String sql = "UPDATE sesiones_pairing SET estado = 'FINALIZADA' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sesionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al finalizar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
