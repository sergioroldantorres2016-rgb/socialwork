package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    public Mensaje insert(Mensaje m) throws SQLException {
        String sql = "INSERT INTO mensajes (conversacion_id, remitente_id, contenido) VALUES (?,?,?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getConversacionId());
            ps.setInt(2, m.getRemitenteId());
            ps.setString(3, m.getContenido());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) m.setId(rs.getInt(1));
        }
        return m;
    }

    public List<Mensaje> findByConversacion(int conversacionId) throws SQLException {
        List<Mensaje> list = new ArrayList<>();
        String sql = "SELECT m.*, u.nombre AS remitente_nombre, u.avatar AS remitente_avatar "
                + "FROM mensajes m JOIN usuarios u ON m.remitente_id = u.id "
                + "WHERE m.conversacion_id = ? ORDER BY m.created_at ASC";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, conversacionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Mensaje findUltimoByConversacion(int conversacionId) throws SQLException {
        String sql = "SELECT m.*, u.nombre AS remitente_nombre, u.avatar AS remitente_avatar "
                + "FROM mensajes m JOIN usuarios u ON m.remitente_id = u.id "
                + "WHERE m.conversacion_id = ? ORDER BY m.created_at DESC LIMIT 1";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, conversacionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    private Mensaje map(ResultSet rs) throws SQLException {
        Mensaje m = new Mensaje();
        m.setId(rs.getInt("id"));
        m.setConversacionId(rs.getInt("conversacion_id"));
        m.setRemitenteId(rs.getInt("remitente_id"));
        m.setContenido(rs.getString("contenido"));
        m.setCreatedAt(rs.getString("created_at"));
        m.setRemitenteNombre(rs.getString("remitente_nombre"));
        m.setRemitenteAvatar(rs.getString("remitente_avatar"));
        return m;
    }
}
