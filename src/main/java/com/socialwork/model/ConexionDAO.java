package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConexionDAO {

    public Conexion insert(Conexion cx) throws SQLException {
        String sql = "INSERT INTO conexiones (seguidor_id, seguido_id) VALUES (?,?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cx.getSeguidorId());
            ps.setInt(2, cx.getSeguidoId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) cx.setId(rs.getInt(1));
        }
        return cx;
    }

    public List<Conexion> findConexiones(int usuarioId) throws SQLException {
        List<Conexion> list = new ArrayList<>();
        String sql = "SELECT cx.*, u.nombre AS seguido_nombre, u.avatar AS seguido_avatar, u.rol AS seguido_rol "
                + "FROM conexiones cx JOIN usuarios u ON cx.seguido_id = u.id WHERE cx.seguidor_id = ? "
                + "ORDER BY u.nombre";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Conexion cx = new Conexion();
                cx.setId(rs.getInt("id"));
                cx.setSeguidorId(rs.getInt("seguidor_id"));
                cx.setSeguidoId(rs.getInt("seguido_id"));
                cx.setCreatedAt(rs.getString("created_at"));
                cx.setSeguidoNombre(rs.getString("seguido_nombre"));
                cx.setSeguidoAvatar(rs.getString("seguido_avatar"));
                cx.setSeguidoRol(rs.getString("seguido_rol"));
                list.add(cx);
            }
        }
        return list;
    }

    public boolean delete(int seguidorId, int seguidoId) throws SQLException {
        String sql = "DELETE FROM conexiones WHERE seguidor_id=? AND seguido_id=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, seguidorId);
            ps.setInt(2, seguidoId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countConexiones(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM conexiones WHERE seguidor_id=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
