package main.java.xp.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    public int crearTarea(int sprintId, String nombre, double horasEstimadas, String devAsignado) {
        String sql = "INSERT INTO tareas (sprint_id, nombre, horas_estimadas, tiempo_invertido, estado_cronometro, completada, dev_asignado) " +
                     "VALUES (?, ?, ?, 0.0, 'PENDIENTE', 0, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sprintId);
            ps.setString(2, nombre);
            ps.setDouble(3, horasEstimadas);
            ps.setString(4, devAsignado);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear tarea: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void actualizarEstadoCronometro(int tareaId, String estado) {
        String sql = "UPDATE tareas SET estado_cronometro = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, tareaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del cronómetro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void registrarTiempo(int tareaId, double horas) {
        String sql = "UPDATE tareas SET tiempo_invertido = tiempo_invertido + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, horas);
            ps.setInt(2, tareaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar tiempo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cerrarTarea(int tareaId) {
        String sql = "UPDATE tareas SET completada = 1, estado_cronometro = 'DETENIDO' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tareaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al cerrar tarea: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String[]> listarTareasPorSprint(int sprintId) {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, horas_estimadas, tiempo_invertido, estado_cronometro, completada, dev_asignado " +
                     "FROM tareas WHERE sprint_id = ? ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sprintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getDouble("horas_estimadas")),
                        String.valueOf(rs.getDouble("tiempo_invertido")),
                        rs.getString("estado_cronometro"),
                        String.valueOf(rs.getInt("completada")),
                        rs.getString("dev_asignado")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tareas por sprint: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public String[] buscarPorId(int tareaId) {
        String sql = "SELECT id, nombre, horas_estimadas, tiempo_invertido, estado_cronometro, completada, dev_asignado " +
                     "FROM tareas WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tareaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getDouble("horas_estimadas")),
                        String.valueOf(rs.getDouble("tiempo_invertido")),
                        rs.getString("estado_cronometro"),
                        String.valueOf(rs.getInt("completada")),
                        rs.getString("dev_asignado")
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar tarea por id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
