package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversacionDAO {

    public Conversacion insert(Conversacion conv) throws SQLException {
        String sql = "INSERT INTO conversaciones () VALUES ()";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) conv.setId(rs.getInt(1));
        }
        return conv;
    }

    public void addParticipante(int conversacionId, int usuarioId) throws SQLException {
        String sql = "INSERT INTO conversacion_participantes (conversacion_id, usuario_id) VALUES (?,?)";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, conversacionId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    public List<Conversacion> findByUsuario(int usuarioId) throws SQLException {
        List<Conversacion> list = new ArrayList<>();
        String sql = "SELECT c.* FROM conversaciones c "
                + "JOIN conversacion_participantes cp ON c.id = cp.conversacion_id "
                + "WHERE cp.usuario_id = ? ORDER BY c.created_at DESC";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Conversacion c = new Conversacion();
                c.setId(rs.getInt("id"));
                c.setCreatedAt(rs.getString("created_at"));
                list.add(c);
            }
        }
        return list;
    }
}
