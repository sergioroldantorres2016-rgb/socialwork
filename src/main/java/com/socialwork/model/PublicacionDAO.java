package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicacionDAO {

    public Publicacion insert(Publicacion p) throws SQLException {
        String sql = "INSERT INTO publicaciones (usuario_id, contenido) VALUES (?,?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUsuarioId());
            ps.setString(2, p.getContenido());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
        }
        return p;
    }

    public List<Publicacion> findAll() throws SQLException {
        List<Publicacion> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS autor_nombre, u.avatar AS autor_avatar "
                + "FROM publicaciones p JOIN usuarios u ON p.usuario_id = u.id ORDER BY p.created_at DESC";
        try (Connection c = ConexionBD.getConnection();
             Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Publicacion findById(int id) throws SQLException {
        String sql = "SELECT p.*, u.nombre AS autor_nombre, u.avatar AS autor_avatar "
                + "FROM publicaciones p JOIN usuarios u ON p.usuario_id = u.id WHERE p.id=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean update(Publicacion p) throws SQLException {
        String sql = "UPDATE publicaciones SET contenido=? WHERE id=? AND usuario_id=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getContenido());
            ps.setInt(2, p.getId());
            ps.setInt(3, p.getUsuarioId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM publicaciones WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Publicacion map(ResultSet rs) throws SQLException {
        Publicacion p = new Publicacion();
        p.setId(rs.getInt("id"));
        p.setUsuarioId(rs.getInt("usuario_id"));
        p.setContenido(rs.getString("contenido"));
        p.setCreatedAt(rs.getString("created_at"));
        p.setAutorNombre(rs.getString("autor_nombre"));
        p.setAutorAvatar(rs.getString("autor_avatar"));
        return p;
    }
}
