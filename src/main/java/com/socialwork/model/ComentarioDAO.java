package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO {

    public Comentario insert(Comentario c) throws SQLException {
        String sql;
        if (c.getPublicacionId() != null) {
            sql = "INSERT INTO comentarios (publicacion_id, usuario_id, contenido) VALUES (?,?,?)";
        } else if (c.getCasoId() != null) {
            sql = "INSERT INTO comentarios (caso_id, usuario_id, contenido) VALUES (?,?,?)";
        } else {
            throw new SQLException("Debe especificar publicacion_id o caso_id");
        }
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (c.getPublicacionId() != null) {
                ps.setInt(1, c.getPublicacionId());
            } else {
                ps.setInt(1, c.getCasoId());
            }
            ps.setInt(2, c.getUsuarioId());
            ps.setString(3, c.getContenido());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getInt(1));
        }
        return c;
    }

    public List<Comentario> findByPublicacion(int publicacionId) throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT com.*, u.nombre AS autor_nombre, u.avatar AS autor_avatar "
                + "FROM comentarios com JOIN usuarios u ON com.usuario_id = u.id "
                + "WHERE com.publicacion_id = ? ORDER BY com.created_at ASC";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, publicacionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Comentario> findByIdAsList(int id) throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT com.*, u.nombre AS autor_nombre, u.avatar AS autor_avatar "
                + "FROM comentarios com JOIN usuarios u ON com.usuario_id = u.id "
                + "WHERE com.id = ? ORDER BY com.created_at ASC";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Comentario> findByCaso(int casoId) throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT com.*, u.nombre AS autor_nombre, u.avatar AS autor_avatar "
                + "FROM comentarios com JOIN usuarios u ON com.usuario_id = u.id "
                + "WHERE com.caso_id = ? ORDER BY com.created_at ASC";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, casoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM comentarios WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Comentario map(ResultSet rs) throws SQLException {
        Comentario c = new Comentario();
        c.setId(rs.getInt("id"));
        int pubId = rs.getInt("publicacion_id");
        if (!rs.wasNull()) c.setPublicacionId(pubId);
        int casoId = rs.getInt("caso_id");
        if (!rs.wasNull()) c.setCasoId(casoId);
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setContenido(rs.getString("contenido"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setAutorNombre(rs.getString("autor_nombre"));
        c.setAutorAvatar(rs.getString("autor_avatar"));
        return c;
    }
}
