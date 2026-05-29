package com.socialwork.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasoDAO {

    public Caso insert(Caso c) throws SQLException {
        String sql = "INSERT INTO casos (usuario_id, titulo, descripcion, ubicacion, estado) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getUsuarioId());
            ps.setString(2, c.getTitulo());
            ps.setString(3, c.getDescripcion());
            ps.setString(4, c.getUbicacion());
            ps.setString(5, c.getEstado());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getInt(1));
        }
        return c;
    }

    public List<Caso> findAll() throws SQLException {
        List<Caso> list = new ArrayList<>();
        String sql = "SELECT * FROM casos ORDER BY updated_at DESC";
        try (Connection c = ConexionBD.getConnection();
             Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Caso findById(int id) throws SQLException {
        String sql = "SELECT * FROM casos WHERE id=?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean update(Caso c) throws SQLException {
        String sql = "UPDATE casos SET titulo=?, descripcion=?, ubicacion=?, estado=? WHERE id=?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitulo());
            ps.setString(2, c.getDescripcion());
            ps.setString(3, c.getUbicacion());
            ps.setString(4, c.getEstado());
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM casos WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Caso map(ResultSet rs) throws SQLException {
        Caso c = new Caso();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setTitulo(rs.getString("titulo"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setUbicacion(rs.getString("ubicacion"));
        c.setEstado(rs.getString("estado"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setUpdatedAt(rs.getString("updated_at"));
        return c;
    }
}
