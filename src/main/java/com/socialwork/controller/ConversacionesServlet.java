package com.socialwork.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socialwork.model.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/conversaciones/*")
public class ConversacionesServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final ConversacionDAO conversacionDAO = new ConversacionDAO();
    private final MensajeDAO mensajeDAO = new MensajeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int uid = (int) req.getSession().getAttribute("usuarioId");
            List<Conversacion> conversaciones = conversacionDAO.findByUsuario(uid);
            List<Conversacion> result = new ArrayList<>();

            for (Conversacion c : conversaciones) {
                List<Conversacion.ParticipanteInfo> participantes = getParticipantes(c.getId(), uid);
                c.setParticipantes(participantes);
                c.setUltimoMensaje(mensajeDAO.findUltimoByConversacion(c.getId()));
                result.add(c);
            }
            escribirJson(resp, 200, gson.toJson(result));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);

            int uid = (int) req.getSession().getAttribute("usuarioId");
            int otroUsuarioId = body.get("usuario_id").getAsInt();

            List<Conversacion> existentes = conversacionDAO.findByUsuario(uid);
            for (Conversacion c : existentes) {
                List<Conversacion.ParticipanteInfo> participantes = getParticipantes(c.getId(), uid);
                if (participantes.size() == 1 && participantes.get(0).getUsuarioId() == otroUsuarioId) {
                    escribirJson(resp, 200, gson.toJson(c));
                    return;
                }
            }

            Conversacion conv = new Conversacion();
            conv = conversacionDAO.insert(conv);
            conversacionDAO.addParticipante(conv.getId(), uid);
            conversacionDAO.addParticipante(conv.getId(), otroUsuarioId);
            List<Conversacion.ParticipanteInfo> participantes = getParticipantes(conv.getId(), uid);
            conv.setParticipantes(participantes);
            escribirJson(resp, 201, gson.toJson(conv));
        } catch (Exception e) {
            escribirJson(resp, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private List<Conversacion.ParticipanteInfo> getParticipantes(int convId, int excludeUid) throws Exception {
        List<Conversacion.ParticipanteInfo> list = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.avatar FROM conversacion_participantes cp "
                + "JOIN usuarios u ON cp.usuario_id = u.id "
                + "WHERE cp.conversacion_id = ? AND cp.usuario_id != ?";
        try (java.sql.Connection c = ConexionBD.getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, convId);
            ps.setInt(2, excludeUid);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Conversacion.ParticipanteInfo p = new Conversacion.ParticipanteInfo();
                p.setUsuarioId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setAvatar(rs.getString("avatar"));
                list.add(p);
            }
        }
        return list;
    }

    private void escribirJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(json);
    }
}
